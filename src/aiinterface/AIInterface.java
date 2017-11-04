package aiinterface;

import struct.FrameData;
import struct.GameData;
import struct.Key;

public interface AIInterface {

/*	String CHARACTER_ZEN = "ZEN";
	String CHARACTER_GARNET = "GARNET";
	String CHARACTER_LUD = "LUD";
	String CHARACTER_KFM = "KFM";*/

	/**
	 * This method initializes AI, and it will be executed only once in the beginning of each game. <br>
	 * Its execution will load the data that cannot be changed and load the flag of player's
	 * side ("Boolean player", <em>true</em> for P1 or <em>false</em> for P2). <br>
	 * If there is anything that needs to be initialized, you had better do it in this method. <br>
	 * It will return 0 when such initialization finishes correctly, otherwise the error code.
	 *
	 * @param gameData Data that will not be changed during a game
	 * @param playerNumber PlayerNumber of self
	 * @return 0 when such initialization finishes correctly
	 * @see structs.GameData
	 */
	int initialize(GameData gameData,boolean playerNumber);

	/**
	 * This method gets information from the game status in each frame. <br>
	 * Such information is stored in the parameter frameData. <br>
	 * If {@code frameData.getRemainingTime()} returns a negative value, the current round has not started yet. <br>
	 * When you use frameData received from getInformation(), <br>
	 * you must always check if the condition {@code !frameData.getEmptyFlag() && frameData.getRemainingTime() > 0} holds;
	 * otherwise, NullPointerException will occur. <br>
	 * You must also check the same condition when you use the CommandCenter class.
	 *
	 * @param frameData Data that will be changed each frame
	 * @see structs.FrameData
	 */
	void getInformation(FrameData frameData);

	/**
	 * This method processes the data from AI. <br>
	 * It is executed in each frame
	 */
	void processing();

	/**
	 * This method receives key input from AI.<br>
	 * It is executed in each frame and returns a value in the Key type.
	 *
	 * @return Key A value in the Key type
	 * @see structs.Key
	 */
	Key input();

	/**
	 * This method finalizes AI.<br>
	 * It runs only once at the end of each game.
	 */
	void close();

	/**
	 * This method informs the result of each round.<br>
	 * It is called when each round ends.<br>
	 *
	 * @param p1Hp P1's remaining HP
	 * @param p2Hp P2's remaining HP
	 * @param frames Elapsed frames from the start to the end of the round
	 * */
	default void roundEnd(int p1Hp, int p2Hp, int frames){}

/*	*//**
	 * This method is for deciding which character to use among ZEN, GARNET, LUD, and KFM.<br>
	 * It returns one of the following values, which <b>must be specified after "return" for the competition:</b> <br>
	 * <b>CHARACTER_ZEN, CHARACTER_GARNET, CHARACTER_LUD, and CHARACTER_KFM</b>
	 *
	 * @return CHARACTER_NAME The character name you specified.<br>
	 *                        <b>Please do not specify values other than one of the above 4 values.</b>
	 *//*
	String getCharacter();
*/


}
