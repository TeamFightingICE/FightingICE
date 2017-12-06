package fighting;

import struct.AttackData;
import struct.HitArea;
import struct.MotionData;

/**
 * 攻撃のダメージ量などのキャラクターの攻撃を扱うクラス
 */
public class Attack {

	/**
	 * HitArea's information of this attack hit box set in Motion.csv
	 *
	 * @see HitArea
	 */
	private HitArea settingHitArea;

	/**
	 * The absolute value of the horizontal speed of the attack hit box (zero
	 * means the attack hit box will track the character)
	 */
	private int settingSpeedX;

	/**
	 * The absolute value of the vertical speed of the attack hit box (zero
	 * means the attack hit box will track the character)
	 */
	private int settingSpeedY;

	/**
	 * HitArea's information of this attack hit box in the current frame
	 *
	 * @see HitArea
	 */
	private HitArea currentHitArea;

	/**
	 * The number of frames since this attack was used
	 */
	private int currentFrame;

	/**
	 * The player side`s flag
	 */
	private boolean playerNumber;

	/**
	 * The horizontal speed of the attack hit box (minus when moving left and
	 * plus when moving right)
	 */
	private int speedX;

	/**
	 * The vertical speed of the attack hit box (minus when moving up and plus
	 * when moving down)
	 */
	private int speedY;

	/**
	 * The number of frames in Startup
	 *
	 * @see MotionData#attackStartUp
	 */
	private int startUp;

	/**
	 * The number of frames in Active
	 *
	 * @see MotionData#attackActive
	 */
	private int active;

	/**
	 * The damage value to the unguarded opponent hit by this skill
	 */
	private int hitDamage;

	/**
	 * The damage value to the guarded opponent hit by this skill
	 */
	private int guardDamage;

	/**
	 * The value of the energy added to the character when it uses this skill
	 */
	private int startAddEnergy;

	/**
	 * The value of the energy added to the character when this skill hits the
	 * opponent
	 */
	private int hitAddEnergy;

	/**
	 * The value of the energy added to the character when this skill is blocked
	 * by the opponent
	 */
	private int guardAddEnergy;

	/**
	 * The value of the energy added to the opponent when it is hit by this
	 * skill
	 */
	private int giveEnergy;

	/**
	 * The change in the horizontal speed of the opponent when it is hit by this
	 * skill
	 */
	private int impactX;

	/**
	 * The change in the vertical speed of the opponent when it is hit by this
	 * skill
	 */
	private int impactY;

	/**
	 * The number of frames that the guarded opponent needs to resume to his
	 * normal status after being hit by this skill
	 */
	private int giveGuardRecov;

	/**
	 * The value of the attack type: 1 = high, 2 = middle, 3 = low, 4 = throw.
	 */
	private int attackType;

	/**
	 * The flag whether this skill can push down the opponent when hit , 1 = can
	 * push down 0=normal hit
	 */
	private boolean downProperty;

	/**
	 * Attackのコンストラクタ
	 */
	public Attack() {
		this.settingHitArea = new HitArea();
		this.settingSpeedX = 0;
		this.settingSpeedY = 0;

		this.currentHitArea = new HitArea();
		this.currentFrame = -1;
		this.playerNumber = true;
		this.speedX = 0;
		this.speedY = 0;
		this.startUp = 0;
		this.active = 0;

		this.hitDamage = 0;
		this.guardDamage = 0;
		this.startAddEnergy = 0;
		this.hitAddEnergy = 0;
		this.guardAddEnergy = 0;
		this.giveEnergy = 0;

		this.impactX = 0;
		this.impactY = 0;
		this.giveGuardRecov = 0;
		this.attackType = 0;
		this.downProperty = false;
	}

