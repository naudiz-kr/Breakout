package core;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class InputHandler extends KeyAdapter {
    private final Set<Integer> down = new HashSet<>();
    private final Set<Integer> pressedThisFrame = new HashSet<>();
    private final StringBuilder typed = new StringBuilder();

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (!Character.isISOControl(c)) typed.append(c);
    }

    public String consumeTyped() {
        String s = typed.toString();
        typed.setLength(0);
        return s;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (down.add(e.getKeyCode()))
            pressedThisFrame.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        down.remove(e.getKeyCode());
    }
   public boolean isDown(int keyCode) {
        return down.contains(keyCode);
   }
   public boolean wasPressed(int keyCode) {
        return pressedThisFrame.contains(keyCode);
   }
   void endFrame() {
        pressedThisFrame.clear();
   }
}