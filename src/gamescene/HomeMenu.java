package gamescene;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;

import enumerate.GameSceneName;
import informationcontainer.MenuItem;
import input.Keyboard;
import loader.ResourceLoader;
import manager.GraphicManager;
import manager.InputManager;
import manager.SoundManager;
import setting.ResourceSetting;
import struct.Key;

public class HomeMenu extends GameScene {

	// 表示する項目数
	private final int NUMBER_OF_ITEM = 3;
	private MenuItem[] menuItems = new MenuItem[NUMBER_OF_ITEM];
	private ArrayList<String> allReplayNames;

	// 現在のカーソル位置
	private int cursorPosition;
	// 現在選択されているreplayのIndex
	private int replayIndex;
	private Keyboard keyboard;
	private SoundManager sm;

	public HomeMenu() {
		// 以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.HOME_MENU;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////
	}

	@Override
	public void initialize() {
		System.out.println("Home Menu initialize");
		InputManager.getInstance().setSceneName(GameSceneName.HOME_MENU);
		// Initialization
		this.menuItems = new MenuItem[] { new MenuItem("FIGHT ", 50, 50, 0), new MenuItem("REPLAY : ", 50, 100, 1),
				new MenuItem("EXIT ", 50, 310, 2) };

		this.cursorPosition = 0;
		this.replayIndex = 0;

		this.allReplayNames = ResourceLoader.getInstance().loadFileNames("./log/replay/", ".dat");
		if (allReplayNames.size() == 0) {
			allReplayNames.add("None");
		}
		sm = SoundManager.getInstance();
		ResourceLoader.getInstance().loadResource();

		this.keyboard = InputManager.getInstance().getKeyboard();
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
		if (keyboard.getKeyDown(GLFW_KEY_1)) {
			sm.play(sm.getSoundEffect().get(ResourceSetting.SOUND_EFFECTS[0]));
		}
		if (keyboard.getKeyDown(GLFW_KEY_2)) {
			sm.play(sm.getSoundEffect().get(ResourceSetting.SOUND_EFFECTS[1]));
		}
		if (keyboard.getKeyDown(GLFW_KEY_3)) {
			sm.play(sm.getSoundEffect().get(ResourceSetting.SOUND_EFFECTS[2]));
		}
		if (keyboard.getKeyDown(GLFW_KEY_4)) {
			sm.play(sm.getSoundEffect().get(ResourceSetting.SOUND_EFFECTS[3]));
		}
		if (keyboard.getKeyDown(GLFW_KEY_5)) {
			sm.play(sm.getSoundEffect().get(ResourceSetting.SOUND_EFFECTS[4]));
		}
		if (keyboard.getKeyDown(GLFW_KEY_6)) {
			sm.play(sm.getBackGroundMusic());
		}
		if (keyboard.getKeyDown(GLFW_KEY_7)) {
			sm.stop(sm.getBackGroundMusic());
		}

		switch (cursorPosition) {
		case 0:
			if (key.A) {
				FightingMenu fightingMenu = new FightingMenu(); // 次のシーンのコンストラクタ作成
				this.setTransitionFlag(true); // 現在のシーンからの遷移要求をtrueに
				this.setNextGameScene(fightingMenu); // 次のシーンをセットする
			}
			break;

		case 1:
			if (key.R) {
				if (replayIndex == allReplayNames.size() - 1) {
					replayIndex = 0;
				} else {
					replayIndex++;
				}
			}
			if (key.L) {
				if (replayIndex == 0) {
					replayIndex = allReplayNames.size() - 1;
				} else {
					replayIndex--;
				}
			}
			if (key.A) {
				// Launcherの次の遷移先を登録
				Launcher launcher = new Launcher(GameSceneName.REPLAY);
				this.setTransitionFlag(true); // 現在のシーンからの遷移要求をtrueに
				this.setNextGameScene(launcher); // 次のシーンをセットする

			}
			break;

		case 2:
			if (key.A) {
				sm.close();
				this.setGameEndFlag(true);
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
		GraphicManager.getInstance().drawString(menuItems[1].getString() + allReplayNames.get(replayIndex),
				menuItems[1].getCoordinateX(), menuItems[1].getCoordinateY());
		GraphicManager.getInstance().drawString(menuItems[2].getString(), menuItems[2].getCoordinateX(),
				menuItems[2].getCoordinateY());
		GraphicManager.getInstance().drawString("=>", menuItems[cursorPosition].getCoordinateX() - 30,
				menuItems[cursorPosition].getCoordinateY());
	}

	@Override
	public void close() {
		System.out.println("Menu close");
	}

}
