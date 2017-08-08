package gamescene;

import manager.GraphicManager;
import manager.InputManager;
import manager.SoundManager;

public abstract class GameScene {

	abstract public void initialize(GraphicManager gm,SoundManager sm,InputManager<?> im);

	abstract public void update(GraphicManager gm,SoundManager sm,InputManager<?> im);

	abstract public void close(GraphicManager gm,SoundManager sm,InputManager<?> im);

}
