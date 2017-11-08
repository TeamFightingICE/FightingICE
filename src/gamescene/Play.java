package gamescene;

import java.util.LinkedList;

import fighting.Fighting;
import struct.FrameData;

public class Play extends GameScene {

	private Fighting fighting;

	private LinkedList<FrameData> framesDara;

	private int nowFrame;

	private int elapsedBreakTime;



	@Override
	public void initialize() {
		this.fighting = new Fighting();


	}

	@Override
	public void update() {


	}

	@Override
	public void close() {


	}

}
