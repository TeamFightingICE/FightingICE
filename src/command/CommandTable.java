package command;

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;

import enumerate.Action;
import enumerate.State;
import fighting.Character;
import input.KeyData;
import struct.Key;

public class CommandTable {

	private HashMap<String, Action> skilltable;

	public CommandTable() {
		this.skilltable = new HashMap<String, Action>();
		setSkillTable();
	}

	public Action convertKeyToAction(Character character, Deque<KeyData> input) {
		int[] commandList = { 5, 5, 5, 5 };
		boolean pushA = false;
		boolean pushB = false;
		boolean pushC = false;
		int characterIndex = character.isPlayerNumber() ? 0 : 1;
		KeyData temp = input.removeLast();
		Key nowKeyData = temp.getKeys()[characterIndex];

		// The decision as input only at the moment you press the button. Press
		// keeps flick.
		if (!input.isEmpty()) {
			pushA = nowKeyData.A && !input.getLast().getKeys()[characterIndex].A;
			pushB = nowKeyData.B && !input.getLast().getKeys()[characterIndex].B;
			pushC = nowKeyData.C && !input.getLast().getKeys()[characterIndex].C;
		} else {
			pushA = nowKeyData.A;
			pushB = nowKeyData.B;
			pushC = nowKeyData.C;
		}

		input.addLast(temp);

		int lever;
		int commandLength = 0;
		for (Iterator<KeyData> i = input.descendingIterator(); i.hasNext() && commandLength < 3;) {
			lever = i.next().getKeys()[characterIndex].getLever(character.isFront());

			// L, R, U, DのどれかがtrueならコマンドリストにそのキーのInteger表現を格納する
			if (lever != commandList[commandLength]) {
				if (commandList[commandLength] != 5) {
					commandLength++;
				}
				commandList[commandLength] = lever;
			}
		}

		if (character.isPlayerNumber()) {
			for (int i = 0; i < commandList.length; i++) {
				System.out.print(commandList[i]);
			}
			System.out.println();
		}

		Action action = null;
		String copy = "";
		for (int i = commandList.length - 1; i >= 0; i--) {
			String inputLever = "";
			for (int j = i; j >= 0; j--) {
				inputLever += Integer.toString(commandList[j]);
			}

			String string;
			if (character.getState() == State.AIR) {
				string = "AIR" + inputLever;
			} else {
				string = "STAND" + inputLever;
			}

			// System.out.println(string);
			// アクションがスキルテーブルにあれば、そのアクションを返す
			if (pushC && action == null) {
				copy = string + "C";
				action = this.skilltable.get(string + "C");
				// break;
			} else if (pushB && action == null) {
				action = this.skilltable.get(string + "B");
				copy = string + "B";
				// break;
			} else if (pushA && action == null) {
				action = this.skilltable.get(string + "A");
				copy = string + "A";
				// break;
			} else if (action == null) {
				action = this.skilltable.get(string + "N");
				copy = string;
				// break;
			}
		}

		if (action != null) {
			System.out.println("該当あり: " + copy);
			return action;
		} else {
			// 合致しなかった場合
			System.out.println("該当なし: " + copy);
			switch (character.getState()) {
			case AIR:
				return Action.AIR;
			default:
				return Action.STAND;
			}
		}

	}