	/**
	 * 指定された値でAttackのインスタンスを作成するコンストラクタ
	 *
	 * @param attack
	 *            攻撃データ
	 */
	public Attack(Attack attack) {
		if (attack != null) {
			this.settingHitArea = attack.getSettingHitArea();
			this.settingSpeedX = attack.getSettingSpeedX();
			this.settingSpeedY = attack.getSettingSpeedY();

			this.currentHitArea = attack.getCurrentHitArea();
			this.currentFrame = attack.getCurrentFrame();
			this.playerNumber = attack.isPlayerNumber();
			this.speedX = attack.getSpeedX();
			this.speedY = attack.getSpeedY();
			;
			this.startUp = attack.getStartUp();
			this.active = attack.getActive();

			this.hitDamage = attack.getHitDamage();
			this.guardDamage = attack.getGuardDamage();
			this.startAddEnergy = attack.getStartAddEnergy();
			this.hitAddEnergy = attack.getHitAddEnergy();
			this.guardAddEnergy = attack.getGuardAddEnergy();
			this.giveEnergy = attack.getGiveEnergy();

			this.impactX = attack.getImpactX();
			this.impactY = attack.getImpactY();
			this.giveGuardRecov = attack.getGiveGuardRecov();
			this.attackType = attack.getAttackType();
			this.downProperty = attack.isDownProperty();
		}
	}

	/**
	 * 指定された値でAttackのインスタンスを作成するコンストラクタ
	 *
	 * @param attackData
	 *            攻撃データ
	 */
	public Attack(AttackData attackData) {
		if (attackData != null) {
			this.settingHitArea = attackData.getSettingHitArea();
			this.settingSpeedX = attackData.getSettingSpeedX();
			this.settingSpeedY = attackData.getSettingSpeedY();

			this.currentHitArea = attackData.getCurrentHitArea();
			this.currentFrame = attackData.getCurrentFrame();
			this.playerNumber = attackData.isPlayerNumber();
			this.speedX = attackData.getSpeedX();
			this.speedY = attackData.getSpeedY();
			;
			this.startUp = attackData.getStartUp();
			this.active = attackData.getActive();

			this.hitDamage = attackData.getHitDamage();
			this.guardDamage = attackData.getGuardDamage();
			this.startAddEnergy = attackData.getStartAddEnergy();
			this.hitAddEnergy = attackData.getHitAddEnergy();
			this.guardAddEnergy = attackData.getGuardAddEnergy();
			this.giveEnergy = attackData.getGiveEnergy();

			this.impactX = attackData.getImpactX();
			this.impactY = attackData.getImpactY();
			this.giveGuardRecov = attackData.getGiveGuardRecov();
			this.attackType = attackData.getAttackType();
			this.downProperty = attackData.isDownProperty();
		}
	}

