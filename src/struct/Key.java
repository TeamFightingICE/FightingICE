package struct;

import protoc.MessageProto.GrpcKey;

/**
 * The class dealing with all possible keys used in the game.
 */
public class Key {
	/**
	 * If the value is set to true, then the "A" button will be pressed.
	 */
	public boolean A;
	/**
	 * If the value is set to true, then the "B" button will be pressed.
	 */
	public boolean B;
	/**
	 * If the value is set to true, then the "C" button will be pressed.
	 */
	public boolean C;
	/**
	 * If the value is set to true, then the "Up" button will be pressed.
	 */
	public boolean U;
	/**
	 * If the value is set to true, then the "Right" button will be pressed.
	 */
	public boolean R;
	/**
	 * If the value is set to true, then the "Down" button will be pressed.
	 */
	public boolean D;
	/**
	 * If the value is set to true, then the "Left" button will be pressed.
	 */
	public boolean L;

	/**
	 * This constructor initializes all keys to false, or not pressed.
	 */
	public Key() {
		this.empty();
	}

	/**
	 * The class constructor that initializes the key with the specified data.
	 *
	 * @param key
	 *            an instance of the Key class
	 */
	public Key(Key key) {
		if (key != null) {
			this.A = key.A;
			this.B = key.B;
			this.C = key.C;
			this.U = key.U;
			this.R = key.R;
			this.D = key.D;
			this.L = key.L;
		} else {
			this.empty();
		}
	}

	/**
	 * Resets all keys to false, or not pressed.
	 */
	public void empty() {
		this.A = false;
		this.B = false;
		this.C = false;
		this.U = false;
		this.R = false;
		this.D = false;
		this.L = false;
	}
	
	public boolean isEmpty() {
		if (this.A) return false;
		else if (this.B) return false;
		else if (this.C) return false;
		else if (this.U) return false;
		else if (this.R) return false;
		else if (this.D) return false;
		else if (this.L) return false;
		return true;
	}

	/**
	 * Returns the value indicating the direction key input by the player using
	 * the numeric keypad.
	 *
	 * @param isFront
	 *            the boolean value representing the player's orientation
	 * @return the direction value from the numeric keypad
	 */
	public int getLever(boolean isFront) {
		int lever = 5;

		if (this.U) {
			lever += 3;
		}
		if (this.D) {
			lever -= 3;
		}
		if (this.L) {
			lever += isFront ? -1 : 1;
		}
		if (this.R) {
			lever += isFront ? 1 : -1;
		}

		return lever;
	}
	
	public GrpcKey toProto() {
		return GrpcKey.newBuilder()
    			.setA(this.A)
    			.setB(this.B)
    			.setC(this.C)
    			.setD(this.D)
    			.setL(this.L)
    			.setR(this.R)
    			.setU(this.U)
    			.build();
	}
	
}
