package gamescene;

import enumerate.GameSceneName;
import loader.ResourceLoader;
import manager.GraphicManager;
import manager.InputManager;
import setting.GameSetting;
import struct.Key;
import util.MenuItem;

public class FightingMenu extends GameScene {

	// 表示する項目数
	private final int NUMBER_OF_ITEM = 7;
	private MenuItem[] menuItems;
	private String[] aiNames;

	// 現在のカーソル位置
	private int cursorPosition;
	private int numberIndex;
	private int[] playerIndexes;
	private int[] characterIndexes;

	public FightingMenu() {
		// 以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.FIGHTING_MENU;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////
	}

	@Override
	public void initialize() {
		System.out.println("Menu initialize");
		// Initialization

		this.menuItems = new MenuItem[] {
				new MenuItem("PLAY ", 50, 50, 0),
				new MenuItem("PLAYER1 : ", 75, 90, 1),
				new MenuItem("PLAYER2 : ", 75, 130, 2),
				new MenuItem("CHARACTER1 : ", 75, 170, 3),
				new MenuItem("CHARACTER2 : ", 75, 210, 4),
				new MenuItem("Repeat Count : ", 50, 260, 5),
				new MenuItem("RETURN ", 50, 310, 6) };

		this.playerIndexes = new int[2];
		this.characterIndexes = new int[2];
		this.cursorPosition = 0;
		this.numberIndex = 0;

		this.aiNames = ResourceLoader.getInstance().loadFileNames("./data/ai", ".jar");
		if (aiNames == null) {
			aiNames = new String[1];
			aiNames[0] = "None";
		}
	}

	@Override
	public void update() {
		Key key = InputManager.getInstance().getKeyData().getKeys()[0];

		if (key.U) {
			if (cursorPosition == 0) {
				cursorPosition = menuItems[NUMBER_OF_ITEM - 1].getCursorPosition();
			} else {
				cursorPosition = menuItems[cursorPosition - 1].getCursorPosition();
			}

		}

		if (key.D) {
			if (cursorPosition == NUMBER_OF_ITEM - 1) {
				cursorPosition = menuItems[0].getCursorPosition();
			} else {
				cursorPosition = menuItems[cursorPosition + 1].getCursorPosition();
			}
		}

		switch (cursorPosition) {
		case 0:
			if (key.A) {
				System.out.println("Play遷移 !");
				// Launcherの次の遷移先を登録
				// Launcher launcher = new Launcher(GameSceneName.PLAY);
			}
			break;

		case 1:
			if (key.R) {
				if (playerIndexes[0] == aiNames.length - 1) {
					playerIndexes[0] = 0;
				} else {
					playerIndexes[0]++;
				}
			}
			if (key.L) {
				if (playerIndexes[0] == 0) {
					playerIndexes[0] = aiNames.length - 1;
				} else {
					playerIndexes[0]--;
				}
			}
			break;

		case 2:
			if (key.R) {
				if (playerIndexes[1] == aiNames.length - 1) {
					playerIndexes[1] = 0;
				} else {
					playerIndexes[1]++;
				}
			}
			if (key.L) {
				if (playerIndexes[1] == 0) {
					playerIndexes[1] = aiNames.length - 1;
				} else {
					playerIndexes[1]--;
				}
			}
			break;

		case 3:
			if (key.R) {
				if (characterIndexes[0] == GameSetting.CHARACTERS.length - 1) {
					characterIndexes[0] = 0;
				} else {
					characterIndexes[0]++;
				}
			}
			if (key.L) {
				if (characterIndexes[0] == 0) {
					characterIndexes[0] = GameSetting.CHARACTERS.length - 1;
				} else {
					characterIndexes[0]--;
				}
			}
			break;

		case 4:
			if (key.R) {
				if (characterIndexes[1] == GameSetting.CHARACTERS.length - 1) {
					characterIndexes[1] = 0;
				} else {
					characterIndexes[1]++;
				}
			}
			if (key.L) {
				if (characterIndexes[1] == 0) {
					characterIndexes[1] = GameSetting.CHARACTERS.length - 1;
				} else {
					characterIndexes[1]--;
				}
			}
			break;

		case 5:
			if (key.R) {
				if (numberIndex == GameSetting.REPEAT_NUMBERS.length - 1) {
					numberIndex = 0;
				} else {
					numberIndex++;
				}
			}
			if (key.L) {
				if (numberIndex == 0) {
					numberIndex = GameSetting.REPEAT_NUMBERS.length - 1;
				} else {
					numberIndex--;
				}
			}
			break;

		case 6:
			if (key.A) {
				HomeMenu homeMenu = new HomeMenu(); // 次のシーンのコンストラクタ作成
				this.setTransitioFlag(true); // 現在のシーンからの遷移要求をtrueに
				this.setNextGameScene(homeMenu); // 次のシーンをセットする
			}
			break;

		default:
			break;
		}

		this.drawScreen();

	}

	public void drawScreen() {
		GraphicManager.getInstance().drawString(menuItems[0].getString(), menuItems[0].getCoordinateX(),
				menuItems[0].getCoordinateY());
		GraphicManager.getInstance().drawString(menuItems[1].getString() + aiNames[playerIndexes[0]],
				menuItems[1].getCoordinateX(), menuItems[1].getCoordinateY());
		GraphicManager.getInstance().drawString(menuItems[2].getString() + aiNames[playerIndexes[1]],
				menuItems[2].getCoordinateX(), menuItems[2].getCoordinateY());
		GraphicManager.getInstance().drawString(menuItems[3].getString() + GameSetting.CHARACTERS[characterIndexes[0]],
				menuItems[3].getCoordinateX(), menuItems[3].getCoordinateY());
		GraphicManager.getInstance().drawString(menuItems[4].getString() + GameSetting.CHARACTERS[characterIndexes[1]],
				menuItems[4].getCoordinateX(), menuItems[4].getCoordinateY());
		GraphicManager.getInstance().drawString(menuItems[5].getString() + GameSetting.REPEAT_NUMBERS[numberIndex],
				menuItems[5].getCoordinateX(), menuItems[5].getCoordinateY());
		GraphicManager.getInstance().drawString(menuItems[6].getString(), menuItems[6].getCoordinateX(),
				menuItems[6].getCoordinateY());
		GraphicManager.getInstance().drawString("=>", menuItems[cursorPosition].getCoordinateX() - 30,
				menuItems[cursorPosition].getCoordinateY());
	}

	@Override
	public void close() {
		System.out.println("Menu close");

	}

}