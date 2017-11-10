package struct;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.LinkedList;

import fighting.Attack;
import input.KeyData;
import setting.GameSetting;

public class FrameData {

	private CharacterData[] characterData;

	private int currentFrameNumber;
	/**
	 * The current round number
	 */
	private int currentRound;
	/**
	 * The projectile data of both characters
	 */
	private Deque<Attack> projectileData;
	/**
	 * The value of input information
	 */
	private KeyData keyData;

	/**
	 * If this value is true, no data are available or they are dummy data
	 */
	private boolean emptyFlag;

	/**
	 * Pixel data of the screen are saved in the form of ByteBuffer
	 */
	private ByteBuffer displayByteBuffer;

	/**
	 * Image of the screen
	 */
	private BufferedImage screenImage;

	/**
	 * This is the default constructor.
	 */
	public FrameData() {
		this.characterData = new CharacterData[2];
		this.currentFrameNumber = -1;
		this.currentRound = -1;
		this.projectileData = new LinkedList<Attack>();
		this.keyData = null;
		this.emptyFlag = true;
		this.displayByteBuffer = null;
		this.screenImage = null;
	}

	public FrameData(CharacterData[] characterData, int currentFrame, int currentRound, Deque<Attack> projectileData,
			KeyData keyData, ByteBuffer displayByteBuffer, BufferedImage screenImage) {
		this.characterData = new CharacterData[] { characterData[0], characterData[1] };
		this.currentFrameNumber = currentFrame;
		this.currentRound = currentRound;

		// make deep copy of the attacks list
		this.projectileData = new LinkedList<Attack>();
		for (Attack attack : projectileData) {
			// this.projectileData.add(new Attack(projectileData));
		}

		this.keyData = keyData;
		this.emptyFlag = false;
		this.displayByteBuffer = displayByteBuffer;
		this.screenImage = screenImage;
	}

	// Copy constructor for the FrameData class
	public FrameData(FrameData frameData) {
		this.characterData[0] = frameData.getMyCharacter(true);
		this.characterData[1] = frameData.getMyCharacter(false);
		this.currentFrameNumber = frameData.getCurrentFrameNumber();
		this.currentRound = frameData.getCurrentRound();

		// make deep copy of the attacks list
		this.projectileData = new LinkedList<Attack>();
		for (Attack attack : frameData.projectileData) {
			// this.projectileData.add(new Attack(attack));
		}

		this.keyData = new KeyData(frameData.getKeyData());
		this.emptyFlag = frameData.getEmptyFlag();
		this.displayByteBuffer = frameData.getDisplayByteBuffer();
		this.screenImage = frameData.getScreenImage();
	}

	public CharacterData getMyCharacter(boolean playerNumber) {
		return playerNumber ? new CharacterData(this.characterData[0]) : new CharacterData(this.characterData[1]);
	}

	public CharacterData getOpponentCharacter(boolean playerNumber) {
		return playerNumber ? new CharacterData(this.characterData[1]) : new CharacterData(this.characterData[0]);
	}

	/**
	 * Returns the expected remaining time in milliseconds of the current round.
	 *
	 * @return The expected remaining time in milliseconds of the current round
	 */
	public int getRemainingTimeMilliseconds() {
		// Calculate the expected remaining time in milliseconds (based on the
		// current frame)
		return GameSetting.ROUND_TIME - (int) (((float) this.currentFrameNumber / GameSetting.FPS) * 1000);
	}

	/**
	 * Returns the expected remaining time in seconds of the current round.
	 *
	 * @return The expected remaining time in seconds of the current round
	 * @deprecated Use {@link #getRemainingTimeMilliseconds()} instead. This
	 *             method has been renamed to more clearly reflect its purpose.
	 */
	public int getRemainingTime() {
		return (int) Math.ceil((float) getRemainingTimeMilliseconds() / 1000);
	}

	/**
	 * Returns the number of remaining frames of the round.
	 *
	 * @return The number of remaining frames of the round.
	 */
	public int getRemainingFrameNumber() {
		return (GameSetting.ROUND_FRAME_NUMBER - currentFrameNumber);
	}

	/**
	 * Returns the number of frames since the beginning of the round.
	 *
	 * @return The number of frames since the beginning of the round.
	 */
	public int getCurrentFrameNumber() {
		return this.currentFrameNumber;
	}

	/**
	 * Returns the current round number.
	 *
	 * @return The current round number
	 */
	public int getCurrentRound() {
		return this.currentRound;
	}

