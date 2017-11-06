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
		//hitAreaSetting.HitArea(hitAreaInput);

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

	public AttackData(Attack attack){
		if(attack == null){}
		else{
			hitAreaNow = new HitArea();
			//hitAreaNow.setParameters(attack.getHitAreaNow());
			playerNumber = attack.isPlayerNumber();

			hitAreaSetting = new HitArea();
			//hitAreaSetting.setParameters(attack.getHitAreaSetting());

			settingSpeedX = attack.getSettingSpeedX();
			settingSpeedY = attack.getSettingSpeedY();
			speedX = attack.getSpeedX();
			speedY = attack.getSpeedY();
			startUp = attack.getStartUp();
			active = attack.getActive();
			hitDamage = attack.getHitDamage();
			guardDamage = attack.getGuardDamage();
			startAddEnergy = attack.getStartAddEnergy();
			hitAddEnergy = attack.getHitAddEnergy();
			guardAddEnergy = attack.getGuardAddEnergy();
			giveEnergy = attack.getGiveEnergy();
			impactX = attack.getImpactX();
			impactY = attack.getImpactY();
			giveGuardRecov = attack.getGiveGuardRecov();
			attackType = attack.getAttackType();
			downProperty = attack.isDownProperty();
		}
	}

	public boolean isPlayerNumber() {
		return playerNumber;
	}

	public boolean isDownProperty() {
		return downProperty;
	}

	// get
	public int getNowFrame() {
		return nowFrame;
	}

	public int getPlayerNumber() {
		return playerNumber ? 0 : 1;
	}

	public int getSpeedX() {
		return speedX;
	}

	public int getSpeedY() {
		return speedY;
	}

	public int getStartUp() {
		return startUp;
	}

	public int getActive() {
		return active;
	}

	public int getHitDamage() {
		return hitDamage;
	}

	public int getGuardDamage() {
		return guardDamage;
	}

	public int getStartAddEnergy() {
		return startAddEnergy;
	}

	public int getHitAddEnergy() {
		return hitAddEnergy;
	}

	public int getGuardAddEnergy() {
		return guardAddEnergy;
	}

	public int getGiveEnergy() {
		return giveEnergy;
	}

	public int getImpactX() {
		return impactX;
	}

	public int getImpactY() {
		return impactY;
	}

	public int getGiveGuardRecov() {
		return giveGuardRecov;
	}

	public int getAttackType() {
		return attackType;
	}

	public int getSettingSpeedX() {
		return settingSpeedX;
	}

	public int getSettingSpeedY() {
		return settingSpeedY;
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
