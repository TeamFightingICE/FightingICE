package aiinterface;

import struct.Key;

public class AIController extends Thread {

	private AIInterface ai;

	public AIController(AIInterface ai) {
		this.ai = ai;
	}

	public Key getInput() {
		return new Key();
	}
}
