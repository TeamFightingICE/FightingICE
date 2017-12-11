package gamescene;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;

import enumerate.GameSceneName;
import informationcontainer.AIContainer;
import informationcontainer.RoundResult;
import input.Keyboard;
import manager.GraphicManager;
import manager.InputManager;
import python.PyManager;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import util.LogWriter;

public class Result extends GameScene {

	/** 各ラウンド終了時のP1, P2の残り体力, 経過時間を格納するリスト */
	private ArrayList<RoundResult> roundResults;

	/** 現在の年月日, 時刻を表す文字列 */
	private String timeInfo;

	/** リザルトの表示フレーム数 */
	private int displayedTime;

	/** リプレイシーンを初期化するコンストラクタ */
	public Result() {
		// 以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.FIGHTING_MENU;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////

		this.roundResults = new ArrayList<RoundResult>();
		this.timeInfo = "0";
		this.displayedTime = 0;
	}

	/** 各ラウンドの結果を格納したリスト及び現在の時間情報をセットし, リプレイシーンを初期化するコンストラクタ */
	public Result(ArrayList<RoundResult> roundResults, String timeInfo) {
		super();

		this.roundResults = new ArrayList<RoundResult>(roundResults);
		this.timeInfo = timeInfo;
		this.displayedTime = 0;
		roundResults.clear();
	}

	@Override
	public void initialize() {
		InputManager.getInstance().setSceneName(GameSceneName.RESULT);

		// pointファイルの書き出し
		LogWriter.getInstance().outputResult(this.roundResults, LogWriter.CSV, this.timeInfo);
	}

	@Override
	public void update() {
		if (FlagSetting.enableWindow) {
			int[] positionX = new int[] { GameSetting.STAGE_WIDTH / 2 - 70, GameSetting.STAGE_WIDTH / 2 + 10 };

			for (int i = 0; i < this.roundResults.size(); i++) {
				String[] score = new String[] { String.valueOf(this.roundResults.get(i).getRemainingHPs()[0]),
						String.valueOf(this.roundResults.get(i).getRemainingHPs()[1]) };

				// スコアの描画
				GraphicManager.getInstance().drawString(score[0], positionX[0], 50 + i * 100);
				GraphicManager.getInstance().drawString(score[1], positionX[1], 50 + i * 100);

				// 勝ちや引き分けに応じてWin !やDrawをスコアの横に印字
				switch (getWinPlayer(i)) {
				case 1:
					GraphicManager.getInstance().drawString("Win !", positionX[0] - 100, 50 + i * 100);
					break;
				case -1:
					GraphicManager.getInstance().drawString("Win !", positionX[1] + 80, 50 + i * 100);
					break;
				default:
					GraphicManager.getInstance().drawString("Draw", positionX[0] - 100, 50 + i * 100);
					GraphicManager.getInstance().drawString("Draw", positionX[1] + 80, 50 + i * 100);
					break;
				}
			}
		}

		endProcess();
	}

	@Override
	public void close() {
		this.roundResults.clear();
		this.displayedTime = 0;
	}

	/**
	 * P1, P2のどちらがそのラウンドで勝ったかを返す
	 *
	 * @return 0: 引き分け, 1: P1の勝ち, -1: P2の勝ち
	 */
	private int getWinPlayer(int i) {
		int[] remainingHPs = this.roundResults.get(i).getRemainingHPs();

		if (remainingHPs[0] == remainingHPs[1]) {
			return 0;
		} else if (remainingHPs[0] > remainingHPs[1]) {
			return 1;
		} else {
			return -1;
		}
	}

	/**
	 * 全AIの総当り対戦が終わったかどうかを返す.
	 *
	 * @return true: 全AIの総当り対戦が終わった; false: otherwise
	 */
	private boolean endRoundRobin() {
		return (AIContainer.p1Index + 1) == AIContainer.allAINameList.size()
				&& (AIContainer.p2Index + 1) == AIContainer.allAINameList.size();
	}

	private void endProcess() {
		// -aや-nを引数にして起動 or Repeat Countを2以上にして起動した場合の処理
		if (FlagSetting.automationFlag || FlagSetting.allCombinationFlag || FlagSetting.py4j) {
			if (++this.displayedTime > 300) {
				// まだ繰り返し回数が残っている場合
				if (FlagSetting.automationFlag && LaunchSetting.repeatedCount + 1 < LaunchSetting.repeatNumber) {
					LaunchSetting.repeatedCount++;

					Launcher launcher = new Launcher(GameSceneName.PLAY);
					this.setTransitionFlag(true);
					this.setNextGameScene(launcher);

					// まだ全AIの総当り対戦が終わっていない場合
				} else if (FlagSetting.allCombinationFlag) {
					if (++AIContainer.p1Index == AIContainer.allAINameList.size()) {
						AIContainer.p1Index = 0;
						AIContainer.p2Index++;
					}

					// 総当り対戦が終了したかどうか
					if (!endRoundRobin()) {
						Launcher launcher = new Launcher(GameSceneName.PLAY);
						this.setTransitionFlag(true);
						this.setNextGameScene(launcher);
					} else {
						this.setGameEndFlag(true);
					}

				} else if (FlagSetting.py4j) {
					synchronized (PyManager.python.getCurrentGame().end) {
						PyManager.python.getCurrentGame().end.notifyAll();
					}
					LaunchSetting.pyGatewayServer.close();
					Python python = new Python();
					this.setTransitionFlag(true);
					this.setNextGameScene(python);

					// 指定した繰り返し回数分対戦が終わった場合
				} else {
					this.setGameEndFlag(true);
				}
			}

			// 通常の対戦の場合, Enterキーが押されるまでResult画面を表示する
		} else {
			String string = "Press Enter key to return menu";
			GraphicManager.getInstance().drawString(string, GameSetting.STAGE_WIDTH / 2 - string.length() * 5 - 30,
					400);

			if (Keyboard.getKeyDown(GLFW_KEY_ENTER)) {
				HomeMenu homeMenu = new HomeMenu();
				this.setTransitionFlag(true); // 現在のシーンからの遷移要求をtrueに
				this.setNextGameScene(homeMenu); // 次のシーンをセットする
			}
		}
	}
}
