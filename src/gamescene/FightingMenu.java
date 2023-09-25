package gamescene;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import java.util.ArrayList;

import enumerate.GameSceneName;
import grpc.PlayerAgent;
import informationcontainer.MenuItem;
import input.Keyboard;
import loader.ResourceLoader;
import manager.GraphicManager;
import manager.InputManager;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import setting.ResourceSetting;
import struct.Key;

/**
 * 使用AI, 使用キャラクター, 繰り返し回数をセットするメニュー画面を扱うクラス．
 */
public class FightingMenu extends GameScene {

	public static final String DEFAULT_AI_NAME = "Keyboard";
	public static final String DEFAULT_SOUND_NAME = "Default";
	public static final int DEFAULT_AI_INDEX = 1;
	public static final int GRPC_INDEX = 0;
	
	/**
	 * 画面に表示する項目数．
	 */
	private final int NUMBER_OF_ITEM = 8;

	/**
	 * 表示する各項目のインデックス,名前,座標を格納している配列．
	 */
	private MenuItem[] menuItems;

	/**
	 * aiフォルダ内にある全AIの名前を格納したリスト．
	 */
	private ArrayList<String> allAiNames;
	
	private ArrayList<String> allSounds;

	/**
	 * 現在のカーソル位置．
	 */
	private int cursorPosition;

	/**
	 * 繰り返し回数(Repeat Count)の項目における現在の選択位置．
	 */
	private int numberIndex;
	
	private int soundIndex;

	/**
	 * PLAYERの項目における現在の選択位置．<br>
	 * Index 0: P1; Index 1: P2
	 */
	private int[] playerIndexes;

	/**
	 * CHARACTERの項目における現在の選択位置．<br>
	 * Index 0: P1; Index 1: P2
	 */
	private int[] characterIndexes;

	/** クラスコンストラクタ． */
	public FightingMenu() {
		// 以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.FIGHTING_MENU;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////
	}

	private boolean isGrpcReady() {
		if (LaunchSetting.grpcServer == null) {
			return true;
		} else if (this.playerIndexes[0] == GRPC_INDEX && LaunchSetting.grpcServer.getPlayer(true).isCancelled()) {
			return false;
		} else if (this.playerIndexes[1] == GRPC_INDEX && LaunchSetting.grpcServer.getPlayer(false).isCancelled()) {
			return false;
		}
		return true;
	}
	
	@Override
	public void initialize() {
		InputManager.getInstance().setSceneName(GameSceneName.FIGHTING_MENU);

		this.menuItems = new MenuItem[] { 
				new MenuItem("PLAY", 50, 50, 0), new MenuItem("Player 1: ", 75, 90, 1),
				new MenuItem("Player 2: ", 75, 130, 2), new MenuItem("Character 1: ", 75, 170, 3),
				new MenuItem("Character 2: ", 75, 210, 4), new MenuItem("Repeat Count: ", 50, 260, 5),
				new MenuItem("Sound: ", 50, 310, 6), new MenuItem("RETURN", 50, 360, 7) 
				};

		this.playerIndexes = new int[2];
		this.characterIndexes = new int[2];
		this.cursorPosition = 0;
		this.numberIndex = 0;
		this.soundIndex = 0;

		this.allAiNames = ResourceLoader.getInstance().loadFileNames("./data/ai", ".jar");
		if (FlagSetting.grpc) {
			allAiNames.add(GRPC_INDEX, "gRPC");
			allAiNames.add(DEFAULT_AI_INDEX, DEFAULT_AI_NAME);
		} else {
			allAiNames.add(0, DEFAULT_AI_NAME);
		}
		
		this.allSounds = ResourceLoader.getInstance().loadSoundNames();
		allSounds.add(0, DEFAULT_SOUND_NAME);
	}

