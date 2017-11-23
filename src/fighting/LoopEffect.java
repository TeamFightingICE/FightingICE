package fighting;

import image.Image;

public class LoopEffect extends Effect {

	public LoopEffect(Attack attack, Image[] hitImages) {
		super(attack, hitImages);
	}

	/**
	 *
	 * Update the effect's state.
	 *
	 */
	public boolean update() {
		if (!super.update()) {
			this.currentFrame = 0;
		}
		return true;
	}

}
