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
import struct.AttackData;
import struct.CharacterData;
import struct.FrameData;
import struct.Key;

/**
 * 対戦処理及びそれに伴う攻撃やキャラクターのパラメータの更新処理を扱うクラス．
 */
public class Fighting {

	/**
	 * The character's data of both characters<br>
	 * Index 0 is P1, index 1 is P2.
	 *
	 * @see Character
	 */
	protected Character[] playerCharacters;

	/**
	 * The list of projectile data of both characters.
	 *
	 * @see LoopEffect
	 */
	protected Deque<LoopEffect> projectileDeque;

	/**
	 * The list of the input information of both characters.
	 *
	 * @see KeyData
	 */
	private Deque<KeyData> inputCommands;

	/**
	 * 攻撃が当たった時に表示するエフェクトや, アッパーのエフェクトの情報を格納するリスト．<br>
	 * Index 0 is P1, index 1 is P2.
	 *
	 * @see HitEffect
	 */
	private LinkedList<LinkedList<HitEffect>> hitEffects;

	/**
	 * キー入力とそれに対応するアクションを管理するクラス変数．
	 *
	 * @see CommandTable
	 */
	protected CommandTable commandTable;


	/**
	 * Class constructor．
	 */
	public Fighting() {
		this.playerCharacters = new Character[2];
		this.projectileDeque = new LinkedList<LoopEffect>();
		this.inputCommands = new LinkedList<KeyData>();
		this.commandTable = new CommandTable();
		this.hitEffects = new LinkedList<LinkedList<HitEffect>>();
	}
	
	public void processingRoundEnd(){
		this.inputCommands.clear();
		this.playerCharacters[0].setProcessedCommand(new LinkedList<Key>());
		this.playerCharacters[1].setProcessedCommand(new LinkedList<Key>());
		this.playerCharacters[0].setInputCommand(new LinkedList<Key>());
		this.playerCharacters[1].setInputCommand(new LinkedList<Key>());
		this.playerCharacters[0].resetEnergyCount();
		this.playerCharacters[1].resetEnergyCount();
	}
	
	/**
	 * P1, P2のキャラクター情報とエフェクトを格納するリストの初期化を行う．
	 */
	public void initialize() {
		for (int i = 0; i < 2; i++) {
			this.playerCharacters[i] = new Character();
			this.playerCharacters[i].initialize(LaunchSetting.characterNames[i], i == 0);
			this.hitEffects.add(new LinkedList<HitEffect>());
		}
	}

	/**
	 * P1, P2のキー入力を基に, 1フレーム分の対戦処理を行う. <br>
	 * 処理順序は以下の通りである．<br>
	 * <ol>
	 * <li>キー入力を基に, アクションを実行</li>
	 * <li>攻撃の当たり判定の処理, 及びそれに伴うキャラクターのHPなどのパラメータの更新</li>
	 * <li>攻撃のパラメータの更新</li>
	 * <li>キャラクターの状態の更新</li>
	 * </ol>
	 *
	 * @param currentFrame
	 *            現在のフレーム
	 * @param keyData
	 *            P1, P2のキー入力． Index 0 is P1, index 1 is P2.
	 */
	public void processingFight(int currentFrame, KeyData keyData) {
		// 1. 入力されたキーを基に, アクションを実行
		processingCommands(keyData);
		// 2. 当たり判定の処理
		calculationHit(currentFrame);
		// 3. 攻撃のパラメータの更新
		updateAttackParameter();
		// 4. キャラクターの状態の更新
		updateCharacter();

	}

