package fighting;

import struct.AttackData;
import struct.HitArea;

public class Attack {

	private HitArea settingHitArea;

	private int settingSpeedX;

	private int settingSpeedY;

	private HitArea currentHitArea;

	private int currentFrame;

	private boolean playerNumber;

	private int speedX;

	private int speedY;

	private int startUp;

	private int active;

	private int hitDamage;

	private int guardDamage;

	private int startAddEnergy;

	private int hitAddEnergy;

	private int guardAddEnergy;

	private int giveEnergy;

	private int impactX;

	private int impactY;

	private int giveGuardRecov;

	private int attackType;

	private boolean downProperty;

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

	public boolean isPlayerNumber() {
		return this.playerNumber;
	}

	public boolean isDownProperty() {
		return this.downProperty;
	}

	public boolean isProjectile() {
		return (this.settingSpeedX + this.settingSpeedY) != 0;
	}

	// get
	public int getCurrentFrame() {
		return this.currentFrame;
	}

	public int getPlayerNumber() {
		return this.playerNumber ? 0 : 1;
	}

	public int getSpeedX() {
		return this.speedX;
	}

	public int getSpeedY() {
		return this.speedY;
	}

	public int getStartUp() {
		return this.startUp;
	}

	public int getActive() {
		return this.active;
	}

	public int getHitDamage() {
		return this.hitDamage;
	}

	public int getGuardDamage() {
		return this.guardDamage;
	}

	public int getStartAddEnergy() {
		return this.startAddEnergy;
	}

	public int getHitAddEnergy() {
		return this.hitAddEnergy;
	}

	public int getGuardAddEnergy() {
		return this.guardAddEnergy;
	}

	public int getGiveEnergy() {
		return this.giveEnergy;
	}

	public int getImpactX() {
		return this.impactX;
	}

	public int getImpactY() {
		return this.impactY;
	}

	public int getGiveGuardRecov() {
		return this.giveGuardRecov;
	}

	public int getAttackType() {
		return this.attackType;
	}

	public int getSettingSpeedX() {
		return this.settingSpeedX;
	}

	public int getSettingSpeedY() {
		return this.settingSpeedY;
	}

	public HitArea getSettingHitArea() {
		return new HitArea(this.settingHitArea);
	}

	public HitArea getCurrentHitArea() {
		return new HitArea(this.currentHitArea);
	}

	////// Setter//////
	public void setCurrentFrame(int nowFrame) {
		this.currentFrame = nowFrame;
	}

	public void setPlayerNumber(boolean playerNumber) {
		this.playerNumber = playerNumber;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}

	public void setStartUp(int startUp) {
		this.startUp = startUp;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public void setHitDamage(int hitDamage) {
		this.hitDamage = hitDamage;
	}

	public void setGuardDamage(int guardDamage) {
		this.guardDamage = guardDamage;
	}

	public void setStartAddEnergy(int startAddEnergy) {
		this.startAddEnergy = startAddEnergy;
	}

	public void setHitAddEnergy(int hitAddEnergy) {
		this.hitAddEnergy = hitAddEnergy;
	}

	public void setGuardAddEnergy(int guardAddEnergy) {
		this.guardAddEnergy = guardAddEnergy;
	}

	public void setGiveEnergy(int giveEnergy) {
		this.giveEnergy = giveEnergy;
	}

	public void setImpactX(int impactX) {
		this.impactX = impactX;
	}

	public void setImpactY(int impactY) {
		this.impactY = impactY;
	}

	public void setGiveGuardRecov(int giveGuardRecov) {
		this.giveGuardRecov = giveGuardRecov;
	}

	public void setAttackType(int attackType) {
		this.attackType = attackType;
	}

	public void setDownProperty(boolean downProperty) {
		this.downProperty = downProperty;
	}

	public void setSettingSpeedX(int settingSpeedX) {
		this.settingSpeedX = settingSpeedX;
	}

	public void setSettingSpeedY(int settingSpeedY) {
		this.settingSpeedY = settingSpeedY;
	}

}
