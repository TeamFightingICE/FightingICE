package util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.stream.JsonGenerator;

import fighting.Character;
import informationcontainer.RoundResult;
import input.KeyData;
import loader.ResourceLoader;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import struct.AttackData;
import struct.CharacterData;
import struct.FrameData;
import struct.HitArea;
import struct.Key;

public class LogWriter {

	/**
	 * 試合結果の出力ファイル拡張子を.csvに指定する定数．
	 */
	public static final int CSV = 0;

	/**
	 * 試合結果の出力ファイル拡張子を.txtに指定する定数．
	 */
	public static final int TXT = 1;

	/**
	 * 試合結果の出力ファイル拡張子を.PLOGに指定する定数．
	 */
	public static final int PLOG = 2;

	/**
	 * This variable stores the current round.<br>
	 * It is updated every time updateJson() is called.<br>
	 * It is used to realise when the round changes.
	 */
	private int currentRound = 0;

	/** Stream generator for JSON. */
	private JsonGenerator generator;

	/**
	 * A flag marking whether to include display information in instances of
	 * FrameData.
	 */
	boolean disableDisplayDataInFrameData;

	/**
	 * クラスコンストラクタ．
	 */
	private LogWriter() {
		Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + LogWriter.class.getName());
	}

	/**
	 * getInstance()が呼ばれたときに初めてインスタンスを生成するホルダークラス．
	 */
	private static class LogWriterHolder {
		private static final LogWriter instance = new LogWriter();
	}

	/**
	 * LogWriterクラスの唯一のインスタンスを取得する．
	 *
	 * @return LogWriterクラスの唯一のインスタンス
	 */
	public static LogWriter getInstance() {
		return LogWriterHolder.instance;
	}

	/**
	 * 試合結果を引数で指定した拡張子のファイルへ出力する．<br>
	 * 引数の現在の時間情報は出力ファイル名に用いられる．
	 *
	 * @param roundResults
	 *            各ラウンドの結果を格納しているリスト
	 * @param extension
	 *            指定拡張子
	 * @param timeInfo
	 *            現在の時間情報
	 */
	public void outputResult(ArrayList<RoundResult> roundResults, int extension, String timeInfo) {
		String path = "./log/point/";
		String fileName = createOutputFileName(path, timeInfo);

		PrintWriter pw;
		switch (extension) {
		case CSV:
			pw = ResourceLoader.getInstance().openWriteFile(fileName + ".csv", false);
			break;
		case TXT:
			pw = ResourceLoader.getInstance().openWriteFile(fileName + ".txt", false);
			break;
		default:
			pw = ResourceLoader.getInstance().openWriteFile(fileName + ".PLOG", false);
			break;
		}

		for (RoundResult roundResult : roundResults) {
			int[] score = roundResult.getRemainingHPs();

			pw.println(roundResult.getRound() + "," + score[0] + "," + score[1] + "," + roundResult.getElapsedFrame());
		}

		pw.close();
	}

	/**
	 * リプレイファイルのログを出力する．<br>
	 * 現在フレームのキャラクター情報とキー入力のデータが書き込まれる．
	 *
	 * @param dos
	 *            リプレイファイルに書き込みを行うためのデータ出力ストリーム
	 * @param keyData
	 *            KeyDataクラスのインスタンス
	 * @param playerCharacters
	 *            P1とP2のキャラクターを格納した配列
	 */
	public void outputLog(DataOutputStream dos, KeyData keyData, Character[] playerCharacters) {
		// output log file for replay
		try {
			for (int i = 0; i < 2; ++i) {
				dos.writeBoolean(playerCharacters[i].isFront());
				dos.writeByte((byte) playerCharacters[i].getRemainingFrame());
				dos.writeByte((byte) playerCharacters[i].getAction().ordinal());
				dos.writeInt(playerCharacters[i].getHp());
				dos.writeInt(playerCharacters[i].getEnergy());
				dos.writeInt(playerCharacters[i].getX());
				dos.writeInt(playerCharacters[i].getY());

				byte input = (byte) (convertBtoI(keyData.getKeys()[i].A) + convertBtoI(keyData.getKeys()[i].B) * 2
						+ convertBtoI(keyData.getKeys()[i].C) * 4 + convertBtoI(keyData.getKeys()[i].D) * 8
						+ convertBtoI(keyData.getKeys()[i].L) * 16 + convertBtoI(keyData.getKeys()[i].R) * 32
						+ convertBtoI(keyData.getKeys()[i].U) * 64);

				dos.writeByte(input);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * リプレイファイルにゲームモード(HP mode or Time mode)や使用キャラといったヘッダ情報を記述する．
	 *
	 * @param dos
	 *            リプレイファイルに書き込みを行うためのデータ出力ストリーム
	 */
	public void writeHeader(DataOutputStream dos) {
		try {
			for (int i = 0; i < 2; i++) {
				if (FlagSetting.limitHpFlag) {
					dos.writeInt(-1);
					dos.writeInt(LaunchSetting.maxHp[i]);
				}

				dos.writeInt(Arrays.asList(GameSetting.CHARACTERS).indexOf(LaunchSetting.characterNames[i]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 出力ファイルの名前を生成する．<br>
	 * "ファイル出力場所のパス+ゲームモード+P1のAI名+P2のAI名+現在時間"をファイル名として返す．
	 *
	 * @param path
	 *            ファイルを出力する場所のパス
	 * @param timeInfo
	 *            現在の時間情報
	 *
	 * @return 出力ファイル名
	 */
	public String createOutputFileName(String path, String timeInfo) {
		String mode = FlagSetting.limitHpFlag ? "HPMode" : "TimeMode";

		return path + mode + "_" + LaunchSetting.aiNames[0] + "_" + LaunchSetting.aiNames[1] + "_" + timeInfo;
	}

	/**
	 * boolean型変数をint型に変換する．
	 *
	 * @param b
	 *            変換したいboolean型の変数
	 *
	 * @return 1 : 引数がtrueのとき, 0: 引数がfalseのとき
	 */
	private int convertBtoI(boolean b) {
		return b ? 1 : 0;
	}

	/**
	 * Instantiates a JSON generator and writes initial information about the
	 * match.<br>
	 * The JSON structure is as follows:
	 *
	 * <pre>
	 * {
	 *     "max_hp": {"x": 200, "y": 200},
	 *     "character_names": {"P1": "ZEN", "P2": "ZEN"},
	 *     "stage_size": {"x": 200, "y": 200},
	 *     "rounds": [
	 *         [
	 *             {
	 *                 "current_frame": 123,
	 *                 "remaining_frames": 123, // actually true only if --limithp is NOT used
	 *                 "P1": {
	 *                     "front": true,
	 *                     "remaining_frames": 48,
	 *                     "action": "STAND",
	 *                     "action_id": 1,
	 *                     "state": "STAND",
	 *                     "state_id": 0,
	 *                     "hp": 0,
	 *                     "energy": 0,
	 *                     "x": 100,
	 *                     "y": 335,
	 *                     "left": 100,
	 *                     "right": 100,
	 *                     "top": 100,
	 *                     "bottom": 100,
	 *                     "speed_x": 0,
	 *                     "speed_y": 0,
	 *                     "key_a": false,
	 *                     "key_b": false,
	 *                     "key_c": false,
	 *                     "key_up": false,
	 *                     "key_down": false,
	 *                     "key_left": false,
	 *                     "key_right": false,
	 *                     "attack": {
	 *                         "speed_x": 0,
	 *                         "speed_y": 0,
	 *                         "hit_damage": 10,
	 *                         "guard_damage": 0,
	 *                         "start_add_energy": -5,
	 *                         "hit_add_energy": 10,
	 *                         "guard_add_energy": 4,
	 *                         "give_energy": 20,
	 *                         "give_guard_recov": 15,
	 *                         "attack_type": "MIDDLE",
	 *                         "attack_type_id": 2,
	 *                         "impact_x": 10,
	 *                         "impact_y": 0,
	 *                         "hit_area": {
	 *                             "bottom": 415,
	 *                             "top": 385,
	 *                             "left": 557,
	 *                             "right": 642
	 *                         }
	 *
	 *                     }
	 *                     "projectiles": [~, ~, ...] // each entry has the same structure as "attack"
	 *                 },
	 *                 "P2: {~} // same structure as P1
	 *             },
	 *             ... // other frames with the same structure
	 *         ],
	 *         ... // other rounds with the same structure
	 *     ]
	 * }
	 * </pre>
	 *
	 * @param jsonName
	 *            file name for the JSON file
	 */
	public void initJson(String jsonName) {
		File file = new File(jsonName);

		try {
			FileOutputStream fos = new FileOutputStream(file, false);
			this.generator = Json.createGenerator(fos);

			// Open root object
			this.generator.writeStartObject();

			// Write max HP
			this.generator.writeStartObject("max_hp");
			this.generator.write("P1", LaunchSetting.maxHp[0]);
			this.generator.write("P2", LaunchSetting.maxHp[1]);
			this.generator.writeEnd();

			// Write character names
			this.generator.writeStartObject("character_names");
			this.generator.write("P1", LaunchSetting.characterNames[0]);
			this.generator.write("P2", LaunchSetting.characterNames[1]);
			this.generator.writeEnd();

			// Write stage details
			this.generator.writeStartObject("stage_size");
			this.generator.write("x", GameSetting.STAGE_WIDTH);
			this.generator.write("y", GameSetting.STAGE_HEIGHT);
			this.generator.writeEnd();

			// TODO: Combo tables

			// Open rounds array
			this.generator.writeStartArray("rounds");

			// Open frames array
			this.generator.writeStartArray();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Uses this.generator to write the data of a frame in JSON.<br>
	 * Calls to writeStartObject() and writeEnd() are handled
	 * <em>internally</em>.
	 *
	 * @param frameData
	 *            frame data
	 * @param keyDataInput
	 *            data about keys input in this frame
	 */
	public void updateJson(FrameData frameData, KeyData keyDataInput) {
		// Check if this is a new round
		if (frameData.getRound() != this.currentRound) {
			this.generator.writeEnd();
			this.generator.writeStartArray();
			this.currentRound = frameData.getRound();
		}

		// Open frame object
		this.generator.writeStartObject();

		this.generator.write("current_frame", frameData.getFramesNumber());
		this.generator.write("remaining_frames", frameData.getRemainingFramesNumber());

		// Write P1 data
		this.generator.writeStartObject("P1");
		this.writeCharacterDataToJson(frameData.getCharacter(true), keyDataInput.getKeys()[0],
				frameData.getProjectilesByP1());
		this.generator.writeEnd();

		// Write P2 data
		this.generator.writeStartObject("P2");
		this.writeCharacterDataToJson(frameData.getCharacter(false), keyDataInput.getKeys()[1],
				frameData.getProjectilesByP2());
		this.generator.writeEnd();

		// Close frame object
		this.generator.writeEnd(); // Players data

		this.generator.flush();
	}

	/**
	 * Uses this.generator to write the data of a character in JSON.<br>
	 * Calls to writeStartObject() and writeEnd() should be handled <em>by the
	 * caller</em>.
	 *
	 * @param cd
	 *            the data of the character
	 * @param keys
	 *            keys input by the character
	 * @param projectiles
	 *            projectiles currently active generated by this player
	 */
	private void writeCharacterDataToJson(CharacterData cd, Key keys, Deque<AttackData> projectiles) {

		// Character
		this.generator.write("front", cd.isFront());
		this.generator.write("remaining_frames", cd.getRemainingFrame());
		this.generator.write("action", cd.getAction().toString());
		this.generator.write("action_id", cd.getAction().ordinal());
		this.generator.write("state", cd.getState().toString());
		this.generator.write("state_id", cd.getState().ordinal());
		this.generator.write("hp", cd.getHp());
		this.generator.write("energy", cd.getEnergy());
		this.generator.write("x", cd.getX());
		this.generator.write("y", cd.getY());
		this.generator.write("left", cd.getLeft());
		this.generator.write("right", cd.getRight());
		this.generator.write("top", cd.getTop());
		this.generator.write("bottom", cd.getBottom());
		this.generator.write("speed_x", cd.getSpeedX());
		this.generator.write("speed_y", cd.getSpeedY());

		// Agent decision
		this.generator.write("key_a", keys.A);
		this.generator.write("key_b", keys.B);
		this.generator.write("key_c", keys.C);
		this.generator.write("key_up", keys.U);
		this.generator.write("key_down", keys.D);
		this.generator.write("key_left", keys.L);
		this.generator.write("key_right", keys.R);

		// Attack
		AttackData attack = cd.getAttack();
		if (attack != null && attack.getAttackType() != 0) {
			this.generator.writeStartObject("attack"); // Attack
			this.writeAttackToJson(attack);
			this.generator.writeEnd(); // Attack
		}

		this.generator.writeStartArray("projectiles"); // Projectiles
		for (AttackData projectile : projectiles) {
			this.generator.writeStartObject(); // Projectile
			this.writeAttackToJson(projectile);
			this.generator.writeEnd(); // Projectile
		}
		this.generator.writeEnd(); // Projectiles
	}

	/**
	 * Uses this.generator to write data about an attack in JSON.<br>
	 * Calls to writeStartObject() and writeEnd() should be handled <em>by the
	 * caller</em>.
	 *
	 * @param attack
	 *            data about the attack
	 */
	private void writeAttackToJson(AttackData attack) {
		this.generator.write("speed_x", attack.getSpeedX());
		this.generator.write("speed_y", attack.getSpeedY());
		this.generator.write("hit_damage", attack.getHitDamage());
		this.generator.write("guard_damage", attack.getGuardDamage());
		this.generator.write("start_add_energy", attack.getStartAddEnergy());
		this.generator.write("hit_add_energy", attack.getHitAddEnergy());
		this.generator.write("guard_add_energy", attack.getGuardAddEnergy());
		this.generator.write("give_energy", attack.getGiveEnergy());
		this.generator.write("give_guard_recov", attack.getGiveGuardRecov());
		int attackType = attack.getAttackType();
		switch (attackType) {
		case 1:
			this.generator.write("attack_type", "HIGH");
			break;
		case 2:
			this.generator.write("attack_type", "MIDDLE");
			break;
		case 3:
			this.generator.write("attack_type", "LOW");
			break;
		case 4:
			this.generator.write("attack_type", "THROW");
			break;
		default:
			throw new IllegalArgumentException("Unexpected attack type: " + attackType);
		}
		this.generator.write("attack_type_id", attackType);
		this.generator.write("impact_x", attack.getImpactX());
		this.generator.write("impact_y", attack.getImpactY());

		HitArea hitArea = attack.getCurrentHitArea();
		this.generator.writeStartObject("hit_area"); // Hit area
		this.generator.write("bottom", hitArea.getBottom());
		this.generator.write("top", hitArea.getTop());
		this.generator.write("left", hitArea.getLeft());
		this.generator.write("right", hitArea.getRight());
		this.generator.writeEnd(); // Hit area
	}

	/**
	 * Uses this.generator to close the JSON tags that are still open and then
	 * closes the generator.
	 */
	public void finalizeJson() {
		// Close rounds array
		this.generator.writeEnd();

		// Close frames array
		this.generator.writeEnd();

		// Close root object
		this.generator.writeEnd();

		// Close the resources
		this.generator.flush();
		this.generator.close();
	}
}