	/**
	 * 指定された値でAttackのインスタンスを作成するコンストラクタ
	 *
	 * @param hitArea
	 *            HitArea's information of this attack hit box set in Motion.csv
	 * @param settingSpeedX
	 *            The absolute value of the horizontal speed of the attack hit
	 *            box (zero means the attack hit box will track the character)
	 * @param settingSpeedY
	 *            The absolute value of the vertical speed of the attack hit box
	 *            (zero means the attack hit box will track the character)
	 * @param startUp
	 *            The number of frames in Startup
	 * @param active
	 *            The number of frames in Active
	 * @param hitDamage
	 *            The damage value to the unguarded opponent hit by this skill
	 * @param guardDamage
	 *            The damage value to the guarded opponent hit by this skill
	 * @param startAddEnergy
	 *            The value of the energy added to the character when it uses
	 *            this skill
	 * @param hitAddEnergy
	 *            The value of the energy added to the character when this skill
	 *            hits the opponent
	 * @param guardAddEnergy
	 *            The value of the energy added to the character when this skill
	 *            is blocked by the opponent
	 * @param giveEnergy
	 *            The value of the energy added to the opponent when it is hit
	 *            by this skill
	 * @param impactX
	 *            The change in the horizontal speed of the opponent when it is
	 *            hit by this skill
	 * @param impactY
	 *            The change in the vertical speed of the opponent when it is
	 *            hit by this skill
	 * @param giveGuardRecov
	 *            The number of frames that the guarded opponent needs to resume
	 *            to his normal status after being hit by this skill
	 * @param attackType
	 *            The value of the attack type: 1 = high, 2 = middle, 3 = low, 4
	 *            = throw.
	 * @param downProp
	 *            The flag whether this skill can push down the opponent when
	 *            hit , 1 = can push down 0=normal hit
	 */
	public Attack(HitArea hitArea, int settingSpeedX, int settingSpeedY, int startUp, int active, int hitDamage,
			int guardDamage, int startAddEnergy, int hitAddEnergy, int guardAddEnergy, int giveEnergy, int impactX,
			int impactY, int giveGuardRecov, int attackType, boolean downProp) {

		this.settingHitArea = hitArea;
		this.settingSpeedX = settingSpeedX;
		this.settingSpeedY = settingSpeedY;

		this.currentHitArea = new HitArea();

		this.startUp = startUp;
		this.active = active;

		this.hitDamage = hitDamage;
		this.guardDamage = guardDamage;
		this.startAddEnergy = startAddEnergy;
		this.hitAddEnergy = hitAddEnergy;
		this.guardAddEnergy = guardAddEnergy;
		this.giveEnergy = giveEnergy;

		this.impactX = impactX;
		this.impactY = impactY;
		this.giveGuardRecov = giveGuardRecov;
		this.attackType = attackType;
		this.downProperty = downProp;
	}

	/**
	 * 攻撃オブジェクトの初期化を行う
	 *
	 * @param playerNumber
	 *            攻撃をP1かP2
	 */
	public void initialize(boolean playerNumber, int x, int y, int size, boolean direction) {
		setParameters(x, y, size, direction);
		this.playerNumber = playerNumber;
		this.currentFrame = 0;
	}

	/**
	 * 波動拳の当たり判定の座標位置と発生からの経過フレームを更新し, まだ攻撃がアクティブかどうかを返す
	 *
	 * @return 攻撃がまだアクティブかどうか
	 */
	public boolean updateProjectileAttack() {
		this.currentHitArea.move(speedX, speedY);

		return ++this.currentFrame <= this.active;
	}

	/**
	 * 波動拳以外の攻撃の当たり判定の座標位置と発生からの経過フレームを更新し, まだ攻撃がアクティブかどうかを返す
	 *
	 * @param character
	 *            攻撃を出したキャラクターの情報
	 *
	 * @return 攻撃がまだアクティブかどうか
	 */
	public boolean update(Character character) {
		setParameters(character.getX(), character.getY(), character.getGraphicSizeX(), character.isFront());

		return ++this.currentFrame <= this.active;
	}

	/**
	 * キャラクターのパラメータを設定するメソッド
	 *
	 * @param x
	 *            キャラクターのx座標
	 * @param y
	 *            キャラクターのy座標
	 * @param size
	 *            当たり判定の大きさ
	 * @param direction
	 *            キャラクターの向いている方向(1: 右方向, 0:左方向)
	 */
	private void setParameters(int x, int y, int size, boolean direction) {
		int left;
		int right;
		int top;
		int bottom;

		if (direction) {
			left = x + this.settingHitArea.getLeft();
			right = x + this.settingHitArea.getRight();
			this.speedX = this.settingSpeedX;
			this.speedY = this.settingSpeedY;

		} else {
			// ちょっとバグが発生する可能性あり
			left = x + size - this.settingHitArea.getRight();
			right = x + size - this.settingHitArea.getLeft();
			this.speedX = -this.settingSpeedX;
			this.speedY = this.settingSpeedY;
		}

		top = y + this.settingHitArea.getTop();
		bottom = y + this.settingHitArea.getBottom();

		this.currentHitArea = new HitArea(left, right, top, bottom);
	}

	//////// Getter//////////

