package gamescene;

import java.util.LinkedList;

import fighting.Fighting;
import struct.FrameData;
import struct.GameData;

public class Play extends GameScene {

	private Fighting fighting;

	private LinkedList<FrameData> framesDara;

	private int nowFrame;

	private int elapsedBreakTime;

	public Play() {

	}

	@Override
	public void initialize() {
		this.fighting = new Fighting();
		this.fighting.initialize();

		GameData gameData = new GameData(fighting.getCharacters());
		// ((Input) im).initialize(deviceTypes, aiNames);
		// ((Input) im).startAI(gameData);

	}

	@Override
	public void update() {

	}

	@Override
	public void close() {

	}

}
