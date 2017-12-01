package struct;

import fighting.Attack;

public class AttackData {

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

	private boolean isProjectile;

	public AttackData() {
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
		this.isProjectile = false;
	}

	public AttackData(Attack attack) {
		if (attack != null) {
			this.currentHitArea = attack.getCurrentHitArea();
			this.currentFrame = attack.getCurrentFrame();
			this.playerNumber = attack.isPlayerNumber();
			this.speedX = attack.getSpeedX();
			this.speedY = attack.getSpeedY();
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
			this.isProjectile = attack.isProjectile();
		}
	}

	public AttackData(AttackData attackData) {
		if (attackData != null) {
			this.currentHitArea = attackData.getCurrentHitArea();
			this.currentFrame = attackData.getCurrentFrame();
			this.playerNumber = attackData.isPlayerNumber();
			this.speedX = attackData.getSpeedX();
			this.speedY = attackData.getSpeedY();
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
			this.isProjectile = attackData.isProjectile();
		}
	}

	//////// Getter//////////

	public boolean isPlayerNumber() {
		return this.playerNumber;
	}

	public boolean isDownProperty() {
		return this.downProperty;
	}

	public boolean isProjectile() {
		return this.isProjectile;
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

	public void setIsProjectile(boolean isProjectile) {
		this.isProjectile = isProjectile;
	}

}
