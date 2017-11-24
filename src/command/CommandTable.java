package command;

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;

import enumerate.Action;
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

		for (int i = commandList.length - 1; i >= 0; i--) {
			String inputLever = "";
			for (int j = i; j >= 0; j--) {
				inputLever += Integer.toString(commandList[j]);
			}

			String string = character.getState().name() + inputLever;
			if(character.isPlayerNumber()) System.out.println(string);

			// アクションがスキルテーブルにあれば、そのアクションを返す
			if (pushC && this.skilltable.containsKey(string + "C")) {
				return this.skilltable.get(string + "C");
			} else if (pushB && this.skilltable.containsKey(string + "B")) {
				return this.skilltable.get(string + "B");
			} else if (pushA && this.skilltable.containsKey(string + "A")) {
				return this.skilltable.get(string + "A");
			} else if (this.skilltable.containsKey(string + "N")) {
				return this.skilltable.get(string + "N");
			}
		}

		// 合致しなかった場合
		switch (character.getState()) {
		case AIR:
			return Action.AIR;
		case CROUCH:
			return Action.CROUCH;
		default:
			return Action.STAND;
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
}
