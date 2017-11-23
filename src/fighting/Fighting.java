package fighting;

import java.util.ArrayList;
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

public class Fighting {

	private Character[] playerCharacters;

	private Deque<LoopEffect> projectileDeque;

	private Deque<KeyData> inputCommands;

	private LinkedList<LinkedList<HitEffect>> hitEffects;

	private CommandTable commandTable;

	public Fighting() {
		this.playerCharacters = new Character[2];
		this.projectileDeque = new LinkedList<LoopEffect>();
		this.inputCommands = new LinkedList<KeyData>();
		this.commandTable = new CommandTable();
		this.hitEffects = new LinkedList<LinkedList<HitEffect>>();

	}

	public void initialize() {
		for (int i = 0; i < 2; i++) {
			this.playerCharacters[i] = new Character();
			this.playerCharacters[i].initialize(LaunchSetting.characterNames[i], i == 0);
			this.hitEffects.add(new LinkedList<HitEffect>());
		}

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
		// 1. コマンドの実行・対戦処理
		processingCommands(currentFrame, keyData);
		// 2. 当たり判定の処理
		calculationHit(currentFrame);
		// 3. 攻撃パラメータの更新
		updateAttackParameter();
		// 4. キャラクター情報の更新
		updateCharacter();

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

	/** 攻撃の当たり判定と,それに伴うキャラクターのパラメータ・コンボ状態の更新を行う */
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
			if (this.playerCharacters[i].getAttack() != null) {
				// 現在のコンボに応じたエフェクトをセット
				Image[] effect = GraphicManager.getInstance().getHitEffectImageContaier()[Math
						.max(this.playerCharacters[i].getComboState() - 1, 0)];
				this.hitEffects.get(i).add(new HitEffect(this.playerCharacters[i].getAttack(), effect, isHit[i]));

				// アッパーの処理
				if (playerCharacters[i].getAction() == Action.STAND_F_D_DFB) {
					Image[] upper = GraphicManager.getInstance().getUpperImageContainer()[i];
					this.hitEffects.get(i).add(new HitEffect(this.playerCharacters[i].getAttack(), upper, true, false));
				}
			}

			if (isHit[i]) {
				this.playerCharacters[i].setHitConfirm(true);
				this.playerCharacters[i].destroyAttackInstance();
			}
		}
	}

	/**
	 * 攻撃オブジェクトのパラメータ更新を行う.
	 */
	private void updateAttackParameter() {
		// update coordinate of Attacks(long distance)
		int dequeSize = this.projectileDeque.size();
		for (int i = 0; i < dequeSize; i++) {

			// if attack's nowFrame reach end of duration, remove it.
			LoopEffect projectile = this.projectileDeque.removeFirst();
			if (projectile.getAttack().updateProjectileAttack()) {
				projectile.update();
				this.projectileDeque.addLast(projectile);
			}
		}

		// update coordinate of Attacks(short distance)
		for (int i = 0; i < 2; ++i) {
			if (this.playerCharacters[i].getAttack() != null) {
				if (!this.playerCharacters[i].getAttack().update(this.playerCharacters[i])) {
					this.playerCharacters[i].destroyAttackInstance();
				}
			}
		}
	}

	/**
	 * キャラクターのパラメータや波動拳の情報を更新する
	 */
	private void updateCharacter() {
		for (int i = 0; i < 2; ++i) {
			// update each character.
			this.playerCharacters[i].update();


			// enque object attack if the data is missile decision
			if (this.playerCharacters[i].getAttack() != null) {
				if (this.playerCharacters[i].getAttack().isProjectile()) {

					Attack attack = this.playerCharacters[i].getAttack();
					ArrayList<Image> projectileImage = GraphicManager.getInstance().getProjectileImageContainer();
					if (this.playerCharacters[i].getAction() == Action.STAND_D_DF_FC) {
						projectileImage = GraphicManager.getInstance().getUltimateAttackImageContainer();
					}

					Image[] temp = new Image[projectileImage.size()];
					for (int j = 0; i < temp.length; j++) {
						temp[j] = projectileImage.get(j);
					}
					this.projectileDeque.addLast(new LoopEffect(attack, temp));
					this.playerCharacters[i].destroyAttackInstance();
				}
			}

			// change player's direction
			if (playerCharacters[i].isControl()) {
				playerCharacters[i].frontDecision(playerCharacters[i == 0 ? 1 : 0].getHitAreaCenterX());
			}

			//エフェクトの更新
			for(int j = 0; j < this.hitEffects.get(i).size(); j++){
				if (!this.hitEffects.get(i).get(j).update()) {
					this.hitEffects.get(i).remove(j);
					--j;
				}
			}
		}
		// run pushing effect
		detectionPush();
		// run collision of first and second character.
		detectionFusion();
		// run effect when character's are in the end of stage.
		decisionEndStage();
	}

	/**
	 * 各キャラクターの現在の水平方向のスピード量に応じて, プッシュ処理を行う
	 */
	private void detectionPush() {
		// whether the conflict of first and second player or not?
		if (isCollision()) {
			int direction = this.playerCharacters[0].isFront() ? 1 : -1;
			int p1SpeedX = direction * this.playerCharacters[0].getSpeedX();
			int p2SpeedX = -direction * this.playerCharacters[1].getSpeedX();

			if (p1SpeedX > p2SpeedX) {
				this.playerCharacters[1]
						.moveX(this.playerCharacters[0].getSpeedX() - this.playerCharacters[1].getSpeedX());

			} else if (p1SpeedX < -p2SpeedX) {
				this.playerCharacters[0]
						.moveX(this.playerCharacters[1].getSpeedX() - this.playerCharacters[0].getSpeedX());

			} else {
				this.playerCharacters[0].moveX(this.playerCharacters[1].getSpeedX());
				this.playerCharacters[1].moveX(this.playerCharacters[0].getSpeedX());
			}
		}
	}

	/**
	 * 相手と位置が重なってしまった場合, 重ならないように各キャラクターの座標の更新処理を行う
	 */
	private void detectionFusion() {
		// whether the conflict of first and second player or not?
		if (isCollision()) {
			int direction = 0;

			// if first player is left
			if (this.playerCharacters[0].getHitAreaCenterX() < this.playerCharacters[1].getHitAreaCenterX()) {
				direction = 1;
				// if second player is left
			} else if (this.playerCharacters[0].getHitAreaCenterX() > this.playerCharacters[1].getHitAreaCenterX()) {
				direction = -1;
			} else {
				if (this.playerCharacters[0].isFront()) {
					direction = 1;
				} else {
					direction = -1;
				}
			}
			this.playerCharacters[0].moveX(-direction * 2);
			this.playerCharacters[1].moveX(direction * 2);
		}
	}

	/** 相手キャラクターとぶつかっている状態かを判定する */
	private boolean isCollision() {
		return this.playerCharacters[0].getHitAreaLeft() <= this.playerCharacters[1].getHitAreaRight()
				&& this.playerCharacters[0].getHitAreaTop() <= this.playerCharacters[1].getHitAreaBottom()
				&& playerCharacters[0].getHitAreaRight() >= this.playerCharacters[1].getHitAreaLeft()
				&& this.playerCharacters[0].getHitAreaBottom() >= this.playerCharacters[1].getHitAreaTop();
	}

	/**
	 * ステージ端からキャラクターがはみ出ないように, 各キャラクターの座標の更新処理を行う
	 */
	private void decisionEndStage() {

		for (int i = 0; i < 2; ++i) {
			// if action is down, character will be rebound.
			// first player's effect
			if (playerCharacters[i].getHitAreaRight() > GameSetting.STAGE_WIDTH) {
				if (playerCharacters[i].getAction() == Action.DOWN) {
					playerCharacters[i].reversalSpeedX();
				}

				playerCharacters[i].moveX(-playerCharacters[i].getHitAreaRight() + GameSetting.STAGE_WIDTH);

			} else if (playerCharacters[i].getHitAreaLeft() < 0) {
				if (playerCharacters[i].getAction() == Action.DOWN) {
					playerCharacters[i].reversalSpeedX();
				}

				playerCharacters[i].moveX(-playerCharacters[i].getHitAreaLeft());
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
	 * 攻撃が相手に当たったかどうかを判定する
	 *
	 * @param opponent
	 *            相手キャラクター.
	 * @param attack
	 *            自身が出した攻撃.
	 * @return <em>True</em> 攻撃が当たった <em>False</em> 攻撃が当たらなかった
	 *
	 * @see Character
	 * @see Attack
	 */
	private boolean detectionHit(Character opponent, Attack attack) {
		if (attack == null || opponent.getState() == State.DOWN) {
			return false;
		} else if (opponent.getHitAreaLeft() <= attack.getCurrentHitArea().getRight()
				&& opponent.getHitAreaRight() >= attack.getCurrentHitArea().getLeft()
				&& opponent.getHitAreaBottom() <= attack.getCurrentHitArea().getBottom()
				&& opponent.getHitAreaTop() >= attack.getCurrentHitArea().getTop()) {
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

	/** ラウンド開始時にキャラクター情報を初期化し, リストの中身をクリアーする */
	public void initRound() {
		for (int i = 0; i < 2; i++) {
			this.playerCharacters[i].roundInit();
			this.hitEffects.get(i).clear();
		}

		this.projectileDeque.clear();
		this.inputCommands.clear();

	}

	public LinkedList<LinkedList<HitEffect>> getHitEffectList() {
	//	LinkedList<LinkedList<HitEffect>> temp = new LinkedList<LinkedList<HitEffect>>();
		LinkedList<LinkedList<HitEffect>> temp = new LinkedList<LinkedList<HitEffect>>(this.hitEffects);

		/*for(int i = 0; i < 2; i++){
			LinkedList<HitEffect> effects = new LinkedList<HitEffect>();

			for(HitEffect effect : temp.get(i)){
				effect = new HitEffect(effect.getAttack(), effect.getImages(), effect.isHit());
				effects.add(effect);
			}

			temp.add(effects);
		}*/

		return temp;
	}

	public Deque<LoopEffect> getProjectileDeque() {
		Deque<LoopEffect> temp = new LinkedList<LoopEffect>(this.projectileDeque);
		/*Deque<LoopEffect> temp = new LinkedList<LoopEffect>();

		for(LoopEffect loopEffect : this.projectileDeque){
			temp.addLast(new LoopEffect(loopEffect.getAttack(), loopEffect.getImages()));
		}*/

		return temp;
	}
}
