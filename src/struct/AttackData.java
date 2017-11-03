package struct;

public class AttackData {
	// 要:コメントの打ち直し
	// private HitArea hitAreaNow;

	private int nowFrame;

	private boolean playerNumber;

	// private HitArea hitAreaSetting;

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

	private int stunFrameNumber;

	private boolean allowOverlapStunFrame;

	public int getNowFrame() {
		return nowFrame;
	}

	public void setNowFrame(int nowFrame) {
		this.nowFrame = nowFrame;
	}

	public boolean isPlayerNumber() {
		return playerNumber;
	}

	public int getPlayerNumber() {
		return playerNumber ? 0 : 1;
	}

	public void setPlayerNumber(boolean playerNumber) {
		this.playerNumber = playerNumber;
	}

	public int getSpeedX() {
		return speedX;
	}

	public void setSpeedX(int speed_x) {
		this.speedX = speed_x;
	}

	public int getSpeedY() {
		return speedY;
	}

	public void setSpeedY(int speed_y) {
		this.speedY = speed_y;
	}

	public int getStartUp() {
		return startUp;
	}

	public void setStartUp(int startUp) {
		this.startUp = startUp;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public int getHitDamage() {
		return hitDamage;
	}

	public void setHitDamage(int hitDamage) {
		this.hitDamage = hitDamage;
	}

	public int getGuardDamage() {
		return guardDamage;
	}

	public void setGuardDamage(int guardDamage) {
		this.guardDamage = guardDamage;
	}

	public int getStartAddEnergy() {
		return startAddEnergy;
	}

	public void setStartAddEnergy(int startAddEnergy) {
		this.startAddEnergy = startAddEnergy;
	}

	public int getHitAddEnergy() {
		return hitAddEnergy;
	}

	public void setHitAddEnergy(int hitAddEnergy) {
		this.hitAddEnergy = hitAddEnergy;
	}

	public int getGuardAddEnergy() {
		return guardAddEnergy;
	}

	public void setGuardAddEnergy(int guardAddEnergy) {
		this.guardAddEnergy = guardAddEnergy;
	}

	public int getGiveEnergy() {
		return giveEnergy;
	}

	public void setGiveEnergy(int giveEnergy) {
		this.giveEnergy = giveEnergy;
	}

	public int getImpactX() {
		return impactX;
	}

	public void setImpactX(int impactX) {
		this.impactX = impactX;
	}

	public int getImpactY() {
		return impactY;
	}

	public void setImpactY(int impactY) {
		this.impactY = impactY;
	}

	public int getGiveGuardRecov() {
		return giveGuardRecov;
	}

	public void setGiveGuardRecov(int giveGuardRecov) {
		this.giveGuardRecov = giveGuardRecov;
	}

	public int getAttackType() {
		return attackType;
	}

	public void setAttackType(int attackType) {
		this.attackType = attackType;
	}

	public boolean isDownProperty() {
		return downProperty;
	}

	public void setDownProperty(boolean downProperty) {
		this.downProperty = downProperty;
	}

	public int getSettingSpeedX() {
		return settingSpeedX;
	}

	public void setSettingSpeedX(int settingSpeedX) {
		this.settingSpeedX = settingSpeedX;
	}

	public int getSettingSpeedY() {
		return settingSpeedY;
	}

	public void setSettingSpeedY(int settingSpeedY) {
		this.settingSpeedY = settingSpeedY;
	}

	public int getStunFrameNumber() {
		return this.stunFrameNumber;
	}

	public void setAllowOverlapStunFrame(boolean allowOverlapStunFrame) {
		this.allowOverlapStunFrame = allowOverlapStunFrame;
	}

	public boolean getAllowOverlapStunFrame() {
		return allowOverlapStunFrame;
	}

}
