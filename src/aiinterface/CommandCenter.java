package aiinterface;

import java.util.Deque;
import java.util.LinkedList;

import struct.FrameData;
import struct.Key;

/**
 * The class that converts actions received from AI to keys and manages the keys
 * after conversion.
 */
public class CommandCenter {

	/**
	 * The list storing the converted keys.
	 */
	private LinkedList<Key> skillKey;

	/**
	 * The frame data storing the information from the game status.
	 */
	private FrameData frameData;

	/**
	 * The character's side flag.<br>
	 * {@code true} if the character is P1, or {@code false} if P2.
	 */
	private boolean playerNumber;

	/**
	 * Class constructor.
	 */
	public CommandCenter() {
		this.skillKey = new LinkedList<Key>();
		this.frameData = new FrameData();
		this.playerNumber = true;
	}

	/**
	 * Converts the action name entered by AI into Key and stores it in the list of keys waiting to be executed.<br>
	 * If there are unexecuted keys in the list, this method does nothing.
	 *
	 * @param str
	 *            an action name
	 */
	public void commandCall(String str) {
		// If there is no unexecuted key in the list
		if (this.skillKey.isEmpty()) {
			actionToCommand(str);
		}
	}

	/**
	 * Converts the action name to a command and creates a key corresponding to the command.
	 *
	 * @param str
	 *            an action name
	 */
	private void actionToCommand(String str) {
		switch (str) {
		case "FORWARD_WALK":
			createKeys("6");
			break;
		case "DASH":
			createKeys("6 5 6");
			break;
		case "BACK_STEP":
			createKeys("4 5 4");
			break;
		case "CROUCH":
			createKeys("2");
			break;
		case "JUMP":
			createKeys("8");
			break;
		case "FOR_JUMP":
			createKeys("9");
			break;
		case "BACK_JUMP":
			createKeys("7");
			break;
		case "STAND_GUARD":
			createKeys("4");
			break;
		case "CROUCH_GUARD":
			createKeys("1");
			break;
		case "AIR_GUARD":
			createKeys("7");
			break;
		case "THROW_A":
			createKeys("4 _ A");
			break;
		case "THROW_B":
			createKeys("4 _ B");
			break;
		case "STAND_A":
			createKeys("A");
			break;
		case "STAND_B":
			createKeys("B");
			break;
		case "CROUCH_A":
			createKeys("2 _ A");
			break;
		case "CROUCH_B":
			createKeys("2 _ B");
			break;
		case "AIR_A":
			createKeys("A");
			break;
		case "AIR_B":
			createKeys("B");
			break;
		case "AIR_DA":
			createKeys("2 _ A");
			break;
		case "AIR_DB":
			createKeys("2 _ B");
			break;
		case "STAND_FA":
			createKeys("6 _ A");
			break;
		case "STAND_FB":
			createKeys("6 _ B");
			break;
		case "CROUCH_FA":
			createKeys("3 _ A");
			break;
		case "CROUCH_FB":
			createKeys("3 _ B");
			break;
		case "AIR_FA":
			createKeys("9 _ A");
			break;
		case "AIR_FB":
			createKeys("9 _ B");
			break;
		case "AIR_UA":
			createKeys("8 _ A");
			break;
		case "AIR_UB":
			createKeys("8 _ B");
			break;
		case "STAND_D_DF_FA":
			createKeys("2 3 6 _ A");
			break;
		case "STAND_D_DF_FB":
			createKeys("2 3 6 _ B");
			break;
		case "STAND_F_D_DFA":
			createKeys("6 2 3 _ A");
			break;
		case "STAND_F_D_DFB":
			createKeys("6 2 3 _ B");
			break;
		case "STAND_D_DB_BA":
			createKeys("2 1 4 _ A");
			break;
		case "STAND_D_DB_BB":
			createKeys("2 1 4 _ B");
			break;
		case "AIR_D_DF_FA":
			createKeys("2 3 6 _ A");
			break;
		case "AIR_D_DF_FB":
			createKeys("2 3 6 _ B");
			break;
		case "AIR_F_D_DFA":
			createKeys("6 2 3 _ A");
			break;
		case "AIR_F_D_DFB":
			createKeys("6 2 3 _ B");
			break;
		case "AIR_D_DB_BA":
			createKeys("2 1 4 _ A");
			break;
		case "AIR_D_DB_BB":
			createKeys("2 1 4 _ B");
			break;
		case "STAND_D_DF_FC":
			createKeys("2 3 6 _ C");
			break;
		default:
			createKeys(str);
			break;
		}

	}

