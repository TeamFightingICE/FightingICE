package fighting;

import image.Image;

public class Effect {
	protected static final int FRAME_PER_IMAGE = 5;

	protected Attack attack;
	protected Image[] hitImages;
	protected int currentFrame;
	protected int framesPerImage;

	public Effect(Attack attack, Image[] hitImages, int framesPerImage) {
		this.attack = attack;
		this.hitImages = hitImages;
		this.currentFrame = 0;
		this.framesPerImage = framesPerImage;
	}

	public Effect(Attack attack, Image[] hitImages) {
		this(attack, hitImages, FRAME_PER_IMAGE);
	}

	/**
	 *
	 * Updates the effect's state.
	 *
	 * @return a boolean which indicate the if the effects is finish or not.
	 */
	public boolean update() {
		return ++this.currentFrame < (this.hitImages.length * this.framesPerImage);
	}

	/**
	 *
	 * Returns effect's image.
	 *
	 * @return The effect's image.
	 */
	public Image getImage() {
		return this.hitImages[(this.currentFrame / this.framesPerImage) % this.hitImages.length];
	}

	/**
	 *
	 * Returns effect's image.
	 *
	 * @return The effect's image.
	 */
	public Image[] getImages() {
		return this.hitImages;
	}

	/**
	 *
	 * Returns attack related to this effect.
	 *
	 * @return The attack related to this effect.
	 */
	public Attack getAttack() {
		return this.attack;
	}

}
