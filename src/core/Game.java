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
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import util.DeleteFiles;

public class Game extends GameManager {

	public Game() {
		super();
	}

	public void setOptions(String[] options) {
		// Read the configurations here
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
				LaunchSetting.deviceTypes[0] = 1;
				break;
			case "--a2":
				LaunchSetting.aiNames[1] = options[++i];
				LaunchSetting.deviceTypes[1] = 1;
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
			/*
			 * case "-off": LogSystem.getInstance().logger.setLevel(Level.OFF);
			 * break;
			 */
			case "-del":
				DeleteFiles.getInstance().deleteFiles();
				break;
			case "--py4j":
				FlagSetting.py4j = true;
				break;
			case "--port":
				LaunchSetting.py4jPort = Integer.parseInt(options[++i]);
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
			case "--disable-window":
			case "--mute":
				FlagSetting.muteFlag = true;
				// Handle in the main
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
			case "--err-log":
				FlagSetting.outputErrorAndLogFlag = true;
				break;
			default:
				System.err.println("arguments error: unknown format is exist. -> " + options[i] + " ?");
			}
		}

	}

	@Override
	public void initialize() {
		// 各マネージャの初期化
		Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
		GraphicManager.getInstance().setLetterFont(new LetterImage(awtFont, true));

		createLogDirectories();

		if (FlagSetting.automationFlag || FlagSetting.allCombinationFlag) {
			if (FlagSetting.allCombinationFlag) {
				AIContainer.allAINameList = ResourceLoader.getInstance().loadFileNames("./data/ai", ".jar");

				if (AIContainer.allAINameList.size() < 2) {
					Logger.getAnonymousLogger().log(Level.INFO, "Cannot launch FightingICE with Round-robin mode.");
					this.isExitFlag = true;
				}
			}

			Launcher launcher = new Launcher(GameSceneName.PLAY);
			this.startGame(launcher);

		} else if (FlagSetting.py4j) {
			Python python = new Python();
			this.startGame(python);

		} else {
			HomeMenu homeMenu = new HomeMenu();
			this.startGame(homeMenu);
		}

	}

	private String getCharacterName(String characterName) {
		for (String character : GameSetting.CHARACTERS) {
			if (character.equals(characterName)) {
				return character;
			}
		}
		return null;
	}

	/**
	 * Create logs directories if not present
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
