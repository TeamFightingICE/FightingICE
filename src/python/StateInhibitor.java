package python;

import manager.GraphicManager;
import manager.InputManager;
import manager.SoundManager;

public interface StateInhibitor {

	void update(GraphicManager gm, SoundManager sm, InputManager<?> im);

}