	/**
	 * キー入力を基にアクションを実行する．
	 *
	 * @param currentFrame
	 *            現在のフレーム
	 * @param keyData
	 *            P1, P2のキー入力．<br>
	 *            Index 0 is P1, index 1 is P2.
	 */
	protected void processingCommands(KeyData keyData) {
		this.inputCommands.addLast(keyData);

		// リストのサイズが上限(INPUT_LIMIT)を超えていたら, 最も古いデータを削除する
		while (this.inputCommands.size() > GameSetting.INPUT_LIMIT) {
			this.inputCommands.removeFirst();
		}

		// アクションの実行
		for (int i = 0; i < 2; i++) {
			if (!this.inputCommands.isEmpty()) {
				Action executeAction = this.commandTable.interpretationCommandFromKeyData(this.playerCharacters[i],
						this.inputCommands);
				if (ableAction(this.playerCharacters[i], executeAction)) {
					this.playerCharacters[i].runAction(executeAction, true);
				}
			}
		}
	}

	/**
	 * 攻撃の当たり判定の処理, 及びそれに伴うキャラクターの体力などのパラメータの更新を行う．
	 *
	 * @param currentFrame
	 *            現在のフレーム
	 */
	protected void calculationHit(int currentFrame) {
		boolean[] isHit = { false, false };

		// 波動拳の処理
		int dequeSize = this.projectileDeque.size();
		for (int i = 0; i < dequeSize; i++) {
			LoopEffect projectile = this.projectileDeque.removeFirst();
			int opponentIndex = projectile.getAttack().isPlayerNumber() ? 1 : 0;

			if (detectionHit(this.playerCharacters[opponentIndex], projectile.getAttack())) {
				int myIndex = opponentIndex == 0 ? 1 : 0;
				this.playerCharacters[opponentIndex].hitAttack(this.playerCharacters[myIndex], projectile.getAttack(),
						currentFrame);
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
				// HP等のパラメータの更新
				this.playerCharacters[opponentIndex].hitAttack(this.playerCharacters[i], attack, currentFrame);
			}
		}

		// エフェクト関係の処理. Windowが生成されているときのみ行う.
		for (int i = 0; i < 2; i++) {
			if (this.playerCharacters[i].getAttack() != null) {
				// 現在のコンボに応じたエフェクトをセット
				int comboState = Math.max(this.playerCharacters[i].getHitCount() - 1, 0);
				// 4Hit以上であれば,エフェクトは4ヒット目のもの固定
				comboState = Math.min(comboState, 3);

				Image[] effect = GraphicManager.getInstance().getHitEffectImageContaier()[comboState];
				this.hitEffects.get(i).add(new HitEffect(this.playerCharacters[i].getAttack(), effect, isHit[i]));

				// アッパーの処理
				if (playerCharacters[i].getAction() == Action.STAND_F_D_DFB) {
					Image[] upper = GraphicManager.getInstance().getUpperImageContainer()[i];
					Motion motion = this.playerCharacters[i].getMotionList().get(Action.STAND_F_D_DFB.ordinal());

					if (this.playerCharacters[i].startActive(motion)) {
						this.hitEffects.get(i).add(new HitEffect(this.playerCharacters[i].getAttack(), upper, true, false));
					}
				}
			}

			if (isHit[i]) {
				this.playerCharacters[i].setHitConfirm(true);
				this.playerCharacters[i].destroyAttackInstance();
			}

			if (!playerCharacters[i].isComboValid(currentFrame)) {
				playerCharacters[i].setHitCount(0);
			}
		}
	}

	/**
	 * 攻撃のパラメータの更新を行う.
	 */
	protected void updateAttackParameter() {
		// Updates the parameters of all of projectiles appearing in the stage
		int dequeSize = this.projectileDeque.size();
		for (int i = 0; i < dequeSize; i++) {

			LoopEffect projectile = this.projectileDeque.removeFirst();
			if (projectile.getAttack().updateProjectileAttack()) {
				projectile.update();
				this.projectileDeque.addLast(projectile);
			}
		}

		// Updates the parameters of all of attacks excepted projectile
		// conducted by both characters
		for (int i = 0; i < 2; ++i) {
			if (this.playerCharacters[i].getAttack() != null) {
				if (!this.playerCharacters[i].getAttack().update(this.playerCharacters[i])) {
					this.playerCharacters[i].destroyAttackInstance();
				}
			}
		}
	}

