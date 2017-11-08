package fighting;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import informationcontainer.RoundResult;
import input.KeyData;

public class Fighting {

	private Character[] playerCharacters;

	private Deque<LoopEffect> projectileDeque;

	private Deque<KeyData> inoutCommands;

	private ArrayList<RoundResult> resultContainer;

	public Fighting() {
		this.playerCharacters = new Character[2];
		this.projectileDeque = new LinkedList<LoopEffect>();
		this.inoutCommands = new LinkedList<KeyData>();
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

	public Character[] getCharacters() {
		return this.playerCharacters.clone();
	}

}
