package core;

import java.awt.Font;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import enumerate.BackgroundType;
import enumerate.GameSceneName;
import gamescene.HomeMenu;
import gamescene.Launcher;
import gamescene.Python;
import image.LetterImage;
import informationcontainer.AIContainer;
import loader.ResourceLoader;
import manager.GameManager;
import manager.GraphicManager;
import manager.InputManager;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import util.DeleteFiles;

/**
 * ゲームの起動情報を設定し, 開始するゲームシーンを設定するクラス．
 */
public class Game extends GameManager {

	/**
	 * 親クラスであるGameManagerを初期化するクラスコンストラクタ．
	 */
	public Game() {
		super();
	}

	/**
	 * 起動時の引数を基に, ゲームの起動情報をセットする.
	 *
	 * @param options
	 *            起動時に入力した全ての引数を格納した配列
	 */
	public void setOptions(String[] options) {
		// Reads the configurations here
		for (int i = 0; i < options.length; ++i) {
			switch (options[i]) {
			case "-a":
			case "--all":
				FlagSetting.allCombinationFlag = true;
				LaunchSetting.deviceTypes = new char[] { 1, 1 };
				break;
			case "-n":
			case "--number":
				LaunchSetting.repeatNumber = Integer.parseInt(options[++i]);
				FlagSetting.automationFlag = true;
				break;
			case "--a1":
				LaunchSetting.aiNames[0] = options[++i];
				LaunchSetting.deviceTypes[0] = InputManager.DEVICE_TYPE_AI;
				break;
			case "--a2":
				LaunchSetting.aiNames[1] = options[++i];
				LaunchSetting.deviceTypes[1] = InputManager.DEVICE_TYPE_AI;
				break;
			case "--c1":
				LaunchSetting.characterNames[0] = getCharacterName(options[++i]);
				break;
			case "--c2":
				LaunchSetting.characterNames[1] = getCharacterName(options[++i]);
				break;
			case "-da":
				FlagSetting.debugActionFlag = true;
				break;
			case "-df":
				FlagSetting.debugFrameDataFlag = true;
				break;
			case "-t":
				FlagSetting.trainingModeFlag = true;
				break;
			case "-del":
				DeleteFiles.getInstance().deleteFiles();
				break;
			case "--py4j":
				FlagSetting.py4j = true;
				break;
			case "--port":
				LaunchSetting.py4jPort = Integer.parseInt(options[++i]);
				break;
			case "-r":
				// -r 100 -> 1 game has 100 rounds
				GameSetting.ROUND_MAX = Integer.parseInt(options[++i]);
				break;
			case "-f":
				// -f 360 -> 1 round has 6 second
				GameSetting.ROUND_FRAME_NUMBER = Integer.parseInt(options[++i]);
				break;
			case "--black-bg":
				LaunchSetting.backgroundType = BackgroundType.BLACK;
				break;
			case "--grey-bg":
				LaunchSetting.backgroundType = BackgroundType.GREY;
				break;
			case "--inverted-player":
				LaunchSetting.invertedPlayer = Integer.parseInt(options[++i]);
				break;
			case "--mute":
				FlagSetting.muteFlag = true;
				break;
			case "--disable-window":
				FlagSetting.enableWindow = false;
				FlagSetting.muteFlag = true;
				FlagSetting.automationFlag = true;
				break;
			case "--fastmode":
				FlagSetting.fastModeFlag = true;
				FlagSetting.automationFlag = true;
				break;
			case "--json":
				FlagSetting.jsonFlag = true;
				break;
			case "--limithp":
				// --limithp P1_HP P2_HP
				FlagSetting.limitHpFlag = true;
				LaunchSetting.maxHp[0] = Integer.parseInt(options[++i]);
				LaunchSetting.maxHp[1] = Integer.parseInt(options[++i]);
				break;
			case "--slow":
				FlagSetting.slowmotion = true;
				break;
			case "--err-log":
				FlagSetting.outputErrorAndLogFlag = true;
				break;
			case "--sound-only":
				FlagSetting.soundOnly = true;
				break;
			default:
				Logger.getAnonymousLogger().log(Level.WARNING,
						"Arguments error: unknown format is exist. -> " + options[i] + " ?");
			}
		}

	}

	@Override
	public void initialize() {
		// 使用フォントの初期化
		Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
		GraphicManager.getInstance().setLetterFont(new LetterImage(awtFont, true));

		createLogDirectories();

		// -nまたは-aが指定されたときは, メニュー画面に行かず直接ゲームをLaunchする
		if ((FlagSetting.automationFlag || FlagSetting.allCombinationFlag) && !FlagSetting.py4j) {
			if (FlagSetting.allCombinationFlag) {
				AIContainer.allAINameList = ResourceLoader.getInstance().loadFileNames("./data/ai", ".jar");

				if (AIContainer.allAINameList.size() < 2) {
					Logger.getAnonymousLogger().log(Level.INFO, "Cannot launch FightingICE with Round-robin mode.");
					this.isExitFlag = true;
				}
			}

			Launcher launcher = new Launcher(GameSceneName.PLAY);
			this.startGame(launcher);

			// -Python側で起動するときは, Pythonシーンからゲームを開始する
		} else if (FlagSetting.py4j) {
			System.out.println("Python");
			Python python = new Python();
			this.startGame(python);

			// 上記以外の場合, メニュー画面からゲームを開始する
		} else {
			HomeMenu homeMenu = new HomeMenu();
			this.startGame(homeMenu);
		}

	}

	/**
	 * 引数で指定されたキャラクター名が使用可能キャラクター内にあるかどうかを検索し, ある場合はその名前を返す．<br>
	 * 無ければ警告文を出し, ZENをデフォルトキャラクターとして返す．
	 *
	 * @param characterName
	 *            検索するキャラクター名
	 *
	 * @return 使用キャラクター名
	 */
	private String getCharacterName(String characterName) {
		for (String character : GameSetting.CHARACTERS) {
			if (character.equals(characterName)) {
				return character;
			}
		}
		Logger.getAnonymousLogger().log(Level.WARNING,
				characterName + " is does not exist. Please check the set character name.");
		return "ZEN"; // Default character
	}

	/**
	 * Creates log directories if they do not exist.
	 */
	private void createLogDirectories() {
		new File("log").mkdir();
		new File("log/replay").mkdir();
		new File("log/point").mkdir();
	}

	@Override
	public void close() {
		this.currentGameScene = null;
	}

}