	/**
	 * キャラクターの状態や, エフェクトの更新を行う.
	 */
	protected void updateCharacter() {
		for (int i = 0; i < 2; ++i) {
			// Updates each character.
			this.playerCharacters[i].update();

			if (this.playerCharacters[i].getAttack() != null) {
				if (this.playerCharacters[i].getAttack().isProjectile()) {
					Attack attack = this.playerCharacters[i].getAttack();
					ArrayList<Image> projectileImage = GraphicManager.getInstance().getProjectileImageContainer();

					if (this.playerCharacters[i].getAction() == Action.STAND_D_DF_FC) {
						projectileImage = GraphicManager.getInstance().getUltimateAttackImageContainer();
					}

					Image[] temp = new Image[projectileImage.size()];
					for (int j = 0; j < temp.length; j++) {
						temp[j] = projectileImage.get(j);
					}
					
					this.projectileDeque.addLast(new LoopEffect(attack, temp));
					this.playerCharacters[i].destroyAttackInstance();
				}
			}

			// Changes player's direction
			if (playerCharacters[i].isControl()) {
				playerCharacters[i].frontDecision(playerCharacters[i == 0 ? 1 : 0].getHitAreaCenterX());
			}

			// Updates the all of effects appearing in this stage
			for (int j = 0; j < this.hitEffects.get(i).size(); j++) {
				if (!this.hitEffects.get(i).get(j).update()) {
					this.hitEffects.get(i).remove(j);
					--j;
				}
			}
		}
		// Runs pushing.
		detectionPush();
		// Runs collision of first and second character.
		detectionFusion();
		// Runs effect when character's are in the end of stage.
		decisionEndStage();
	}

	/**
	 * P1とP2のキャラクターの水平方向のスピードに応じて, 相手を押す処理を行う．
	 */
	protected void detectionPush() {
		if (isCollision()) {
			int p1SpeedX = Math.abs(this.playerCharacters[0].getSpeedX());
			int p2SpeedX = Math.abs(this.playerCharacters[1].getSpeedX());

			if (p1SpeedX > p2SpeedX) {
				this.playerCharacters[1]
						.moveX(this.playerCharacters[0].getSpeedX() - this.playerCharacters[1].getSpeedX());
			} else if (p1SpeedX < p2SpeedX) {
				this.playerCharacters[0]
						.moveX(this.playerCharacters[1].getSpeedX() - this.playerCharacters[0].getSpeedX());
			} else {
				this.playerCharacters[0].moveX(this.playerCharacters[1].getSpeedX());
				this.playerCharacters[1].moveX(this.playerCharacters[0].getSpeedX());
			}
		}
	}