	@Override
	public void update() {
		Key key = InputManager.getInstance().getKeyData().getKeys()[0];

		if (key.U) {
			if (this.cursorPosition == 0) {
				this.cursorPosition = this.menuItems[this.NUMBER_OF_ITEM - 1].getCursorPosition();
			} else {
				this.cursorPosition = this.menuItems[this.cursorPosition - 1].getCursorPosition();
			}
		}

		if (key.D) {
			if (this.cursorPosition == this.NUMBER_OF_ITEM - 1) {
				this.cursorPosition = this.menuItems[0].getCursorPosition();
			} else {
				this.cursorPosition = this.menuItems[this.cursorPosition + 1].getCursorPosition();
			}
		}

		switch (this.cursorPosition) {
		// PLAYの項目の位置のとき
		case 0:
			if (key.A && this.isGrpcReady()) {
				// Launch情報をセット
				for (int i = 0; i < 2; i++) {
					LaunchSetting.aiNames[i] = this.allAiNames.get(this.playerIndexes[i]);
					LaunchSetting.characterNames[i] = GameSetting.CHARACTERS[this.characterIndexes[i]];

					if (LaunchSetting.aiNames[i].equals(DEFAULT_AI_NAME)) {
						LaunchSetting.deviceTypes[i] = InputManager.DEVICE_TYPE_KEYBOARD;
					} else if (this.playerIndexes[i] == GRPC_INDEX && FlagSetting.grpc) {
						LaunchSetting.aiNames[i] = LaunchSetting.grpcServer.getPlayer(i == 0).getPlayerName();
						LaunchSetting.deviceTypes[i] = InputManager.DEVICE_TYPE_GRPC;
					} else {
						LaunchSetting.deviceTypes[i] = InputManager.DEVICE_TYPE_AI;
					}
				}

				LaunchSetting.repeatNumber = GameSetting.REPEAT_NUMBERS[this.numberIndex];
				if (LaunchSetting.repeatNumber > 1) {
					FlagSetting.automationFlag = true;
				}

				LaunchSetting.soundName = this.allSounds.get(this.soundIndex);
				if (LaunchSetting.soundName.equals(DEFAULT_SOUND_NAME)) {
					ResourceSetting.SOUND_DIRECTORY = "./data/sounds/";
				} else {
					ResourceSetting.SOUND_DIRECTORY = String.format("./data/sounds/%s/", LaunchSetting.soundName);
				}

				// Launcherの次の遷移先を登録
				Launcher launcher = new Launcher(GameSceneName.PLAY);
				this.setTransitionFlag(true);
				this.setNextGameScene(launcher);
			}
			break;

		// PLAYER1の位置のとき
		case 1:
			if (key.R) {
				if (this.playerIndexes[0] == this.allAiNames.size() - 1) {
					this.playerIndexes[0] = 0;
				} else {
					this.playerIndexes[0]++;
				}
			}
			if (key.L) {
				if (this.playerIndexes[0] == 0) {
					this.playerIndexes[0] = this.allAiNames.size() - 1;
				} else {
					this.playerIndexes[0]--;
				}
			}
			break;

		// PLAYER2の位置のとき
		case 2:
			if (key.R) {
				if (this.playerIndexes[1] == this.allAiNames.size() - 1) {
					this.playerIndexes[1] = 0;
				} else {
					this.playerIndexes[1]++;
				}
			}
			if (key.L) {
				if (this.playerIndexes[1] == 0) {
					this.playerIndexes[1] = this.allAiNames.size() - 1;
				} else {
					this.playerIndexes[1]--;
				}
			}
			break;

		// CHARACTER1の位置のとき
		case 3:
			if (key.R) {
				if (this.characterIndexes[0] == GameSetting.CHARACTERS.length - 1) {
					this.characterIndexes[0] = 0;
				} else {
					this.characterIndexes[0]++;
				}
			}
			if (key.L) {
				if (this.characterIndexes[0] == 0) {
					this.characterIndexes[0] = GameSetting.CHARACTERS.length - 1;
				} else {
					this.characterIndexes[0]--;
				}
			}
			break;

		// CHARACTER2の位置のとき
		case 4:
			if (key.R) {
				if (this.characterIndexes[1] == GameSetting.CHARACTERS.length - 1) {
					this.characterIndexes[1] = 0;
				} else {
					this.characterIndexes[1]++;
				}
			}
			if (key.L) {
				if (this.characterIndexes[1] == 0) {
					this.characterIndexes[1] = GameSetting.CHARACTERS.length - 1;
				} else {
					this.characterIndexes[1]--;
				}
			}
			break;
		// Repeat Countの位置のとき
		case 5:
			if (key.R) {
				if (this.numberIndex == GameSetting.REPEAT_NUMBERS.length - 1) {
					this.numberIndex = 0;
				} else {
					this.numberIndex++;
				}
			}
			if (key.L) {
				if (this.numberIndex == 0) {
					this.numberIndex = GameSetting.REPEAT_NUMBERS.length - 1;
				} else {
					this.numberIndex--;
				}
			}
			break;
		// Sound
		case 6:
			if (key.R) {
				if (this.soundIndex == this.allSounds.size() - 1) {
					this.soundIndex = 0;
				} else {
					this.soundIndex++;
				}
			}
			if (key.L) {
				if (this.soundIndex == 0) {
					this.soundIndex = this.allSounds.size() - 1;
				} else {
					this.soundIndex--;
				}
			}
			break;
		// RETURNの位置のとき
		case 7:
			if (key.A) {
				HomeMenu homeMenu = new HomeMenu();
				this.setTransitionFlag(true);
				this.setNextGameScene(homeMenu);
			}
			break;

		default:
			break;
		}

		if (Keyboard.getKeyDown(GLFW_KEY_ESCAPE)) {
			HomeMenu homeMenu = new HomeMenu();
			this.setTransitionFlag(true);
			this.setNextGameScene(homeMenu);
		}
		// 画面の描画
		this.drawScreen();
	}