	/**
	 * Returns the player side's flag.
	 *
	 * @return true: P1; false: P2
	 */
	public boolean isPlayerNumber() {
		return this.playerNumber;
	}

	/**
	 * Returns the flag whether this skill can push down the opponent when hit.
	 *
	 * @return The flag whether this skill can push down the opponent when hit
	 */
	public boolean isDownProperty() {
		return this.downProperty;
	}

	/**
	 * Returns the boolean value whether this skill is projectile or not.
	 *
	 * @return true: this skill is projectile; false: otherwise
	 */
	public boolean isProjectile() {
		return (this.settingSpeedX + this.settingSpeedY) != 0;
	}

	// get

	/**
	 * Returns the number of frames since this attack was used.
	 *
	 * @return The number of frames since this attack was used
	 */
	public int getCurrentFrame() {
		return this.currentFrame;
	}

	/**
	 * Returns the integer number indicating the player of the attack. (0: P1;
	 * 1: P2)
	 *
	 * @return The integer number indicating the player of the attack.
	 */
	public int getPlayerNumber() {
		return this.playerNumber ? 0 : 1;
	}

	/**
	 * Returns the horizontal speed of the attack hit box (minus when moving
	 * left and plus when moving right).
	 *
	 * @return The horizontal speed of the attack hit box (minus when moving
	 *         left and plus when moving right)
	 */
	public int getSpeedX() {
		return this.speedX;
	}

	/**
	 * Returns the vertical speed of the attack hit box (minus when moving up
	 * and plus when moving down).
	 *
	 * @return The vertical speed of the attack hit box (minus when moving up
	 *         and plus when moving down)
	 */
	public int getSpeedY() {
		return this.speedY;
	}

	/**
	 * Returns the number of frames in Startup.
	 *
	 * @see MotionData#attackStartUp
	 *
	 * @return The number of frames in Startup
	 */
	public int getStartUp() {
		return this.startUp;
	}

	/**
	 * Returns the number of frames in Active.
	 *
	 * @see MotionData#attackActive
	 *
	 * @return The number of frames in Active
	 */
	public int getActive() {
		return this.active;
	}

	/**
	 * Returns the damage value to the unguarded opponent hit by this skill.
	 *
	 * @return The damage value to the unguarded opponent hit by this skill
	 */
	public int getHitDamage() {
		return this.hitDamage;
	}

	/**
	 * Returns the damage value to the guarded opponent hit by this skill.
	 *
	 * @return The damage value to the guarded opponent hit by this skill
	 */
	public int getGuardDamage() {
		return this.guardDamage;
	}

	/**
	 * Returns the value of the energy added to the character when it uses this
	 * skill.
	 *
	 * @return The value of the energy added to the character when it uses this
	 *         skill
	 */
	public int getStartAddEnergy() {
		return this.startAddEnergy;
	}

	/**
	 * Returns the value of the energy added to the character when this skill
	 * hits the opponent.
	 *
	 * @return The value of the energy added to the character when this skill
	 *         hits the opponent
	 */
	public int getHitAddEnergy() {
		return this.hitAddEnergy;
	}

	/**
	 * Returns the value of the energy added to the character when this skill is
	 * blocked by the opponent.
	 *
	 * @return The value of the energy added to the character when this skill is
	 *         blocked by the opponent
	 */
	public int getGuardAddEnergy() {
		return this.guardAddEnergy;
	}

	/**
	 * Returns the value of the energy added to the opponent when it is hit by
	 * this skill.
	 *
	 * @return The value of the energy added to the opponent when it is hit by
	 *         this skill
	 */
	public int getGiveEnergy() {
		return this.giveEnergy;
	}

	/**
	 * Returns the change in the horizontal speed of the opponent when it is hit
	 * by this skill.
	 *
	 * @return The change in the horizontal speed of the opponent when it is hit
	 *         by this skill
	 */
	public int getImpactX() {
		return this.impactX;
	}