	/**
	 * Returns the projectile data of both characters.
	 *
	 * @return The projectile data of both characters
	 */
	public Deque<Attack> getProjectiles() {
		// create a deep copy of the attacks list
		LinkedList<Attack> attackList = new LinkedList<Attack>();
		for (Attack anAttack : this.projectileData) {
			// attackList.add(new Attack(anAttack));
		}

		return attackList;
	}

	/**
	 * Returns the projectile data of player 1.
	 *
	 * @return The projectile data of player 1
	 */
	public Deque<Attack> getProjectilesByP1() {
		LinkedList<Attack> attackList = new LinkedList<Attack>();
		for (Attack attack : this.projectileData) {
			if (attack.isPlayerNumber()) {
				// attackList.add(new Attack(attack));
			}
		}
		return attackList;
	}

	/**
	 * Returns the projectile data of player 2.
	 *
	 * @return The projectile data of player 2
	 */
	public Deque<Attack> getProjectilesByP2() {
		LinkedList<Attack> attackList = new LinkedList<Attack>();
		for (Attack attack : this.projectileData) {
			if (!attack.isPlayerNumber()) {
				// attackList.add(new Attack(attack));
			}
		}
		return attackList;
	}

	/**
	 * Returns the value of input information.
	 *
	 * @return The value of input information
	 */
	public KeyData getKeyData() {
		return new KeyData(keyData);
	}

	/**
	 * Returns true if this instance is empty, false if it contains meaningful
	 * data.
	 *
	 * @return emptyFlag true if this instance is empty, false if it contains
	 *         meaningful data.
	 */
	public boolean getEmptyFlag() {
		return this.emptyFlag;
	}

	/**
	 * Obtains RGB data of the screen in the form of ByteBuffer<br>
	 * Warning: If the window is disabled, will just return a black buffer
	 *
	 * @return The RGB data of the screen in the form of ByteBuffer
	 */
	public ByteBuffer getDisplayByteBuffer() {
		return this.displayByteBuffer;
	}

	/**
	 * Obtains RGB data of the screen in the form of byte[]<br>
	 * Warning: If the window is disabled, will just return a black buffer
	 *
	 * @return The RGB data of the screen in the form of byte[]
	 */
	public byte[] getDisplayByteBufferAsBytes() {
		byte[] buffer = new byte[this.displayByteBuffer.remaining()];
		this.displayByteBuffer.get(buffer);
		return buffer;
	}

	/**
	 * Obtains RGB data or the grayScale data of the screen in the form of
	 * byte[]<br>
	 * Warning: This method doesn't return exactly the same buffer as
	 * getDisplayByteBufferAsBytes()
	 *
	 * @param newWidth
	 *            The width in pixel for the scaled image
	 * @param newHeight
	 *            The height in pixel for the scaled image
	 * @param grayScale
	 *            True to use grayScale for the scaled image (1 byte per pixel
	 *            instead of 3 bytes per pixel with RGB)
	 * @return The RGB data or the grayScale data of the screen in the form of
	 *         byte[]
	 */
	public byte[] getDisplayByteBufferAsBytes(int newWidth, int newHeight, boolean grayScale) {
		// Scale the image (and grayScale too)
		BufferedImage newImage = new BufferedImage(newWidth, newHeight,
				grayScale ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_INT_RGB);
		{
			Graphics2D g = newImage.createGraphics();
			g.drawImage(this.screenImage, 0, 0, newWidth, newHeight, null);
			g.dispose();
		}

		// Convert it back to array of bytes
		byte[] dst;

		if (grayScale) {
			dst = ((DataBufferByte) newImage.getData().getDataBuffer()).getData();
		} else {
			dst = new byte[newWidth * newHeight * 3];

			int[] array = ((DataBufferInt) newImage.getRaster().getDataBuffer()).getData();
			for (int x = 0; x < newWidth; x++) {
				for (int y = 0; y < newHeight; y++) {
					int idx = x + y * newWidth;
					dst[idx * 3] = (byte) ((array[idx] >> 16) & 0xFF); // R
					dst[idx * 3 + 1] = (byte) ((array[idx] >> 8) & 0xFF); // G
					dst[idx * 3 + 2] = (byte) ((array[idx]) & 0xFF); // B
				}
			}
		}

		return dst;
	}

	public BufferedImage getScreenImage() {
		return this.screenImage;
	}

}
