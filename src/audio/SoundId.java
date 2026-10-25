package audio;

public enum SoundId {
    PADDLE_BOUNCE("paddle-bounce.wav", 520, 760, 85),
    WALL_BOUNCE("wall-bounce.wav", 460, 320, 55),
    BRICK_BREAK("brick-break.wav", 880, 220, 125),
    PICKUP("pickup.wav", 430, 960, 170),
    LIFE_LOST("life-lost.wav", 520, 130, 300);

    private final String fileName;
    private final double startFrequency;
    private final double endFrequency;
    private final int durationMillis;

    SoundId(String fileName, double startFrequency, double endFrequency, int durationMillis) {
        this.fileName = fileName;
        this.startFrequency = startFrequency;
        this.endFrequency = endFrequency;
        this.durationMillis = durationMillis;
    }

    String fileName() {
        return fileName;
    }

    double startFrequency() {
        return startFrequency;
    }

    double endFrequency() {
        return endFrequency;
    }

    int durationMillis() {
        return durationMillis;
    }
}
