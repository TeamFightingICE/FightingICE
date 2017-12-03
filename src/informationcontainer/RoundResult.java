package informationcontainer;

import struct.FrameData;

/**
 * ラウンドの結果を処理するクラス
 */
public class RoundResult {

	private int currentRound;

	private int[] remainingHPs;

	private int elapsedFrame;

	/**
	 * RoundResultの初期化を行うコンストラクタ
	 */
	public RoundResult() {
		this.currentRound = -1;
		this.remainingHPs = new int[2];
		this.elapsedFrame = -1;
	}

	/**
	 * 指定された値でRoundResultを更新するコンストラクタ
	 *
	 * @param round
	 *            ラウンド数
	 * @param hp
	 *            P1,P2の残りHP
	 * @param frame
	 *            経過フレーム数
	 */
	public RoundResult(int round, int[] hp, int frame) {
		this.currentRound = round;
		this.remainingHPs = hp;
		this.elapsedFrame = frame;
	}

	/**
	 * フレーム内のゲームデータからRoundResultを更新するコンストラクタ
	 *
	 * @param frameData
	 *            フレーム内のゲームデータ
	 */
	public RoundResult(FrameData frameData) {
		this.currentRound = frameData.getRound();
		this.remainingHPs = new int[] { Math.max(frameData.getCharacter(true).getHp(), 0),
				Math.max(frameData.getCharacter(false).getHp(), 0) };
		this.elapsedFrame = frameData.getFramesNumber();
	}

	/**
	 * 現在のラウンド数を返すメソッド
	 *
	 * @return 現在のラウンド数
	 */
	public int getRound() {
		return this.currentRound;
	}

	/**
	 * P1,P2の残りHPを返すメソッド
	 *
	 * @return P1,P2の残りHP
	 */
	public int[] getRemainingHPs() {
		return new int[] { this.remainingHPs[0], this.remainingHPs[1] };
	}

	/**
	 * 経過フレーム数を返すメソッド
	 *
	 * @return 経過フレーム数
	 */
	public int getElapsedFrame() {
		return this.elapsedFrame;
	}
}
