package core;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class InputHandler extends KeyAdapter {
    private final Set<Integer> down = new HashSet<>();
    private final Set<Integer> pressedThisFrame = new HashSet<>();

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