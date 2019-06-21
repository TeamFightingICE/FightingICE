package aiinterface;

import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.ScreenData;

/**
 * The interface that defines the methods to implement in AI.
 */
public interface AIInterface {

	/**
	 * This method initializes AI, and it will be executed only once in the
	 * beginning of each game. <br>
	 * Its execution will load the data that cannot be changed and load the flag
	 * of the player's side ("Boolean player", <em>true</em> for P1 or
	 * <em>false</em> for P2). <br>
	 * If there is anything that needs to be initialized, you had better do it
	 * in this method. <br>
	 * It will return 0 when such initialization finishes correctly, otherwise
	 * the error code.
	 *
	 * @param gd
	 *            the data that will not be changed during a game
	 * @param playerNumber
	 *            the character's side flag.<br>
	 *            {@code true} if the character is P1, or {@code false} if P2.
	 *
	 * @return 0 when such initialization finishes correctly
	 * @see GameData
	 */
	int initialize(GameData gd, boolean playerNumber);

	/**
	 * Gets information from the game status in each frame. <br>
	 * Such information is stored in the parameter frameData. <br>
	 * If {@code frameData.getRemainingTime()} returns a negative value, the
	 * current round has not started yet. <br>
	 * When you use frameData received from getInformation(), <br>
	 * you must always check if the condition
	 * {@code !frameData.getEmptyFlag() && frameData.getRemainingTime() > 0}
	 * holds; otherwise, NullPointerException will occur. <br>
	 * You must also check the same condition when you use the CommandCenter
	 * class.
	 *
	 * @param fd
	 *            the data that will be changed each frame
	 * @see FrameData
	 */
	void getInformation(FrameData fd, boolean isControl);

	/**
	 * Processes the data from AI. <br>
	 * It is executed in each frame.
	 */
	void processing();

	/**
	 * Receives a key input from AI.<br>
	 * It is executed in each frame and returns a value in the Key type.
	 *
	 * @return the value in the Key type
	 * @see Key
	 */
	Key input();

	/**
	 * Finalizes AI.<br>
	 * It runs only once at the end of each game.
	 */
	void close();

	/**
	 * Informs the result of each round.<br>
	 * It is called when each round ends.<br>
	 *
	 * @param p1Hp
	 *            P1's remaining HP
	 * @param p2Hp
	 *            P2's remaining HP
	 * @param frames
	 *            the elapsed frames from the start to the end of the round
	 */
	void roundEnd(int p1Hp, int p2Hp, int frames);

	/**
	 * Gets the screen information in each frame.
	 *
	 * @param sd
	 *            the screen information such as the pixel data
	 */
	default void getScreenData(ScreenData sd) {
	};

}