	/**
	 * P1とP2のキャラクター位置が重なってしまった場合, 重ならないように各キャラクターの座標の更新処理を行う．
	 */
	protected void detectionFusion() {
		if (isCollision()) {
			int direction = 0;

			// If first player is left
			if (this.playerCharacters[0].getHitAreaCenterX() < this.playerCharacters[1].getHitAreaCenterX()) {
				direction = 1;
				// If second player is left
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

	/**
	 * P1とP2のキャラクターが衝突している状態かどうかを判定する．
	 *
	 * @return {@code true} 両者が衝突している， {@code false} otherwise
	 */
	private boolean isCollision() {
		return this.playerCharacters[0].getHitAreaLeft() <= this.playerCharacters[1].getHitAreaRight()
				&& this.playerCharacters[0].getHitAreaTop() <= this.playerCharacters[1].getHitAreaBottom()
				&& this.playerCharacters[0].getHitAreaRight() >= this.playerCharacters[1].getHitAreaLeft()
				&& this.playerCharacters[0].getHitAreaBottom() >= this.playerCharacters[1].getHitAreaTop();
	}

	/**
	 * ステージの端からキャラクターがはみ出ないように, 各キャラクターの座標の更新処理を行う．
	 */
	protected void decisionEndStage() {
		for (int i = 0; i < 2; ++i) {
			// If action is down, character will be rebound.
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

	/**
	 * 次に実行予定のアクションが実行可能かどうかを返す．
	 *
	 * @param character
	 *            アクションを実行するキャラクターのインスタンス
	 * @param nextAction
	 *            次に実行予定のアクション
	 *
	 * @return {@code true} 実行可能である，{@code false} otherwise
	 *
	 * @see Character
	 * @see Action
	 */
	protected boolean ableAction(Character character, Action nextAction) {
		Motion nextMotion = character.getMotionList().get(nextAction.ordinal());
		Motion nowMotion = character.getMotionList().get(character.getAction().ordinal());

		if (character.getEnergy() < -nextMotion.getAttackStartAddEnergy()) {
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
	 * 攻撃が相手に当たったかどうかを判定する．
	 *
	 * @param opponent
	 *            相手キャラクター.
	 * @param attack
	 *            自身が出した攻撃.
	 * @return {@code true} 攻撃が当たった場合，{@code false} otherwise
	 *
	 * @see Character
	 * @see Attack
	 */
	protected boolean detectionHit(Character opponent, Attack attack) {
		if (attack == null || opponent.getState() == State.DOWN) {
			return false;
		} else if (opponent.getHitAreaLeft() <= attack.getCurrentHitArea().getRight()
				&& opponent.getHitAreaRight() >= attack.getCurrentHitArea().getLeft()
				&& opponent.getHitAreaTop() <= attack.getCurrentHitArea().getBottom()
				&& opponent.getHitAreaBottom() >= attack.getCurrentHitArea().getTop()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * P1, P2のキャラクター情報が格納された配列を返す．
	 *
	 * @return P1, P2のキャラクター情報が格納された配列
	 */
	public Character[] getCharacters() {
		return this.playerCharacters.clone();
	}

	/**
	 * 現在のフレームにおけるゲーム情報を格納したフレームデータを作成する．<br>
	 * 両キャラクターの情報, 現在のフレーム数, 現在のラウンド, 波動拳の情報を格納したリスト, 両キャラクターのキー情報を持つ．
	 *
	 * @param nowFrame
	 *            現在のフレーム
	 * @param round
	 *            現在のラウンド
	 *
	 * @return 現在のフレームにおけるゲーム情報を格納したフレームデータ
	 *
	 * @see KeyData
	 * @see FrameData
	 */
	public FrameData createFrameData(int nowFrame, int round) {
		CharacterData[] characterData = new CharacterData[] { new CharacterData(playerCharacters[0]),
				new CharacterData(playerCharacters[1]) };
		
		Deque<AttackData> newAttackDeque = new LinkedList<AttackData>();
		for (LoopEffect loopEffect : this.projectileDeque) {
			newAttackDeque.addLast(new AttackData(loopEffect.getAttack()));
		}

		return new FrameData(characterData, nowFrame, round, newAttackDeque);
	}

	/**
	 * ラウンド開始時にキャラクター情報を初期化し,リストやキューの中身を空にする．
	 */
	public void initRound() {
		for (int i = 0; i < 2; i++) {
			this.playerCharacters[i].roundInit();
			this.hitEffects.get(i).clear();
		}

		this.projectileDeque.clear();
		this.inputCommands.clear();
	}

	/**
	 * P1, P2のエフェクトのリストを返す．
	 *
	 * @return P1, P2のエフェクトのリスト
	 */
	public LinkedList<LinkedList<HitEffect>> getHitEffectList() {
		return new LinkedList<LinkedList<HitEffect>>(this.hitEffects);
	}

	/**
	 * Returns the list of projectile data of both characters.
	 *
	 * @return the list of projectile data of both characters
	 */
	public Deque<LoopEffect> getProjectileDeque() {
		return new LinkedList<LoopEffect>(this.projectileDeque);
	}

	public void close(){
		for (Character character: this.getCharacters())
			character.close();
	}
}