	/**
	 * Creates a key corresponding to a command name.
	 *
	 * @param str
	 *            a command name
	 */
	private void createKeys(String str) {
		Key buf;
		String[] commands = str.split(" ");
		if (!this.frameData.getCharacter(playerNumber).isFront()) {
			commands = reverseKey(commands);
		}

		int index = 0;
		while (index < commands.length) {
			buf = new Key();
			if (commands[index].equals("L") || commands[index].equals("4")) {
				buf.L = true;
			} else if (commands[index].equals("R") || commands[index].equals("6")) {
				buf.R = true;
			} else if (commands[index].equals("D") || commands[index].equals("2")) {
				buf.D = true;
			} else if (commands[index].equals("U") || commands[index].equals("8")) {
				buf.U = true;
			} else if (commands[index].equals("LD") || commands[index].equals("1")) {
				buf.L = true;
				buf.D = true;
			} else if (commands[index].equals("LU") || commands[index].equals("7")) {
				buf.L = true;
				buf.U = true;
			} else if (commands[index].equals("RD") || commands[index].equals("3")) {
				buf.R = true;
				buf.D = true;
			} else if (commands[index].equals("RU") || commands[index].equals("9")) {
				buf.R = true;
				buf.U = true;
			}

			if (index + 2 < commands.length && commands[index + 1].equals("_")) {
				index += 2;
			}
			if (commands[index].equals("A")) {
				buf.A = true;
			} else if (commands[index].equals("B")) {
				buf.B = true;
			} else if (commands[index].equals("C")) {
				buf.C = true;
			}
			skillKey.add(buf);
			index++;
		}
	}

	/**
	 * Sets the current frame data and the boolean variable representing P1 and P2.
	 *
	 * @param frameData
	 *            the current frame data
	 * @param playerNumber
	 *            The character's side flag.<br>
	 *            {@code true} if the character is P1, or {@code false} if P2.
	 */
	public void setFrameData(FrameData frameData, boolean playerNumber) {
		this.frameData = frameData;
		this.playerNumber = playerNumber;
	}

	/**
	 * Returns whether there are unexecuted keys in the list of keys waiting to be executed.
	 *
	 * @return {@code true} if there are keys not yet executed in the list，{@code false} otherwise.
	 */
	public boolean getSkillFlag() {
		return !this.skillKey.isEmpty();
	}

	/**
	 * Returns the first element from the list of keys waiting to be executed. <br>
	 * The returned element is deleted from the CommandCenter.
	 *
	 * @return the next key to be executed
	 */
	public Key getSkillKey() {
		if (!this.skillKey.isEmpty()) {
			return this.skillKey.pollFirst();
		} else {
			return new Key();
		}
	}

	/**
	 * Returns the list of keys waiting to be executed.
	 *
	 * @return the list of keys waiting to be executed
	 */
	public Deque<Key> getSkillKeys() {
		return new LinkedList<Key>(this.skillKey);
	}

	/**
	 * Deletes all the keys from the list of keys waiting to be executed.
	 */
	public void skillCancel() {
		this.skillKey.clear();
	}

	/**
	 * Returns the character's side flag.
	 *
	 * @return {@code true} if the character is P1, or {@code false} if P2.
	 */
	public boolean isPlayerNumber() {
		return this.playerNumber;
	}

	/**
	 * Reverses the command horizontally according to the direction the character is facing.
	 *
	 * @param commands
	 *            an array containing the command converted from an action name
	 *
	 * @return an array that contains the command after processing
	 */
	private String[] reverseKey(String[] commands) {
		String[] buffer = new String[commands.length];
		for (int i = 0; i < commands.length; i++) {
			if (commands[i].equals("L") || commands[i].equals("4")) {
				buffer[i] = "6";
			} else if (commands[i].equals("R") || commands[i].equals("6")) {
				buffer[i] = "4";
			} else if (commands[i].equals("LD") || commands[i].equals("1")) {
				buffer[i] = "3";
			} else if (commands[i].equals("LU") || commands[i].equals("7")) {
				buffer[i] = "9";
			} else if (commands[i].equals("RD") || commands[i].equals("3")) {
				buffer[i] = "1";
			} else if (commands[i].equals("RU") || commands[i].equals("9")) {
				buffer[i] = "7";
			} else {
				buffer[i] = commands[i];
			}
		}
		return buffer;
	}
}
