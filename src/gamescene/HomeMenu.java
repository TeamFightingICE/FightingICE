package gamescene;

import java.util.ArrayList;

import enumerate.GameSceneName;
import informationcontainer.MenuItem;
import loader.ResourceLoader;
import manager.GraphicManager;
import manager.InputManager;
import setting.LaunchSetting;
import struct.Key;

/**
 * 対戦メニュー画面, リプレイ再生, ゲーム終了を選択するメニュー画面を扱うクラス．
 */
public class HomeMenu extends GameScene {

	/**
	 * 画面に表示する項目数．
	 */
	private final int NUMBER_OF_ITEM = 3;

	/**
	 * 表示する各項目のインデックス,名前,座標を格納している配列．
	 */
	private MenuItem[] menuItems;

	/**
	 * replayフォルダ内にある全replayの名前を格納したリスト．
	 */
	private ArrayList<String> allReplayNames;

	/**
	 * 現在のカーソル位置．
	 */
	private int cursorPosition;

	/**
	 * REPLAYの項目における現在の選択位置．
	 */
	private int replayIndex;

	/**
	 * クラスコンストラクタ．
	 */
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
		InputManager.getInstance().setSceneName(GameSceneName.HOME_MENU);

		this.menuItems = new MenuItem[] {
				new MenuItem("FIGHT ", 50, 50, 0),
				new MenuItem("REPLAY : ", 50, 100, 1),
				new MenuItem("EXIT ", 50, 310, 2)
		};
		this.cursorPosition = 0;
		this.replayIndex = 0;

		this.allReplayNames = ResourceLoader.getInstance().loadFileNames("./log/replay/", ".dat");
		if (this.allReplayNames.size() == 0) {
			allReplayNames.add("None");
		}

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
		// FIGHTの位置のとき
		case 0:
			if (key.A) {
				FightingMenu fightingMenu = new FightingMenu(); // 次のシーンのコンストラクタ作成
				this.setTransitionFlag(true); // 現在のシーンからの遷移要求をtrueに
				this.setNextGameScene(fightingMenu); // 次のシーンをセットする
			}
			break;

		// REPLAYの位置のとき
		case 1:
			if (key.R) {
				if (this.replayIndex == this.allReplayNames.size() - 1) {
					this.replayIndex = 0;
				} else {
					this.replayIndex++;
				}
			}

			if (key.L) {
				if (this.replayIndex == 0) {
					this.replayIndex = this.allReplayNames.size() - 1;
				} else {
					this.replayIndex--;
				}
			}

			if (key.A) {
				LaunchSetting.replayName = this.allReplayNames.get(this.replayIndex);
				// Launcherの次の遷移先を登録
				Launcher launcher = new Launcher(GameSceneName.REPLAY);
				this.setTransitionFlag(true); // 現在のシーンからの遷移要求をtrueに
				this.setNextGameScene(launcher); // 次のシーンをセットする

			}
			break;

		// EXITの位置のとき
		case 2:
			if (key.A) {
				this.setGameEndFlag(true);
			}
			break;

		default:
			break;
		}

		this.drawScreen();

	}

	/**
	 * 対戦の設定を行うメニュー画面を描画する．
	 */
	private void drawScreen() {
		GraphicManager.getInstance().drawString(this.menuItems[0].getString(), this.menuItems[0].getCoordinateX(),
				this.menuItems[0].getCoordinateY());
		GraphicManager.getInstance().drawString(
				this.menuItems[1].getString() + this.allReplayNames.get(this.replayIndex),
				this.menuItems[1].getCoordinateX(), this.menuItems[1].getCoordinateY());
		GraphicManager.getInstance().drawString(this.menuItems[2].getString(), this.menuItems[2].getCoordinateX(),
				this.menuItems[2].getCoordinateY());
		GraphicManager.getInstance().drawString("=>", this.menuItems[cursorPosition].getCoordinateX() - 30,
				this.menuItems[this.cursorPosition].getCoordinateY());
	}

	@Override
	public void close() {
		this.allReplayNames.clear();
		this.menuItems = null;
	}

}