	/**
	 * Returns the change in the vertical speed of the opponent when it is hit
	 * by this skill.
	 *
	 * @return The change in the vertical speed of the opponent when it is hit
	 *         by this skill
	 */
	public int getImpactY() {
		return this.impactY;
	}

	/**
	 * Returns the number of frames that the guarded opponent needs to resume to
	 * its normal status after being hit by this skill.
	 *
	 * @return The number of frames that the guarded opponent needs to resume to
	 *         its normal status after being hit by this skill
	 */
	public int getGiveGuardRecov() {
		return this.giveGuardRecov;
	}

	/**
	 * Returns the value of the attack type: 1 = high, 2 = middle, 3 = low, 4 =
	 * throw.
	 *
	 * @return The value of the attack type
	 */
	public int getAttackType() {
		return this.attackType;
	}

	/**
	 * Returns the absolute value of the horizontal speed of the attack hit box
	 * (zero means the attack hit box will track the character).
	 *
	 * @return The absolute value of the horizontal speed of the attack hit box
	 *         (zero means the attack hit box will track the character)
	 */
	public int getSettingSpeedX() {
		return this.settingSpeedX;
	}

	/**
	 * Returns the absolute value of the vertical speed of the attack hit box
	 * (zero means the attack hit box will track the character).
	 *
	 * @return The absolute value of the vertical speed of the attack hit box
	 *         (zero means the attack hit box will track the character)
	 */
	public int getSettingSpeedY() {
		return this.settingSpeedY;
	}

	/**
	 * Returns HitArea's setting information.
	 *
	 *
	 * @return HitArea's setting information
	 */
	public HitArea getSettingHitArea() {
		return new HitArea(this.settingHitArea);
	}

	/**
	 * Returns HitArea's information of this attack hit box in the current
	 * frame.
	 *
	 * @see HitArea
	 *
	 * @return HitArea's information of this attack hit box in the current frame
	 */
	public HitArea getCurrentHitArea() {
		return new HitArea(this.currentHitArea);
	}

	////// Setter//////

	/**
	 * Sets the number of frames since this attack was used.
	 *
	 * @param nowFrame
	 *            The number of frames since this attack was used
	 */
	public void setCurrentFrame(int nowFrame) {
		this.currentFrame = nowFrame;
	}

	/**
	 * Sets the player side's flag.
	 *
	 * @param playerNumber
	 *            The player side's flag
	 */
	public void setPlayerNumber(boolean playerNumber) {
		this.playerNumber = playerNumber;
	}

