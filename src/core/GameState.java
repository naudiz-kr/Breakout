package core;

import java.awt.Graphics2D;

public interface GameState {
    void enter();
    void exit();
    void update(double dt);
    void render(Graphics2D g);
}
/* this handles each frame/input/entity.
  enter runs when a certain object is active
  exit runs when a certain object becomes inactive
  update advances logic by a frame/second/ms idk
  render basically handles each frame
 */