	private String getAIName(int i) {
		if (FlagSetting.grpc) {
			PlayerAgent player = LaunchSetting.grpcServer.getPlayer(i == 0);
			if (this.playerIndexes[i] == GRPC_INDEX && !player.isCancelled()) {
				return String.format("%s (%s AI)", player.getPlayerName(), player.isBlind() ? "Blind" : "Visual");
			}
		}
		return this.allAiNames.get(this.playerIndexes[i]);
	}
	
	/**
	 * 対戦の設定を行うメニュー画面を描画する．
	 */
	private void drawScreen() {
		GraphicManager.getInstance().drawString(this.menuItems[0].getString(), this.menuItems[0].getCoordinateX(), 
				this.menuItems[0].getCoordinateY());
		GraphicManager.getInstance().drawString(this.menuItems[1].getString() + this.getAIName(0),
				this.menuItems[1].getCoordinateX(), this.menuItems[1].getCoordinateY());
		GraphicManager.getInstance().drawString(this.menuItems[2].getString() + this.getAIName(1),
				this.menuItems[2].getCoordinateX(), this.menuItems[2].getCoordinateY());
		GraphicManager.getInstance().drawString(this.menuItems[3].getString() + GameSetting.CHARACTERS[this.characterIndexes[0]],
				this.menuItems[3].getCoordinateX(), this.menuItems[3].getCoordinateY());
		GraphicManager.getInstance().drawString(this.menuItems[4].getString() + GameSetting.CHARACTERS[this.characterIndexes[1]],
				this.menuItems[4].getCoordinateX(), this.menuItems[4].getCoordinateY());
		GraphicManager.getInstance().drawString(this.menuItems[5].getString() + GameSetting.REPEAT_NUMBERS[this.numberIndex],
				this.menuItems[5].getCoordinateX(), this.menuItems[5].getCoordinateY());
		GraphicManager.getInstance().drawString(this.menuItems[6].getString() + this.allSounds.get(this.soundIndex),
				this.menuItems[6].getCoordinateX(), this.menuItems[6].getCoordinateY());
		GraphicManager.getInstance().drawString(this.menuItems[7].getString(), this.menuItems[7].getCoordinateX(), 
				this.menuItems[7].getCoordinateY());
		GraphicManager.getInstance().drawString("=>", this.menuItems[this.cursorPosition].getCoordinateX() - 30,
				this.menuItems[this.cursorPosition].getCoordinateY());
	}

	@Override
	public void close() {
		this.menuItems = null;
		this.allAiNames.clear();
		this.allSounds.clear();
		this.characterIndexes = null;
		this.playerIndexes = null;
	}

}