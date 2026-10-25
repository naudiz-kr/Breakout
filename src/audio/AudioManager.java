package audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class AudioManager {
    private static final float SAMPLE_RATE = 22_050;
    private static final int MAX_OPEN_CLIPS = 3;
    private static final Map<SoundId, Clip> CLIPS =
            new LinkedHashMap<>(MAX_OPEN_CLIPS, 0.75f, true);
    private static final Map<SoundId, Path> SOUND_FILES = new EnumMap<>(SoundId.class);
    private static final Set<SoundId> FAILED_SOUNDS = EnumSet.noneOf(SoundId.class);
    private static final ExecutorService PLAYER = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "breakout-audio");
        thread.setDaemon(true);
        return thread;
    });
    private static final ExecutorService PROCESS_MONITOR = Executors.newCachedThreadPool(task -> {
        Thread thread = new Thread(task, "breakout-audio-monitor");
        thread.setDaemon(true);
        return thread;
    });
    private static final boolean USE_PAPLAY = isLinux() && isPaplayAvailable();

    private AudioManager() { }

    public static void play(SoundId sound) {
        PLAYER.execute(() -> playOnAudioThread(sound));
    }

    private static void playOnAudioThread(SoundId sound) {
        if (FAILED_SOUNDS.contains(sound)) return;

        if (USE_PAPLAY) {
            playThroughDesktopAudio(sound);
            return;
        }

        try {
            Clip clip = CLIPS.get(sound);
            if (clip == null) {
                if (CLIPS.size() >= MAX_OPEN_CLIPS) {
                    Iterator<Clip> clips = CLIPS.values().iterator();
                    Clip oldest = clips.next();
                    oldest.stop();
                    oldest.close();
                    clips.remove();
                }
                clip = createClip(sound);
                CLIPS.put(sound, clip);
            }
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException
                 | IllegalArgumentException exception) {
            FAILED_SOUNDS.add(sound);
            System.err.println("Unable to play " + sound + ": " + exception.getMessage());
        }
    }

    private static void playThroughDesktopAudio(SoundId sound) {
        try {
            Path file = soundFileForPlayback(sound);
            Process process = new ProcessBuilder("paplay", file.toAbsolutePath().toString())
                    .redirectErrorStream(true)
                    .start();
            PROCESS_MONITOR.execute(() -> reportProcessResult(process, sound));
        } catch (IOException exception) {
            FAILED_SOUNDS.add(sound);
            System.err.println("Unable to play " + sound + " with paplay: " + exception.getMessage());
        }
    }

    private static Path soundFileForPlayback(SoundId sound) throws IOException {
        Path file = SOUND_FILES.get(sound);
        if (file != null) return file;

        try {
            file = resolveSoundFile(sound);
        } catch (FileNotFoundException exception) {
            file = Files.createTempFile("breakout-" + sound.name().toLowerCase(), ".wav");
            try (AudioInputStream stream = generatedSound(sound)) {
                AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file.toFile());
            }
            file.toFile().deleteOnExit();
        }
        SOUND_FILES.put(sound, file);
        return file;
    }

    private static void reportProcessResult(Process process, SoundId sound) {
        try {
            byte[] output = process.getInputStream().readAllBytes();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                String detail = new String(output).trim();
                System.err.println("paplay failed for " + sound + " (exit " + exitCode + ")"
                        + (detail.isEmpty() ? "" : ": " + detail));
            }
        } catch (IOException exception) {
            System.err.println("Unable to read paplay output for " + sound + ": "
                    + exception.getMessage());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private static Clip createClip(SoundId sound)
            throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        AudioInputStream stream;
        try {
            stream = AudioSystem.getAudioInputStream(resolveSoundFile(sound).toFile());
        } catch (FileNotFoundException exception) {
            stream = generatedSound(sound);
        }

        byte[] samples;
        AudioFormat playbackFormat;
        try (AudioInputStream source = stream) {
            AudioFormat sourceFormat = source.getFormat();
            playbackFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    sourceFormat.getSampleRate(),
                    16,
                    sourceFormat.getChannels(),
                    sourceFormat.getChannels() * 2,
                    sourceFormat.getSampleRate(),
                    false);
            try (AudioInputStream converted =
                         AudioSystem.getAudioInputStream(playbackFormat, source)) {
                samples = converted.readAllBytes();
            }
        }

        DataLine.Info lineInfo = new DataLine.Info(Clip.class, playbackFormat);
        LineUnavailableException lastFailure = null;
        Mixer defaultMixer = AudioSystem.getMixer(null);
        if (defaultMixer.isLineSupported(lineInfo)) {
            try {
                return openClip(defaultMixer, lineInfo, samples, playbackFormat);
            } catch (LineUnavailableException | IllegalArgumentException exception) {
                lastFailure = new LineUnavailableException(exception.getMessage());
            }
        }
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            if (mixer.equals(defaultMixer) || !mixer.isLineSupported(lineInfo)) continue;
            try {
                return openClip(mixer, lineInfo, samples, playbackFormat);
            } catch (LineUnavailableException | IllegalArgumentException exception) {
                lastFailure = new LineUnavailableException(exception.getMessage());
            }
        }
        if (lastFailure != null) throw lastFailure;
        throw new LineUnavailableException("No audio output mixer supports " + playbackFormat);
    }

    private static Clip openClip(Mixer mixer, DataLine.Info lineInfo, byte[] samples,
                                 AudioFormat format)
            throws LineUnavailableException, IOException {
        Clip clip = (Clip) mixer.getLine(lineInfo);
        try (AudioInputStream stream = new AudioInputStream(
                new ByteArrayInputStream(samples), format, samples.length / format.getFrameSize())) {
            clip.open(stream);
            return clip;
        } catch (LineUnavailableException | IOException | IllegalArgumentException exception) {
            clip.close();
            throw exception;
        }
    }

    private static AudioInputStream generatedSound(SoundId sound) {
        int sampleCount = Math.round(SAMPLE_RATE * sound.durationMillis() / 1000f);
        byte[] samples = new byte[sampleCount * 2];
        double duration = sound.durationMillis() / 1000.0;
        for (int i = 0; i < sampleCount; i++) {
            double progress = (double) i / sampleCount;
            double time = (double) i / SAMPLE_RATE;
            double phase = 2 * Math.PI * (sound.startFrequency() * time
                    + (sound.endFrequency() - sound.startFrequency()) * time * time / (2 * duration));
            double envelope = Math.min(1, time / 0.006) * Math.pow(1 - progress, 2);
            double value = Math.sin(phase) * envelope * 0.72;
            if (sound == SoundId.BRICK_BREAK) value += Math.sin(i * 1.73) * envelope * 0.18;
            short sample = (short) (Math.max(-1, Math.min(1, value)) * Short.MAX_VALUE);
            samples[i * 2] = (byte) sample;
            samples[i * 2 + 1] = (byte) (sample >> 8);
        }

        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        return new AudioInputStream(new ByteArrayInputStream(samples), format, sampleCount);
    }

    private static Path resolveSoundFile(SoundId sound) throws IOException {
        String[] resourcePaths = {
                "/sounds/" + sound.fileName(),
                "/resources/sounds/" + sound.fileName()
        };
        for (String resourcePath : resourcePaths) {
            var resource = AudioManager.class.getResource(resourcePath);
            if (resource == null) continue;
            if (resource.getProtocol().equals("file")) {
                try {
                    return Path.of(resource.toURI());
                } catch (URISyntaxException exception) {
                    throw new IOException("Invalid sound resource URL: " + resource, exception);
                }
            }
            Path extracted = Files.createTempFile("breakout-" + sound.name().toLowerCase(), ".wav");
            try (var input = resource.openStream()) {
                Files.copy(input, extracted, StandardCopyOption.REPLACE_EXISTING);
            }
            extracted.toFile().deleteOnExit();
            return extracted;
        }

        Path[] filePaths = {
                Path.of("resources", "sounds", sound.fileName()),
                Path.of("src", "resources", "sounds", sound.fileName())
        };
        for (Path file : filePaths) {
            if (Files.isRegularFile(file)) return file;
        }
        throw new FileNotFoundException("Missing " + sound.fileName()
                + " in resources/sounds, src/resources/sounds, or the classpath");
    }

    private static boolean isLinux() {
        return System.getProperty("os.name", "").toLowerCase().contains("linux");
    }

    private static boolean isPaplayAvailable() {
        try {
            Process check = new ProcessBuilder("paplay", "--version")
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();
            if (!check.waitFor(1, TimeUnit.SECONDS)) {
                check.destroyForcibly();
                return false;
            }
            return check.exitValue() == 0;
        } catch (IOException exception) {
            return false;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
