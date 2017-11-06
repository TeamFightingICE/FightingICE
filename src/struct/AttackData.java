package struct;

import fighting.Attack;

public class AttackData {
	// 要:コメントの打ち直し
	private HitArea hitAreaNow;

	private int nowFrame;

	private boolean playerNumber;

	private HitArea hitAreaSetting;

	private int settingSpeedX;
	private int settingSpeedY;

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

	public AttackData() {
		this.settingSpeedX = 0;
		this.settingSpeedY = 0;
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

	public AttackData(HitArea hitAreaInput, int speedXInput, int speedYInput, int invokeInput, int activeInput,
			int hitDamegeInput, int guardDamageInput, int startAddEnergyInput, int hitAddEnergyInput,
			int guardAddEnergyInput, int giveEnergyInput, int impactXInput, int impactYInput, int giveGuardRecovInput,
			int attackTypeInput, boolean downPropInput) {
		this.hitAreaNow = new HitArea();

		this.hitAreaSetting = new HitArea();
		// hitAreaSetting.HitArea(hitAreaInput);

		this.settingSpeedX = speedXInput;
		this.settingSpeedY = speedYInput;
		this.startUp = invokeInput;
		this.active = activeInput;
		this.hitDamage = hitDamegeInput;
		this.guardDamage = guardDamageInput;
		this.startAddEnergy = startAddEnergyInput;
		this.hitAddEnergy = hitAddEnergyInput;
		this.guardAddEnergy = guardAddEnergyInput;
		this.giveEnergy = giveEnergyInput;
		this.impactX = impactXInput;
		this.impactY = impactYInput;
		this.giveGuardRecov = giveGuardRecovInput;
		this.attackType = attackTypeInput;
		this.downProperty = downPropInput;
	}

	public AttackData(Attack attack) {
		if (attack == null) {
		} else {
			this.hitAreaNow = new HitArea();
			// this.hitAreaNow.setParameters(attack.getHitAreaNow());
			this.playerNumber = attack.isPlayerNumber();

			this.hitAreaSetting = new HitArea();
			// this.hitAreaSetting.setParameters(attack.getHitAreaSetting());

			this.settingSpeedX = attack.getSettingSpeedX();
			this.settingSpeedY = attack.getSettingSpeedY();
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
		}
	}

	public boolean isPlayerNumber() {
		return this.playerNumber;
	}

	public boolean isDownProperty() {
		return this.downProperty;
	}

	// get
	public int getNowFrame() {
		return this.nowFrame;
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

	// set
	public void setNowFrame(int nowFrame) {
		this.nowFrame = nowFrame;
	}

	public void setPlayerNumber(boolean playerNumber) {
		this.playerNumber = playerNumber;
	}

	public void setSpeedX(int speed_x) {
		this.speedX = speed_x;
	}

	public void setSpeedY(int speed_y) {
		this.speedY = speed_y;
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
