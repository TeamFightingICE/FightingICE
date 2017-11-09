package fighting;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import informationcontainer.RoundResult;
import input.KeyData;
import manager.InputManager;
import struct.FrameData;

public class Fighting {

	private Character[] playerCharacters;

	private Deque<LoopEffect> projectileDeque;

	private Deque<KeyData> inputCommands;

	private ArrayList<RoundResult> resultContainer;

	public Fighting() {
		this.playerCharacters = new Character[2];
		this.projectileDeque = new LinkedList<LoopEffect>();
		this.inputCommands = new LinkedList<KeyData>();
		this.resultContainer = new ArrayList<RoundResult>();

	}

	public void initialize() {

		///// 旧Fighting処理内容/////

		// BGMのロード
		// SEロード
		// 画像系ロード
		// スクリーン画像取得
		// 背景画像ロード

	}

	public void processingFight(int nowFrame) {
		KeyData keyData = new KeyData(InputManager.getInstance().getKeyData());

	}

	public Character[] getCharacters() {
		return this.playerCharacters.clone();
	}

	public FrameData createFrameData(int nowFrame){
		return new FrameData();
	}

	public void initRound() {

	}
}
