package command;

import java.util.Deque;
import java.util.Iterator;

import enumerate.Action;
import enumerate.State;
import fighting.Character;
import input.KeyData;
import struct.Key;

/**
 * キー入力データをそれに対応するアクションに変換する処理を行うクラス．
 */
public class CommandTable {

	/**
	 * クラスコンストラクタ．
	 */
	public CommandTable() {

	}

	/**
	 * P1またはP2のキー入力データを対応するアクションに変換する処理を行い，そのアクションを返す．<br>
	 * P1とP2の判別は，キャラクターデータが持つプレイヤー番号によって行う．
	 *
	 * @param character
	 *            キャラクターデータ
	 * @param input
	 *            P1とP2両方のキー入力が格納されたキュー
	 *
	 * @return キー入力データに対応するアクション
	 *
	 * @see KeyData
	 */
	public Action interpretationCommandFromKeyData(Character character, Deque<KeyData> input) {
		Key nowKeyData;
		boolean pushA = false;
		boolean pushB = false;
		boolean pushC = false;
		int charIndex = character.isPlayerNumber() ? 0 : 1;

		KeyData temp;

		// get current key state
		temp = input.removeLast();
		nowKeyData = temp.getKeys()[charIndex];

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
		int[] commandList = { 5, 5, 5, 5 };
		int commandLength = 0;
		for (Iterator<KeyData> i = input.descendingIterator(); i.hasNext() && commandLength < 3;) {

			lever = i.next().getKeys()[charIndex].getLever(character.isFront());

			if (lever != commandList[commandLength]) {
				if (commandList[commandLength] != 5)
					commandLength++;
				commandList[commandLength] = lever;
			}
		}

		return convertKeyToAction(pushA, pushB, pushC, nowKeyData, commandList, character.getState(),
				character.isFront());
	}

	/**
	 * P1またはP2のキー入力データを対応するアクションに変換する処理を行い，そのアクションを返す．<br>
	 * このメソッドはシミュレータ内でのみ呼び出される.
	 *
	 * @param character
	 *            キャラクターデータ
	 * @param input
	 *            P1またはP2のキー入力が格納されたキュー
	 *
	 * @return キー入力データに対応するアクション
	 *
	 * @see Key
	 */
	public Action interpretationCommandFromKey(Character character, Deque<Key> input) {
		boolean pushA = false;
		boolean pushB = false;
		boolean pushC = false;

		// get current key state
		Key nowKey = new Key(input.removeLast());

		// The decision as input only at the moment you press the button. Press
		// keeps flick.
		if (!input.isEmpty()) {
			pushA = nowKey.A && !input.getLast().A;
			pushB = nowKey.B && !input.getLast().B;
			pushC = nowKey.C && !input.getLast().C;
		} else {
			pushA = nowKey.A;
			pushB = nowKey.B;
			pushC = nowKey.C;
		}

		input.addLast(nowKey);

		int lever;
		int[] commandList = { 5, 5, 5, 5 };
		int commandLength = 0;
		for (Iterator<Key> i = input.descendingIterator(); i.hasNext() && commandLength < 3;) {
			lever = i.next().getLever(character.isFront());

			if (lever != commandList[commandLength]) {
				if (commandList[commandLength] != 5)
					commandLength++;
				commandList[commandLength] = lever;
			}
		}

		return convertKeyToAction(pushA, pushB, pushC, nowKey, commandList, character.getState(), character.isFront());
	}

