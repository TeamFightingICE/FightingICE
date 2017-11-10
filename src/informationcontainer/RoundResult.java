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
		this.remainingHPs = new int[] { frameData.getMyCharacter(true).getHp(),
				frameData.getMyCharacter(false).getHp() };
		this.elapsedFrame = frameData.getCurrentFrameNumber();
	}

	public int getRound() {
		return this.currentRound;
	}

	public int[] getRemainingHPs() {
		return new int[] { this.remainingHPs[0], this.remainingHPs[1] };
	}

	public int gerElapsedFrame() {
		return this.elapsedFrame;
	}

}
