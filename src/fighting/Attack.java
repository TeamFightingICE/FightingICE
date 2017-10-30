package fighting;

public class Attack {
	/**
	 * Refresh the information per frame,and check the result of attack
	 */
	//private HitArea hitAreaNow;

	/**
	 * The number of frame
	 */
	private	int nowFrame;

	/**
	 * Player side`s flag
	 */
	private	boolean playerNumber;

	/**
	 * HitArea`s information and position
	 */
	//private	HitArea hitAreaSetting;

	/**
	 * Attack action's moving value
	 */
	private int settingSpeedX, settingSpeedY;

	/**
	 * Attack action's moving value
	 */
	private int speedX, speedY;

	/**
	 * Attack effect start sign (per frame)
	 */
	private	int startUp;

	/**
	 * Attack action`s active time
	 */
	private	int active;

	/**
	 * Attack action`s damage
	 */
	private	int hitDamage, guardDamage;

	/**
	 * ExEnergy value
	 */
	private	int startAddEnergy, hitAddEnergy, guardAddEnergy, giveEnergy;

	/**
	 * Feedback value
	 */
	private	int impactX,impactY;

	/**
	 * Recovery guard time
	 */
	private	int giveGuardRecov;

	/**
	 * Attack's typeA 1=high 2=mid 3=low
	 */
	private	int attackType;

	/**
	 * Down flag , 1 = can push down 0=normal hit
	 */
	private	boolean downProperty;

	/**
	 * ImageHandle
	 */
	//private	render.Image image;

	/**
	 * The number of frame for the recovery after being hit
	 */
	private	int stunFrameNumber;

	/**
	 * Allow the overlap of stun frames
	 */
	private boolean allowOverlapStunFrame;

}