	/**
	 * 引数として渡されたキー入力情報とキャラクター情報を基に, それに対応するアクションを返す.<br>
	 *
	 * @param pushA
	 *            最新のキー入力でAキー(P1: Z, P2: T)が押されているかどうか
	 * @param pushB
	 *            最新のキー入力でBキー(P1: X, P2: Y)が押されているかどうか
	 * @param pushC
	 *            最新のキー入力でCキー(P1: C, P2: U)が押されているかどうか
	 * @param nowKeyData
	 *            最新のキー入力
	 * @param commandList
	 *            直近4つの方向キー入力を格納した配列(新しい入力ほどindexが小さい)
	 * @param state
	 *            キャラクターの現在の状態
	 * @param isFront
	 *            キャラクターが向いている方向(右向きはtrue;左向きはfalse)
	 *
	 * @return キー入力情報とキャラクター情報に対応するアクション
	 *
	 * @see Key
	 * @see State
	 * @see Action
	 */
	private Action convertKeyToAction(boolean pushA, boolean pushB, boolean pushC, Key nowKeyData, int[] commandList,
			State state, boolean isFront) {
		// 789
		// 456
		// 123

		// AIR Action
		if (state == State.AIR) {
			if (pushB) {
				// special move
				if ((commandList[0] == 6 && commandList[1] == 3 && commandList[2] == 2)) {
					return Action.AIR_D_DF_FB;// AIR236B

				} else if ((commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 6)
						|| (commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 3 && commandList[3] == 6)) {
					return Action.AIR_F_D_DFB;// AIR623B

				} else if (commandList[0] == 4 && commandList[1] == 1 && commandList[2] == 2) {
					return Action.AIR_D_DB_BB;// AIR214B

				} else if (nowKeyData.getLever(isFront) == 2) {
					return Action.AIR_DB;// AIR2B

				} else if (nowKeyData.getLever(isFront) == 8) {
					return Action.AIR_UB;// AIR8B

				} else if (nowKeyData.getLever(isFront) == 6) {
					return Action.AIR_FB;// AIR6B

				} else {
					return Action.AIR_B;// AIR5B
				}

			} else if (pushA) {
				// special move
				if ((commandList[0] == 6 && commandList[1] == 3 && commandList[2] == 2)) {
					return Action.AIR_D_DF_FA;// AIR236A

				} else if ((commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 6)
						|| (commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 3 && commandList[3] == 6)) {
					return Action.AIR_F_D_DFA;// AIR623A

				} else if (commandList[0] == 4 && commandList[1] == 1 && commandList[2] == 2) {
					return Action.AIR_D_DB_BA;// AIR214A

				} else if (nowKeyData.getLever(isFront) == 2) {
					return Action.AIR_DA;// AIR2A

				} else if (nowKeyData.getLever(isFront) == 8) {
					return Action.AIR_UA;// AIR8A

				} else if (nowKeyData.getLever(isFront) == 6) {
					return Action.AIR_FA;// AIR6A

				} else {
					return Action.AIR_A;// AIR5A
				}

			} else if (nowKeyData.getLever(isFront) == 4) {
				return Action.AIR_GUARD;// AIR4

			} else {
				return Action.AIR;// AIR5
			}

			// Ground Action
		} else {
			// Super special move
			if (pushC) {
				if ((commandList[0] == 6 && commandList[1] == 3 && commandList[2] == 2)) {
					return Action.STAND_D_DF_FC;// STAND236A
				}

			} else if (pushB) {
				// special move
				if ((commandList[0] == 6 && commandList[1] == 3 && commandList[2] == 2)) {
					return Action.STAND_D_DF_FB;// STAND236B

				} else if ((commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 6)
						|| (commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 3 && commandList[3] == 6)) {
					return Action.STAND_F_D_DFB;// STAND623B

				} else if (commandList[0] == 4 && commandList[1] == 1 && commandList[2] == 2) {
					return Action.STAND_D_DB_BB;// STAND214B

					// normal move
				} else if (nowKeyData.getLever(isFront) == 3) {
					return Action.CROUCH_FB;// STAND3B

				} else if (nowKeyData.getLever(isFront) == 2) {
					return Action.CROUCH_B;// STAND2B

				} else if (nowKeyData.getLever(isFront) == 4) {
					return Action.THROW_B;// STAND4B

				} else if (nowKeyData.getLever(isFront) == 6) {
					return Action.STAND_FB;// STAND6B

				} else {
					return Action.STAND_B;// STAND5B
				}

			} else if (pushA) {
				// special move
				if ((commandList[0] == 6 && commandList[1] == 3 && commandList[2] == 2)) {
					return Action.STAND_D_DF_FA;// STAND236A

				} else if ((commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 6)
						|| (commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 3 && commandList[3] == 6)) {
					return Action.STAND_F_D_DFA;// STAND623A

				} else if (commandList[0] == 4 && commandList[1] == 1 && commandList[2] == 2) {
					return Action.STAND_D_DB_BA;// STAND214A

					// normal move
				} else if (nowKeyData.getLever(isFront) == 3) {
					return Action.CROUCH_FA;// CROUCH3A

				} else if (nowKeyData.getLever(isFront) == 2) {
					return Action.CROUCH_A;// CROUCH2A

				} else if (nowKeyData.getLever(isFront) == 4) {
					return Action.THROW_A;// THROW4A

				} else if (nowKeyData.getLever(isFront) == 6) {
					return Action.STAND_FA;// STAND6A

				} else {
					return Action.STAND_A;// STAND5A
				}

			} else if (nowKeyData.getLever(isFront) == 6) {
				if (commandList[1] == 6) {
					return Action.DASH;// STAND66

				} else {
					return Action.FORWARD_WALK;// STAND6
				}

			} else if (nowKeyData.getLever(isFront) == 4) {
				if (commandList[1] == 4) {
					return Action.BACK_STEP;// STAND44

				} else {
					return Action.STAND_GUARD;// STAND4
				}

			} else {
				if (nowKeyData.getLever(isFront) == 1) {
					return Action.CROUCH_GUARD;// CROUCH1

				} else if (nowKeyData.getLever(isFront) == 2) {
					return Action.CROUCH;// CROUCH2

				} else if (nowKeyData.getLever(isFront) == 7) {
					return Action.BACK_JUMP;// STAND7

				} else if (nowKeyData.getLever(isFront) == 9) {
					return Action.FOR_JUMP;// STAND9
				}

				else if (nowKeyData.getLever(isFront) == 8) {
					return Action.JUMP;// STAND8

				} else {
					return Action.STAND;// STAND
				}

			}
		}
		return Action.STAND;
	}
}