	private void setSkillTable() {
		////// AIR//////
		// B
		this.skilltable.put("AIR236B", Action.AIR_D_DF_FB);
		this.skilltable.put("AIR6323B", Action.AIR_F_D_DFB);
		this.skilltable.put("AIR623B", Action.AIR_F_D_DFB);
		this.skilltable.put("AIR214B", Action.AIR_D_DB_BB);

		this.skilltable.put("AIR2B", Action.AIR_DB);
		this.skilltable.put("AIR8B", Action.AIR_UB);
		this.skilltable.put("AIR6B", Action.AIR_FB);
		this.skilltable.put("AIR5B", Action.AIR_B);

		// A
		this.skilltable.put("AIR236A", Action.AIR_D_DF_FA);
		this.skilltable.put("AIR6323A", Action.AIR_F_D_DFA);
		this.skilltable.put("AIR623A", Action.AIR_F_D_DFA);
		this.skilltable.put("AIR214A", Action.AIR_D_DB_BA);
		this.skilltable.put("AIR2A", Action.AIR_DA);
		this.skilltable.put("AIR8A", Action.AIR_UA);
		this.skilltable.put("AIR6A", Action.AIR_FA);
		this.skilltable.put("AIR5A", Action.AIR_A);

		// N
		this.skilltable.put("AIR4N", Action.AIR_GUARD);
		this.skilltable.put("AIR5N", Action.AIR);

		////// GROUND//////
		// C
		this.skilltable.put("STAND236C", Action.STAND_D_DF_FC);

		// B
		this.skilltable.put("STAND236B", Action.STAND_D_DF_FB);
		this.skilltable.put("STAND6323B", Action.STAND_F_D_DFB);
		this.skilltable.put("STAND623B", Action.STAND_F_D_DFB);
		this.skilltable.put("STAND214B", Action.STAND_D_DB_BB);

		this.skilltable.put("STAND3B", Action.CROUCH_FB);
		this.skilltable.put("STAND2B", Action.CROUCH_B);
		this.skilltable.put("STAND4B", Action.THROW_B);
		this.skilltable.put("STAND6B", Action.STAND_FB);
		this.skilltable.put("STAND5B", Action.STAND_B);

		// A
		this.skilltable.put("STAND236A", Action.STAND_D_DF_FA);
		this.skilltable.put("STAND6323A", Action.STAND_F_D_DFA);
		this.skilltable.put("STAND623A", Action.STAND_F_D_DFA);
		this.skilltable.put("STAND214A", Action.STAND_D_DB_BA);

		this.skilltable.put("STAND3A", Action.CROUCH_FA);
		this.skilltable.put("STAND2A", Action.CROUCH_A);
		this.skilltable.put("STAND4A", Action.THROW_A);
		this.skilltable.put("STAND6A", Action.STAND_FA);
		this.skilltable.put("STAND5A", Action.STAND_A);

		// N
		this.skilltable.put("STAND66N", Action.DASH);
		this.skilltable.put("STAND6N", Action.FORWARD_WALK);
		this.skilltable.put("STAND44N", Action.BACK_STEP);
		this.skilltable.put("STAND4N", Action.STAND_GUARD);
		this.skilltable.put("STAND1N", Action.CROUCH_GUARD);
		this.skilltable.put("STAND2N", Action.CROUCH);
		this.skilltable.put("STAND7N", Action.BACK_JUMP);
		this.skilltable.put("STAND9N", Action.FOR_JUMP);
		this.skilltable.put("STAND8N", Action.JUMP);
		this.skilltable.put("STAND5N", Action.STAND);
	}

