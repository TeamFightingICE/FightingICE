package simulator;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import aiinterface.CommandCenter;
import command.CommandTable;
import enumerate.Action;
import fighting.Attack;
import fighting.Character;
import fighting.Fighting;
import fighting.LoopEffect;
import fighting.Motion;
import input.KeyData;
import setting.GameSetting;
import struct.AttackData;
import struct.CharacterData;
import struct.FrameData;
import struct.Key;

public class SimFighting extends Fighting {

	private ArrayList<Deque<Key>> inputKeys;

	private ArrayList<Deque<Action>> inputActions;

	private CommandCenter[] commandCenter;

	public SimFighting() {
		this.playerCharacters = new Character[2];
		this.projectileDeque = new LinkedList<LoopEffect>();
		this.commandTable = new CommandTable();

		this.inputKeys = new ArrayList<Deque<Key>>(2);
		this.inputActions = new ArrayList<Deque<Action>>(2);
		this.commandCenter = new CommandCenter[2];
	}

	public void initialize(ArrayList<ArrayList<Motion>> motionList, ArrayList<Deque<Action>> actionList,
			FrameData frameData, boolean playerNumber) {

		for (int i = 0; i < 2; i++) {
			this.playerCharacters[i] = new Character(frameData.getCharacter(i == 0), motionList.get(i));

			this.inputKeys.add(this.playerCharacters[i].getProcessedCommand());
			this.inputActions.add(actionList.get(i));

			this.commandCenter[i] = new CommandCenter();
			this.commandCenter[i].setFrameData(frameData, i == 0);
		}

		Deque<AttackData> projectiles = frameData.getProjectiles();
		for (AttackData temp : projectiles) {
			this.projectileDeque.addLast(new LoopEffect(new Attack(temp), null));
		}
	}

	public void processingFight(int currentFrame) {
		// 1. コマンドの実行・対戦処理
		processingCommands();
		// 2. 当たり判定の処理
		calculationHit(currentFrame);
		// 3. 攻撃パラメータの更新
		updateAttackParameter();
		// 4. キャラクター情報の更新
		updateCharacter();
	}

	public void processingCommands() {

		for (int i = 0; i < 2; i++) {
			Deque<Key> keyList = this.inputKeys.get(i);
			Deque<Action> actList = this.inputActions.get(i);

			if (keyList.size() > GameSetting.INPUT_LIMIT) {
				keyList.removeLast();
			}

			if (!this.playerCharacters[i].getInputCommand().isEmpty() && !this.commandCenter[i].getSkillFlag()) {
				Deque<Key> temp = this.playerCharacters[i].getProcessedCommand();
				temp.addLast(this.playerCharacters[i].getInputCommand().getFirst());

				keyList.add(new Key(this.playerCharacters[i].getInputCommand().getFirst()));

				Action act = this.commandTable.interpretationCommandFromKey(this.playerCharacters[i], temp);
				if (ableAction(this.playerCharacters[i], act)) {
					this.playerCharacters[i].runAction(act, true);
				}

			} else if (actList != null) {
				if (!actList.isEmpty()) {

					if (ableAction(this.playerCharacters[i], actList.getFirst()) && !commandCenter[i].getSkillFlag()) {
						this.commandCenter[i].commandCall(actList.removeFirst().name());
						this.playerCharacters[i].setInputCommand(this.commandCenter[i].getSkillKeys());

					} else if (this.playerCharacters[i].isControl() && !this.commandCenter[i].getSkillFlag()) {
						actList.removeFirst();
					}
				}

				this.inputKeys.get(i).add(this.commandCenter[i].getSkillKey());
				Action act = this.commandTable.interpretationCommandFromKey(this.playerCharacters[i], keyList);
				if (ableAction(this.playerCharacters[i], act)) {
					this.playerCharacters[i].runAction(act, true);
				}
			}
		}
	}

	@Override
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
				// Fightingと共通してcharacterを用いているため、音が鳴る処理が実行されてしまう
				this.playerCharacters[opponentIndex].hitAttack(this.playerCharacters[i], attack, currentFrame);
			}
		}

		for (int i = 0; i < 2; i++) {
			if (isHit[i]) {
				this.playerCharacters[i].setHitConfirm(true);
				this.playerCharacters[i].destroyAttackInstance();
			}

			if (!this.playerCharacters[i].isComboValid(currentFrame)) {
				this.playerCharacters[i].setHitCount(0);
			}
		}
	}

	@Override
	protected void updateAttackParameter() {
		// Updates the parameters of all of projectiles appearing in the stage
		int dequeSize = this.projectileDeque.size();
		for (int i = 0; i < dequeSize; i++) {

			LoopEffect projectile = this.projectileDeque.removeFirst();
			if (projectile.getAttack().updateProjectileAttack()) {
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

	@Override
	protected void updateCharacter() {
		for (int i = 0; i < 2; ++i) {
			// update each character.
			this.playerCharacters[i].update();

			// enque object attack if the data is missile decision
			if (this.playerCharacters[i].getAttack() != null) {
				if (this.playerCharacters[i].getAttack().isProjectile()) {

					this.projectileDeque.addLast(new LoopEffect(this.playerCharacters[i].getAttack(), null));
					this.playerCharacters[i].destroyAttackInstance();
				}
			}

			// change player's direction
			if (playerCharacters[i].isControl()) {
				playerCharacters[i].frontDecision(playerCharacters[i == 0 ? 1 : 0].getHitAreaCenterX());
			}
		}
		// run pushing effect
		detectionPush();
		// run collision of first and second character.
		detectionFusion();
		// run effect when character's are in the end of stage.
		decisionEndStage();

	}

	@Override
	public FrameData createFrameData(int nowFrame, int round, KeyData keyData) {
		CharacterData[] characterData = new CharacterData[2];
		for (int i = 0; i < 2; i++) {
			characterData[i] = new CharacterData(this.playerCharacters[i]);
			characterData[i].setProcessedCommand(this.inputKeys.get(i));
		}

		Deque<AttackData> newAttackDeque = new LinkedList<AttackData>();
		for (LoopEffect loopEffect : this.projectileDeque) {
			newAttackDeque.addLast(new AttackData(loopEffect.getAttack()));
		}

		return new FrameData(characterData, nowFrame, round, newAttackDeque, keyData);
	}

}
