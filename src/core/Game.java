package core;

import java.awt.Font;

import enumerate.BackgroundType;
import gamescene.Launcher;
import image.LetterImage;
import manager.GameManager;
import manager.GraphicManager;
import setting.FlagSetting;
import setting.GameSetting;
import util.DeleteFiles;

public class Game extends GameManager {

	private String[] aiNames;

	private String[] characterNames;

	private int number = 1;

	private BackgroundType backgroundType = BackgroundType.Image;

	private boolean py4j = false;
	private int py4j_port = 4242;

	private int invertedPlayer = 0;

	private static int p1MaxHp = 0;

	private static int p2MaxHp = 0;

	public Game(String[] options) {
		super();

		String[] aiName = new String[2];
		String[] characterName = new String[2];

		// Read the configurations here
		for (int i = 0; i < options.length; ++i) {
			switch (options[i]) {
			/*	case "-a":
				case "--all":
					allCombinationFlag = true;
					break; */
			case "-n":
			case "--number":
				number = Integer.parseInt(options[++i]);
				FlagSetting.automationFlag = true;
				break;
			case "--a1":
				aiName[0] = options[++i];
				break;
			case "--a2":
				aiName[1] = options[++i];
				break;
			case "--c1":
				characterName[0] = getCharacterName(options[++i]);
				break;
			case "--c2":
				characterName[1] = getCharacterName(options[++i]);
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
				py4j = true;
				break;
			case "--port":
				py4j_port = Integer.parseInt(options[++i]);
				break;
			case "--black-bg":
				this.backgroundType = BackgroundType.Black;
				break;
			case "--grey-bg":
				this.backgroundType = BackgroundType.Grey;
				break;
			case "--inverted-player":
				invertedPlayer = Integer.parseInt(options[++i]);
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
				p1MaxHp = Integer.parseInt(options[++i]);
				p2MaxHp = Integer.parseInt(options[++i]);
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

		//Menu menu = new Menu();
		Launcher launcher = new Launcher();
		this.startGame(launcher);

	}

	private String getCharacterName(String characterName) {
		for (String character : GameSetting.CHARACTERS) {
			if (character.equals(characterName)) {
				return character;
			}
		}
		return null;
	}

	@Override
	public void close() {

	}

}