	/**
	 * Sets the horizontal speed of the attack hit box (minus when moving left
	 * and plus when moving right).
	 *
	 * @param speedX
	 *            The horizontal speed of the attack hit box (minus when moving
	 *            left and plus when moving right)
	 */
	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	/**
	 * Sets the vertical speed of the attack hit box (minus when moving up and
	 * plus when moving down).
	 *
	 * @param speedY
	 *            The vertical speed of the attack hit box (minus when moving up
	 *            and plus when moving down)
	 */
	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}

	/**
	 * Sets the number of frames in Startup.
	 *
	 * @see MotionData#attackStartUp
	 *
	 * @param startUp
	 *            The number of frames in Startup
	 */
	public void setStartUp(int startUp) {
		this.startUp = startUp;
	}

	/**
	 * Sets the number of frames in Active.
	 *
	 * @see MotionData#attackActive
	 *
	 * @param active
	 *            The number of frames in Active
	 */
	public void setActive(int active) {
		this.active = active;
	}

	/**
	 * Sets the damage value to the unguarded opponent hit by this skill.
	 *
	 * @param hitDamage
	 *            The damage value to the unguarded opponent hit by this skill
	 */
	public void setHitDamage(int hitDamage) {
		this.hitDamage = hitDamage;
	}

	/**
	 * Sets the damage value to the guarded opponent hit by this skill.
	 *
	 * @param guardDamage
	 *            The damage value to the guarded opponent hit by this skill
	 */
	public void setGuardDamage(int guardDamage) {
		this.guardDamage = guardDamage;
	}

	/**
	 * Sets the value of energy added to the character when it uses this skill.
	 *
	 * @param startAddEnergy
	 *            The value of the energy added to the character when it uses
	 *            this skill
	 */
	public void setStartAddEnergy(int startAddEnergy) {
		this.startAddEnergy = startAddEnergy;
	}

	/**
	 * Sets the value of the energy added to the character when this skill hits
	 * the opponent.
	 *
	 * @param hitAddEnergy
	 *            The value of the energy added to the character when this skill
	 *            hits the opponent
	 */
	public void setHitAddEnergy(int hitAddEnergy) {
		this.hitAddEnergy = hitAddEnergy;
	}

	/**
	 * Sets the value of the energy added to the character when this skill is
	 * blocked by the opponent.
	 *
	 * @param guardAddEnergy
	 *            The value of the energy added to the character when this skill
	 *            is blocked by the opponent
	 */
	public void setGuardAddEnergy(int guardAddEnergy) {
		this.guardAddEnergy = guardAddEnergy;
	}

	/**
	 * Sets the value of the energy added to the opponent when it is hit by this
	 * skill.
	 *
	 * @param giveEnergy
	 *            The value of the energy added to the opponent when it is hit
	 *            by this skill
	 */
	public void setGiveEnergy(int giveEnergy) {
		this.giveEnergy = giveEnergy;
	}

	/**
	 * Sets the change in the horizontal speed of the opponent when it is hit by
	 * this skill.
	 *
	 * @param impactX
	 *            The change in the horizontal speed of the opponent when it is
	 *            hit by this skill
	 */
	public void setImpactX(int impactX) {
		this.impactX = impactX;
	}

	/**
	 * Sets the change in the vertical speed of the opponent when it is hit by
	 * this skill.
	 *
	 * @param impactY
	 *            The change in the vertical speed of the opponent when it is
	 *            hit by this skill
	 */
	public void setImpactY(int impactY) {
		this.impactY = impactY;
	}

	/**
	 * Sets the number of frames that the guarded opponent needs to resume to
	 * his normal status after being hit by this skill.
	 *
	 * @param giveGuardRecov
	 *            The number of frames that the guarded opponent needs to resume
	 *            to his normal status after being hit by this skill
	 */
	public void setGiveGuardRecov(int giveGuardRecov) {
		this.giveGuardRecov = giveGuardRecov;
	}

	/**
	 * Sets the value of the attack type: 1 = high, 2 = middle, 3 = low, 4 =
	 * throw.
	 *
	 * @param attackType
	 *            The value of the attack type
	 */
	public void setAttackType(int attackType) {
		this.attackType = attackType;
	}

	/**
	 * Sets the flag whether this skill can push down the opponent when hit.
	 *
	 * @param downProperty
	 *            The flag whether this skill can push down the opponent when
	 *            hit
	 */
	public void setDownProperty(boolean downProperty) {
		this.downProperty = downProperty;
	}

	/**
	 * Sets the absolute value of the horizontal speed of the attack hit box
	 * (zero means the attack hit box will track the character).
	 *
	 * @param settingSpeedX
	 *            The absolute value of the horizontal speed of the attack hit
	 *            box (zero means the attack hit box will track the character)
	 */
	public void setSettingSpeedX(int settingSpeedX) {
		this.settingSpeedX = settingSpeedX;
	}

	/**
	 * Sets the absolute value of the vertical speed of the attack hit box (zero
	 * means the attack hit box will track the character).
	 *
	 * @param settingSpeedY
	 *            The absolute value of the vertical speed of the attack hit box
	 *            (zero means the attack hit box will track the character)
	 */
	public void setSettingSpeedY(int settingSpeedY) {
		this.settingSpeedY = settingSpeedY;
	}

}
