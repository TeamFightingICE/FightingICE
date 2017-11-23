package fighting;

import image.Image;

public class HitEffect extends Effect {
	/**
	 * a boolean value which indicate if a hit is active or not.
	 */
	private boolean isHit;

	/**
	 * Horizontal variation.
	 */
	private int variationX;

	/**
	 * Vertical variation.
	 */
	private int variationY;

	public HitEffect(Attack attack, Image[] hitImages, boolean isActive, boolean variation, int framesPerImage) {
		super(attack, hitImages, framesPerImage);
		this.initialize(isActive, variation);
	}

	public HitEffect(Attack attack, Image[] hitImages, boolean isActive, boolean variation) {
		super(attack, hitImages);
		this.initialize(isActive, variation);
	}

	public HitEffect(Attack attack, Image[] hitImages, boolean isActive) {
		this(attack, hitImages, isActive, true);
	}

	/**
	 *
	 * Initialize a hit effect.
	 *
	 * @param isActive
	 *            a boolean value which indicate if a hit is active or not.
	 * @param variation
	 *            a boolean value which indicate if variations must be applied.
	 */
	private void initialize(boolean isActive, boolean variation) {
		this.isHit = isActive;
		this.variationX = variation ? (int) (Math.random() * 30) - 15 : 0;
		this.variationY = variation ? (int) (Math.random() * 30) - 15 : 0;
	}

	/**
	 *
	 * @return hit's state.
	 */
	public boolean isHit() {
		return this.isHit;
	}

	/**
	 * Returns the hit effect X variation
	 *
	 * @return hit effect's X-variation.
	 */
	public int getXVariation() {
		return this.variationX;
	}

	/**
	 * Returns the hit effect Y variation
	 *
	 * @return hit effect's Y-variation.
	 */
	public int getYVariation() {
		return this.variationY;
	}

}