	public Action interpretationCommand(Character character, Deque<KeyData> input) {
		Key nowKeyData;
		int[] commandList = { 5, 5, 5, 5 };
		boolean pushA = false, pushB = false, pushC = false;
		int charIndex = character.isPlayerNumber() ? 0 : 1;

		KeyData temp;

		// get current key state
		temp = input.removeLast();
		nowKeyData = new Key(temp.getKeys()[charIndex]);

		// The decision as input only at the moment you press the button. Press
		// keeps flick.
		if (!input.isEmpty()) {
			pushA = nowKeyData.A && !input.getLast().getKeys()[charIndex].A;
			pushB = nowKeyData.B && !input.getLast().getKeys()[charIndex].B;
			pushC = nowKeyData.C && !input.getLast().getKeys()[charIndex].C;
		} else {
			pushA = nowKeyData.A;
			pushB = nowKeyData.B;
			pushC = nowKeyData.C;
		}

		input.addLast(temp);

		int lever;
		int commandLength = 0;
		for (Iterator<KeyData> i = input.descendingIterator(); i.hasNext() && commandLength < 3;) {

			lever = i.next().getKeys()[charIndex].getLever(character.isFront());

			if (lever != commandList[commandLength]) {
				if (commandList[commandLength] != 5)
					commandLength++;
				commandList[commandLength] = lever;
			}
		}

		/*if (character.isPlayerNumber()) {
			for (int i = 0; i < commandList.length; i++) {
				System.out.print(commandList[i]);
			}
			System.out.println();
		}*/

		// return which command whether the inputed
		// 789
		// 456
		// 123
		// AIR Action
		if (character.getState() == State.AIR) {
			if (pushB) {
				// special move
				if ((commandList[0] == 6 && commandList[1] == 3 && commandList[2] == 2))
					return Action.AIR_D_DF_FB;// AIR236B
				else if ((commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 6)
						|| (commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 3 && commandList[3] == 6))
					return Action.AIR_F_D_DFB;// AIR623B
				else if (commandList[0] == 4 && commandList[1] == 1 && commandList[2] == 2)
					return Action.AIR_D_DB_BB;// AIR214B
				// normal move
				else if (nowKeyData.getLever(character.isFront()) == 2)
					return Action.AIR_DB;// AIR2B
				else if (nowKeyData.getLever(character.isFront()) == 8)
					return Action.AIR_UB;// AIR8B
				else if (nowKeyData.getLever(character.isFront()) == 6)
					return Action.AIR_FB;// AIR6B
				else
					return Action.AIR_B;// AIR5B
			} else if (pushA) {
				// special move
				if ((commandList[0] == 6 && commandList[1] == 3 && commandList[2] == 2))
					return Action.AIR_D_DF_FA;// AIR236A
				else if ((commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 6)
						|| (commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 3 && commandList[3] == 6))
					return Action.AIR_F_D_DFA;// AIR623A
				else if (commandList[0] == 4 && commandList[1] == 1 && commandList[2] == 2)
					return Action.AIR_D_DB_BA;// AIR214A
				// normal move
				else if (nowKeyData.getLever(character.isFront()) == 2)
					return Action.AIR_DA;// AIR2A
				else if (nowKeyData.getLever(character.isFront()) == 8)
					return Action.AIR_UA;// AIR8A
				else if (nowKeyData.getLever(character.isFront()) == 6)
					return Action.AIR_FA;// AIR6A
				else
					return Action.AIR_A;// AIR5A
			} else if (nowKeyData.getLever(character.isFront()) == 4)
				return Action.AIR_GUARD;// AIR4
			else
				return Action.AIR;// AIR5
		}
		// Ground Action
		else {
			// Super special move
			if (pushC) {
				if ((commandList[0] == 6 && commandList[1] == 3 && commandList[2] == 2))
					return Action.STAND_D_DF_FC;// STAND236A
			} else if (pushB) {
				// special move
				if ((commandList[0] == 6 && commandList[1] == 3 && commandList[2] == 2))
					return Action.STAND_D_DF_FB;// STAND236B
				else if ((commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 6)
						|| (commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 3 && commandList[3] == 6))
					return Action.STAND_F_D_DFB;// STAND623B
				else if (commandList[0] == 4 && commandList[1] == 1 && commandList[2] == 2)
					return Action.STAND_D_DB_BB;// STAND214B
				// normal move
				else if (nowKeyData.getLever(character.isFront()) == 3)
					return Action.CROUCH_FB;// STAND3B
				else if (nowKeyData.getLever(character.isFront()) == 2)
					return Action.CROUCH_B;// STAND2B
				else if (nowKeyData.getLever(character.isFront()) == 4)
					return Action.THROW_B;// STAND4B
				else if (nowKeyData.getLever(character.isFront()) == 6)
					return Action.STAND_FB;// STAND6B
				else
					return Action.STAND_B;// STAND5B
			} else if (pushA) {
				// special move
				if ((commandList[0] == 6 && commandList[1] == 3 && commandList[2] == 2)) {

					return Action.STAND_D_DF_FA;// STAND236A
				}

				else if ((commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 6)
						|| (commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 3 && commandList[3] == 6))
					return Action.STAND_F_D_DFA;// STAND623A
				else if (commandList[0] == 4 && commandList[1] == 1 && commandList[2] == 2)
					return Action.STAND_D_DB_BA;// STAND214A
				// normal move
				else if (nowKeyData.getLever(character.isFront()) == 3)
					return Action.CROUCH_FA;// CROUCH3A
				else if (nowKeyData.getLever(character.isFront()) == 2)
					return Action.CROUCH_A;// CROUCH2A
				else if (nowKeyData.getLever(character.isFront()) == 4)
					return Action.THROW_A;// THROW4A
				else if (nowKeyData.getLever(character.isFront()) == 6)
					return Action.STAND_FA;// STAND6A
				else
					return Action.STAND_A;// STAND5A
			} else if (nowKeyData.getLever(character.isFront()) == 6) {
				if (commandList[1] == 6)
					return Action.DASH;// STAND66
				else
					return Action.FORWARD_WALK;// STAND6
			} else if (nowKeyData.getLever(character.isFront()) == 4) {
				if (commandList[1] == 4)
					return Action.BACK_STEP;// STAND44
				else
					return Action.STAND_GUARD;// STAND4
			} else {
				if (nowKeyData.getLever(character.isFront()) == 1)
					return Action.CROUCH_GUARD;// CROUCH1
				else if (nowKeyData.getLever(character.isFront()) == 2) {
					// System.out.println("該当あり: CROUCH");
					return Action.CROUCH;// CROUCH2
				}

				else if (nowKeyData.getLever(character.isFront()) == 7)
					return Action.BACK_JUMP;// STAND7
				else if (nowKeyData.getLever(character.isFront()) == 9) {
					return Action.FOR_JUMP;// STAND9
				}

				else if (nowKeyData.getLever(character.isFront()) == 8)
					return Action.JUMP;// STAND8
				else {
					// System.out.println("該当あり: STAND");
					return Action.STAND;// STAND
				}

			}
		}
		System.out.println("該当なし");
		return Action.STAND;
	}
}
