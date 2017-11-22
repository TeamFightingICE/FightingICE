package fighting;

import java.awt.image.BufferedImage;
import java.util.Deque;
import java.util.LinkedList;

import command.CommandTable;
import enumerate.Action;
import enumerate.State;
import image.Image;
import input.KeyData;
import manager.GraphicManager;
import setting.GameSetting;
import setting.LaunchSetting;
import struct.CharacterData;
import struct.FrameData;
import struct.ScreenData;

public class Fighting {

	private Character[] playerCharacters;

	private Deque<LoopEffect> projectileDeque;

	private Deque<KeyData> inputCommands;

	private BufferedImage screen;

	private LinkedList<LinkedList<HitEffect>> hitEffects;

	private CommandTable commandTable;

	public Fighting() {
		this.playerCharacters = new Character[2];
		this.projectileDeque = new LinkedList<LoopEffect>();
		this.inputCommands = new LinkedList<KeyData>();
		this.screen = null;
		this.commandTable = new CommandTable();

	}

	public void initialize() {

		for (int i = 0; i < 2; i++) {
			this.playerCharacters[i] = new Character();
			this.playerCharacters[i].initialize(LaunchSetting.characterNames[i], i == 0);
			this.hitEffects.add(new LinkedList<HitEffect>());
		}

		this.screen = new BufferedImage(GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
		this.projectileDeque = new LinkedList<LoopEffect>();
		this.inputCommands = new LinkedList<KeyData>();

		///// 旧Fighting処理内容/////

		// BGMのロード
		// SEロード
		// 画像系ロード←Launcherでやってる
		// スクリーン画像取得←ここでやる
		// 背景画像ロード←Launcherでやってる
		// スコア・経過時間の結果を格納する配列初期化←Playでやってる
		// 波動拳格納リスト初期化←ここ
		// コマンド格納リスト初期化←ここ
		// リプレイ用ファイルオープン←ここかPlay
		// Json用ファイルオープン←未定

	}

	public void processingFight(int currentFrame, KeyData keyData) {
		// 1. キャラクターの状態の更新←ここ5でやったほうがよくない？
		// 2. コマンドの実行・対戦処理
		processingCommands(currentFrame, keyData);
		// 3. 当たり判定の処理
		calculationHit(currentFrame);
		// 4. 攻撃パラメータの更新
		// 5. キャラクター情報の更新

	}

	/** 入力されたキーを基にアクションを実行する */
	private void processingCommands(int currentFrame, KeyData keyData) {
		this.inputCommands.addLast(keyData);

		if (this.inputCommands.size() > GameSetting.INPUT_LIMIT) {
			this.inputCommands.removeFirst();
		}

		for (int i = 0; i < 2; i++) {
			if (!this.inputCommands.isEmpty()) {
				Action executeAction = this.commandTable.convertKeyToAction(this.playerCharacters[i],
						this.inputCommands);

				if (ableAction(this.playerCharacters[i], executeAction)) {
					this.playerCharacters[i].runAction(executeAction, true);
				}
			}
		}
	}

	private void calculationHit(int currentFrame) {
		boolean[] isHit = { false, false };

		// 波動拳の処理
		int dequeSize = this.projectileDeque.size();
		for (int i = 0; i < dequeSize; i++) {
			LoopEffect projectile = this.projectileDeque.removeFirst();
			int opponentIndex = projectile.getAttack().isPlayerNumber() ? 1 : 0;

			if (detectionHit(this.playerCharacters[opponentIndex], projectile.getAttack())) {
				int myIndex = opponentIndex == 0 ? 1 : 0;
				this.playerCharacters[opponentIndex].hitAttack(this.playerCharacters[myIndex], projectile.getAttack());

			} else {
				this.projectileDeque.addLast(projectile);
			}
		}

		// 通常攻撃の処理
		for (int i = 0; i < 2; i++) {
			int opponentIndex = i == 0 ? 1 : 0;
			Attack attack = this.playerCharacters[i].getAttack();

			if (detectionHit(this.playerCharacters[opponentIndex], attack)) {
				isHit[i] = true;
				// コンボの処理
				processingCombo(currentFrame, i);
				// HP等のパラメータの更新
				this.playerCharacters[i].hitAttack(this.playerCharacters[opponentIndex], attack);

			} else if (this.playerCharacters[i].getAttack() != null) {
				this.playerCharacters[i].resetCombo();
			}
		}

		// エフェクト関係の処理
		for (int i = 0; i < 2; i++) {
			if (playerCharacters[i].getAttack() != null) {
				// 現在のコンボに応じたエフェクトをセット
				Image[] effect = GraphicManager.getInstance().getHitEffectImageContaier()[Math
						.max(playerCharacters[i].getComboState() - 1, 0)];
				this.hitEffects.get(i).add(new HitEffect(playerCharacters[i].getAttack(), effect, isHit[i]));

				// アッパーの処理
				if (playerCharacters[i].getAction() == Action.STAND_F_D_DFB) {
					Image[] upper = GraphicManager.getInstance().getUpperImageContainer()[i];
					this.hitEffects.get(i).add(new HitEffect(playerCharacters[i].getAttack(), upper, true, false));
				}
			}

			if (isHit[i]) {
				this.playerCharacters[i].setHitConfirm(true);
				this.playerCharacters[i].destroyAttackInstance();
			}
		}
	}

	/** 自身の攻撃が相手に当たった時, コンボの遷移処理及び相手のコンボのブレイク処理を行う */
	private void processingCombo(int currentFrame, int myIndex) {
		int opponentIndex = myIndex == 0 ? 1 : 0;
		Action action = this.playerCharacters[myIndex].getAction();

		// 次のコンボに遷移
		this.playerCharacters[myIndex].nextCombo(currentFrame);
		// 自身のコンボブレイカーによって相手のコンボがブレイクできたか
		if (this.playerCharacters[opponentIndex].isComboBreakable()
				&& this.playerCharacters[opponentIndex].getComboBreakers().contains(action)) {
			this.playerCharacters[opponentIndex].breakCombo();
			this.playerCharacters[opponentIndex].resetCombo();
		}
	}

	/** 入力されたアクションが実行可能かどうかを返す */
	private boolean ableAction(Character character, Action nextAction) {
		Motion nextMotion = character.getMotionList().get(nextAction.ordinal());
		Motion nowMotion = character.getMotionList().get(character.getAction().ordinal());

		if (character.getEnergy() < Math.abs(nextMotion.getAttackStartAddEnergy())) {
			return false;
		} else if (character.isControl()) {
			return true;
		} else {
			boolean checkFrame = nowMotion.getCancelAbleFrame() <= nowMotion.getFrameNumber()
					- character.getRemainingFrame();
			boolean checkAction = nowMotion.getCancelAbleMotionLevel() >= nextMotion.getMotionLevel();

			return character.isHitConfirm() && checkFrame && checkAction;
		}
	}

	/**
	 * Calculate collision.
	 *
	 * @param character
	 *            Character you want to check.
	 * @param attack
	 *            Attack you want to check.
	 * @return <em>True</em> if the character is hit. <em>False</em> otherwise.
	 *
	 * @see Character
	 * @see Attack
	 */
	private boolean detectionHit(Character character, Attack attack) {
		if (attack == null || character.getState() == State.DOWN) {
			return false;
		} else if (character.getCharacterHitAreaLeft() <= attack.getCurrentHitArea().getRight()
				&& character.getCharacterHitAreaRight() >= attack.getCurrentHitArea().getLeft()
				&& character.getCharacterHitAreaBottom() <= attack.getCurrentHitArea().getBottom()
				&& character.getCharacterHitAreaTop() >= attack.getCurrentHitArea().getTop()) {
			return true;
		} else {
			return false;
		}
	}

	/** P1, P2のキャラクター情報が格納された配列を返す */
	public Character[] getCharacters() {
		return this.playerCharacters.clone();
	}

	/**
	 * 現在のフレームにおけるゲーム情報を格納したフレームデータを作成する<br>
	 * 両キャラクターの情報, 現在のフレーム数, 現在のラウンド, 波動拳の情報を格納したリスト, 両キャラクターのキー情報, 画面のピクセル情報,
	 * 画面のBufferedImage
	 */
	public FrameData createFrameData(int nowFrame, int round, KeyData keyData) {
		CharacterData[] characterData = new CharacterData[] { new CharacterData(playerCharacters[0]),
				new CharacterData(playerCharacters[1]) };

		Deque<Attack> newAttackDeque = new LinkedList<Attack>();
		for (LoopEffect loopEffect : this.projectileDeque) {
			newAttackDeque.addLast(loopEffect.getAttack());
		}

		return new FrameData(characterData, nowFrame, round, newAttackDeque, keyData);
	}

	public ScreenData getScreenData() {
		return new ScreenData(this.screen);
	}

	public void initRound() {
		this.projectileDeque.clear();
		this.inputCommands.clear();

	}
}
