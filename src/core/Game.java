package core;

import java.awt.Font;
import java.io.File;

import enumerate.BackgroundType;
import gamescene.HomeMenu;
import image.LetterImage;
import manager.GameManager;
import manager.GraphicManager;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import util.DeleteFiles;

public class Game extends GameManager {

	private String[] aiNames;

	private String[] characterNames;

	private int repeatNumber;

	private BackgroundType backgroundType;

	private int py4jPort;

	private int invertedPlayer;

	public Game() {
		super();

		this.aiNames = new String[2];
		this.characterNames = new String[2];
		this.repeatNumber = 1;
		this.backgroundType = BackgroundType.Image;
		this.py4jPort = 4242;
		this.invertedPlayer = 0;
	}

	public void setOptions(String[] options){
		// Read the configurations here
				for (int i = 0; i < options.length; ++i) {
					switch (options[i]) {
					/*	case "-a":
						case "--all":
							allCombinationFlag = true;
							break; */
					case "-n":
					case "--number":
						LaunchSetting.repeatNumber = Integer.parseInt(options[++i]);
						FlagSetting.automationFlag = true;
						break;
					case "--a1":
						LaunchSetting.aiNames[0] = options[++i];
						break;
					case "--a2":
						LaunchSetting.aiNames[1] = options[++i];
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
					/* case "-off":
						LogSystem.getInstance().logger.setLevel(Level.OFF);
						break; */
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
						LaunchSetting.backgroundType = BackgroundType.Black;
						break;
					case "--grey-bg":
						LaunchSetting.backgroundType = BackgroundType.Grey;
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
		//各マネージャの初期化
		Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
		GraphicManager.getInstance().setLetterFont(new LetterImage(awtFont, true));

		createLogDirectories();

		HomeMenu homeMenu = new HomeMenu();
		this.startGame(homeMenu);

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
	 * */
	private void createLogDirectories(){
		new File("log").mkdir();
		new File("log/replay").mkdir();
		new File("log/point").mkdir();
	}


	@Override
	public void close() {

	}

}
