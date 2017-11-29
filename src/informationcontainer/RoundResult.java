package informationcontainer;

import struct.FrameData;

public class RoundResult {

	private int currentRound;

	private int[] remainingHPs;

	private int elapsedFrame;

	public RoundResult() {
		this.currentRound = -1;
		this.remainingHPs = new int[2];
		this.elapsedFrame = -1;
	}

	public RoundResult(int round, int[] hp, int frame) {
		this.currentRound = round;
		this.remainingHPs = hp;
		this.elapsedFrame = frame;
	}

	public RoundResult(FrameData frameData) {
		this.currentRound = frameData.getCurrentRound();
		this.remainingHPs = new int[] { Math.max(frameData.getMyCharacter(true).getHp(), 0),
				Math.max(frameData.getMyCharacter(false).getHp(), 0) };
		this.elapsedFrame = frameData.getCurrentFrameNumber();
	}

	public int getRound() {
		return this.currentRound;
	}

	public int[] getRemainingHPs() {
		return new int[] { this.remainingHPs[0], this.remainingHPs[1] };
	}

	public int getElapsedFrame() {
		return this.elapsedFrame;
	}
}
