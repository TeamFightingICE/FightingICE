package fighting;

import java.awt.RenderingHints;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import enumerate.State;
import image.CharacterActionImage;
import image.Image;
import manager.GraphicManager;
import setting.LaunchSetting;
import struct.HitArea;
import struct.MotionData;

/**
 * キャラクターが使用できる全モーションのパラメータ及び，それに対応する画像を管理するクラス．
 */
public class Motion {

	/**
	 * This motion's name.
	 */
	private String actionName;

	/**
	 * The number of frames in this motion.
	 */
	private int frameNumber;

	/**
	 * The speed value in the horizontal direction that will be applied to the
	 * character when it does this motion.
	 */
	private int speedX;

	/**
	 * The speed value in the vertical direction that will be applied to the
	 * character when it does this motion.
	 */
	private int speedY;

	/**
	 * The information on the character's hit box.
	 */
	private HitArea characterHitArea;

	/**
	 * The resulting state after running this motion.
	 */
	private State state;

	/**
	 * The information on the attack hit box.
	 */
	private HitArea attackHitArea;

	/**
	 * The horizontal speed of the attack hit box.
	 */
	private int attackSpeedX;

	/**
	 * The vertical speed of the attack hit box.
	 */
	private int attackSpeedY;

	/**
	 * The number of startup frames required for this motion to become active.
	 */
	private int attackStartUp;

	/**
	 * The number of active frames of this motion.
	 */
	private int attackActive;

	/**
	 * The value of the damage to the unguarding opponent when it is hit by this
	 * motion.
	 */
	private int attackHitDamage;

	/**
	 * The value of the damage to the guarding opponent when it is hit by this
	 * motion.
	 */
	private int attackGuardDamage;

	/**
	 * The value of the energy added to the character when it uses this motion.
	 */
	private int attackStartAddEnergy;

	/**
	 * The value of the energy added to the character when this motion hits the
	 * opponent.
	 */
	private int attackHitAddEnergy;

	/**
	 * The value of the energy added to the character when this motion is
	 * guarded by the guarding opponent.
	 */
	private int attackGuardAddEnergy;

	/**
	 * The value of the energy given to the opponent when it is hit by this
	 * motion.
	 */
	private int attackGiveEnergy;

	/**
	 * The change in the horizontal speed of the opponent when it is hit by this
	 * motion.
	 */
	private int attackImpactX;
	/**
	 * The change in the vertical speed of the opponent when it is hit by this
	 * motion.
	 */
	private int attackImpactY;

	/**
	 * The number of frames that the guarding opponent needs to resume to its
	 * normal status when it is hit by this motion.
	 */
	private int attackGiveGuardRecov;

	/**
	 * The value of attack type.<br>
	 * 1: high<br>
	 * 2: middle<br>
	 * 3: low<br>
	 * 4: throw
	 */
	private int attackType;

	/**
	 * The flag whether this motion can push down the opponent when it is hit by
	 * this motion.
	 */
	private boolean attackDownProp;

	/**
	 * The value of the first frame that the character can cancel this motion.
	 * <br>
	 * If this motion has reached this timing, it can be canceled with a motion
	 * having a lower value of motionLevel; if this motion has no cancelable
	 * period, the returned value will be -1.
	 */
	private int cancelAbleFrame;

	/**
	 * The value of the level that can cancel this motion; during cancelable
	 * frames, any motion whose level is below this value can cancel this
	 * motion.
	 */
	private int cancelAbleMotionLevel;

	/**
	 * The value of this motion's level.
	 *
	 * @see #cancelAbleMotionLevel
	 * @see #cancelAbleFrame
	 */
	private int motionLevel;

	/**
	 * The flag whether this character can run a motion with the motion's
	 * command.
	 */
	private boolean control;

	/**
	 * The flag whether a landing motion can cancel this motion.
	 */
	private boolean landingFlag;

	/**
	 * The list of data structure for image.
	 */
	private ArrayList<Image> imageList;

	/**
	 * 指定されたデータでMotionクラスのインスタンスを生成するクラスコンストラクタ．
	 *
	 * @param data
	 *            Motion.csvから読み込んだキャラクターのパラメータ
	 * @param characterName
	 *            キャラクターの名前
	 * @param playerIndex
	 *            プレイヤー番号(0:P1 1:P2)
	 */
	public Motion(String[] data, String characterName, int playerIndex) {
		this.actionName = data[0];
		this.frameNumber = Integer.valueOf(data[1]);
		this.speedX = Integer.valueOf(data[2]);
		this.speedY = Integer.valueOf(data[3]);
		this.characterHitArea = new HitArea(Integer.valueOf(data[4]), Integer.valueOf(data[5]),
				Integer.valueOf(data[6]), Integer.valueOf(data[7]));
		this.state = State.valueOf(data[8]);
		this.attackHitArea = new HitArea(Integer.valueOf(data[9]), Integer.valueOf(data[10]), Integer.valueOf(data[11]),
				Integer.valueOf(data[12]));
		this.attackSpeedX = Integer.valueOf(data[13]);
		this.attackSpeedY = Integer.valueOf(data[14]);
		this.attackStartUp = Integer.valueOf(data[15]);
		this.attackActive = Integer.valueOf(data[16]);
		this.attackHitDamage = Integer.valueOf(data[17]);
		this.attackGuardDamage = Integer.valueOf(data[18]);
		this.attackStartAddEnergy = Integer.valueOf(data[19]);
		this.attackHitAddEnergy = Integer.valueOf(data[20]);
		this.attackGuardAddEnergy = Integer.valueOf(data[21]);
		this.attackGiveEnergy = Integer.valueOf(data[22]);
		this.attackImpactX = Integer.valueOf(data[23]);
		this.attackImpactY = Integer.valueOf(data[24]);
		this.attackGiveGuardRecov = Integer.valueOf(data[25]);
		this.attackType = Integer.valueOf(data[26]);
		this.attackDownProp = Boolean.valueOf(data[27]);
		this.cancelAbleFrame = Integer.valueOf(data[28]);
		this.cancelAbleMotionLevel = Integer.valueOf(data[29]);
		this.motionLevel = Integer.valueOf(data[30]);
		this.control = Boolean.valueOf(data[31]);
		this.landingFlag = Boolean.valueOf(data[32]);
		
		if (LaunchSetting.isExpectedProcessingMode(LaunchSetting.HEADLESS_MODE)) {
			setMotionImage(characterName, playerIndex);
		}
	}

	/**
	 * 指定されたデータでMotionのインスタンスを作成するクラスコンストラクタ.<br>
	 * シミュレータ内でのみ呼び出される.
	 *
	 * @param motionData
	 *            モーションデータ
	 * @see MotionData
	 */
	public Motion(MotionData motionData) {
		this.actionName = motionData.getActionName();
		this.frameNumber = motionData.getFrameNumber();
		this.speedX = motionData.getSpeedX();
		this.speedY = motionData.getSpeedY();
		this.characterHitArea = motionData.getCharacterHitArea();
		this.state = motionData.getState();
		this.attackHitArea = motionData.getAttackHitArea();
		this.attackSpeedX = motionData.getAttackSpeedX();
		this.attackSpeedY = motionData.getAttackSpeedY();
		this.attackStartUp = motionData.getAttackStartUp();
		this.attackActive = motionData.getAttackActive();
		this.attackHitDamage = motionData.getAttackHitDamage();
		this.attackGuardDamage = motionData.getAttackGuardDamage();
		this.attackStartAddEnergy = motionData.getAttackStartAddEnergy();
		this.attackHitAddEnergy = motionData.getAttackHitAddEnergy();
		this.attackGuardAddEnergy = motionData.getAttackGuardAddEnergy();
		this.attackGiveEnergy = motionData.getAttackGiveEnergy();
		this.attackImpactX = motionData.getAttackImpactX();
		this.attackImpactY = motionData.getAttackImpactY();
		this.attackGiveGuardRecov = motionData.getAttackGiveGuardRecov();
		this.attackType = motionData.getAttackType();
		this.attackDownProp = motionData.isAttackDownProp();
		this.cancelAbleFrame = motionData.getCancelAbleFrame();
		this.cancelAbleMotionLevel = motionData.getCancelAbleMotionLevel();
		this.motionLevel = motionData.getMotionLevel();
		this.control = motionData.isControl();
		this.landingFlag = motionData.isLandingFlag();

		// 画像は読み込まない
	}

	/**
	 * キャラクターの各モーションに対応するキャラクター画像を設定する．
	 *
	 * @param characterName
	 *            キャラクターの名前
	 * @param playerIndex
	 *            プレイヤー番号(0:P1 1:P2)
	 */
	private void setMotionImage(String characterName, int playerIndex) {
		this.imageList = new ArrayList<Image>();
		ArrayList<CharacterActionImage> temp = GraphicManager.getInstance().getCharacterImageContainer();
		int index = temp.indexOf(new CharacterActionImage(characterName, this.actionName));

		if (index == -1) {
			Logger.getAnonymousLogger().log(Level.WARNING, "There is no character graphic according to the action");

		} else {
			// アクション名に対応する画像を読み込む
			Image[] image = temp.get(index).getActionImage();

			for (Image img : image) {
				if (LaunchSetting.characterNames[0].equals(LaunchSetting.characterNames[1])) {
					// 画素の反転
					if (LaunchSetting.invertedPlayer == playerIndex + 1) {
						Logger.getAnonymousLogger().log(Level.INFO,
								"Inverting all character images of P" + playerIndex + 1);
						img = invert(img);
					}
				}
				this.imageList.add(img);
			}
		}
	}

	/**
	 * 画像の画素情報を反転させる．
	 *
	 * @param image
	 *            入力画像
	 * @return 反転された画像
	 * @see Image
	 */
	private Image invert(Image image) {
		BufferedImage temp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		//temp.setData(image.getBufferedImage().getData());
		//BufferedImage invertedImage = image.getBufferedImage();

		float[][] matrix = new float[][] {
			new float[] { -1.0f, 0.0f, 0.0f, 0.0f, 255.0f },
			new float[] { 0.0f, -1.0f, 0.0f, 0.0f, 255.0f },
			new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 255.0f },
			new float[] { 0.0f, 0.0f, 0.0f, 1.0f, 0.0f } };

		BandCombineOp invert = new BandCombineOp(matrix, new RenderingHints(null));
		invert.filter(image.getBufferedImage().getRaster(), temp.getRaster());

		return new Image(image.getTextureId(),temp);
	}

	/**
	 * Returns the name of this motion.
	 *
	 * @return the name of this motion
	 */
	public String getActionName() {
		return this.actionName;
	}

	/**
	 * Returns the number of frames in this motion.
	 *
	 * @return the number of frames in this motion
	 */
	public int getFrameNumber() {
		return this.frameNumber;
	}

	/**
	 * Returns the horizontal speed applied to a character using this motion.
	 *
	 * @return the horizontal speed applied to a character using this motion
	 */
	public int getSpeedX() {
		return this.speedX;
	}

	/**
	 * Returns the vertical speed applied to a character using this motion.
	 *
	 * @return the vertical speed applied to a character using this motion
	 */
	public int getSpeedY() {
		return this.speedY;
	}

	/**
	 * Returns the hit area associated to this motion.
	 *
	 * @return the attack hit area associated to this motion
	 */
	public HitArea getAttackHitArea() {
		return this.attackHitArea;
	}

	/**
	 * Returns the hit area associated to this character.
	 *
	 * @return the character hit area associated to this motion
	 */
	public HitArea getCharacterHitArea() {
		return this.characterHitArea;
	}

	/**
	 * Returns the state that is assigned to a character after this motion is
	 * completed.
	 *
	 * @return the state that is assigned to a character after this motion is
	 *         completed
	 */
	public State getState() {
		return this.state;
	}

	/**
	 * Returns the horizontal speed of the attack hit box.
	 *
	 * @return the horizontal speed of the attack hit box
	 */
	public int getAttackSpeedX() {
		return attackSpeedX;
	}

	/**
	 * Returns the vertical speed of the attack hit box.
	 *
	 * @return the vertical speed of the attack hit box
	 */
	public int getAttackSpeedY() {
		return attackSpeedY;
	}

	/**
	 * Returns the number of startup frames required for this motion to become
	 * active
	 *
	 * @return the number of startup frames required for this motion to become
	 *         active
	 */
	public int getAttackStartUp() {
		return attackStartUp;
	}

	/**
	 * Returns the number of active frames.
	 *
	 * @return the number of active frames
	 */
	public int getAttackActive() {
		return attackActive;
	}

	/**
	 * Returns the value of the damage to the unguarding opponent when it is hit
	 * by this motion.
	 *
	 * @return the value of the damage to the unguarding opponent when it is hit
	 *         by this motion
	 */
	public int getAttackHitDamage() {
		return attackHitDamage;
	}

	/**
	 * Returns the value of the damage to the guarding opponent when it is hit
	 * by this motion.
	 *
	 * @return the value of the damage to the guarding opponent when it is hit
	 *         by this motion
	 */
	public int getAttackGuardDamage() {
		return attackGuardDamage;
	}

	/**
	 * Returns the controllable state associated to this motion. If the flag is
	 * false, a character using this motion cannot be controlled until this
	 * motion finishes.
	 *
	 * @return {@code true} if a character using this motion is controllable,
	 *         {@code false} otherwise
	 */
	public boolean isControl() {
		return this.control;
	}

	/**
	 * Returns the value of the energy added to the character when it uses this
	 * motion.
	 *
	 * @return the value of the energy added to the character when it uses this
	 *         motion
	 */
	public int getAttackStartAddEnergy() {
		return attackStartAddEnergy;
	}

	/**
	 * Returns the first frame in which this motion can be canceled. Notice that
	 * only a motion whose {@link #motionLevel} is lower than the
	 * {@link #cancelAbleMotionLevel} of this one can cancel this motion.
	 *
	 * @return the first frame in which this motion can be canceled
	 */
	public int getCancelAbleFrame() {
		return this.cancelAbleFrame;
	}

	/**
	 * Returns the value of the energy added to the character when this motion
	 * hits the opponent.
	 *
	 * @return the value of the energy added to the character when this motion
	 *         hits the opponent
	 */
	public int getAttackHitAddEnergy() {
		return attackHitAddEnergy;
	}

	/**
	 * Returns the value of the energy added to the character when this motion
	 * is guarded by the guarding opponent.
	 *
	 * @return the value of the energy added to the character when this motion
	 *         is guarded by the guarding opponent
	 */
	public int getAttackGuardAddEnergy() {
		return attackGuardAddEnergy;
	}

	/**
	 * Returns the value of the energy given to the opponent when it is hit by
	 * this motion.
	 *
	 * @return the value of the energy given to the opponent when it is hit by
	 *         this motion
	 */
	public int getAttackGiveEnergy() {
		return attackGiveEnergy;
	}

	/**
	 * Returns the change in the horizontal speed of the opponent when it is hit
	 * by this motion.
	 *
	 * @return the change in the horizontal speed of the opponent when it is hit
	 *         by this motion
	 */
	public int getAttackImpactX() {
		return attackImpactX;
	}

	/**
	 * Returns the change in the vertical speed of the opponent when it is hit
	 * by this motion.
	 *
	 * @return the change in the vertical speed of the opponent when it is hit
	 *         by this motion
	 */
	public int getAttackImpactY() {
		return attackImpactY;
	}

	/**
	 * Returns the number of frames that the guarding opponent needs to resume
	 * to its normal status when it is hit by this motion.
	 *
	 * @return the number of frames that the guarding opponent needs to resume
	 *         to its normal status when it is hit by this motion
	 */
	public int getAttackGiveGuardRecov() {
		return attackGiveGuardRecov;
	}

	/**
	 * Returns the value of the attack type:<br>
	 * 1 = high,<br>
	 * 2 = middle,<br>
	 * 3 = low,<br>
	 * 4 = throw,<br>
	 *
	 * @return the value of the attack type
	 */
	public int getAttackType() {
		return attackType;
	}

	/**
	 * Returns the flag whether this motion can push down the opponent when hit.
	 *
	 * @return the flag whether this motion can push down the opponent when hit
	 */
	public boolean isAttackDownProp() {
		return attackDownProp;
	}

	/**
	 * Returns the maximum {@link #motionLevel} a motion can have in order to be
	 * able to cancel this motion.
	 *
	 * @return the maximum {@link #motionLevel} a motion can have in order to be
	 *         able to cancel this motion.
	 */
	public int getCancelAbleMotionLevel() {
		return this.cancelAbleMotionLevel;
	}

	/**
	 * Returns the level of this motion.<br>
	 * Notice that this motion can cancel other motions (after their
	 * {@link #cancelAbleFrame}) if such motions have a
	 * {@link #cancelAbleMotionLevel} higher than the level of this motion.
	 *
	 * @return the level of this motion
	 */
	public int getMotionLevel() {
		return this.motionLevel;
	}

	/**
	 * Returns {@code true} if the Landing motion can cancel this motion,
	 * {@code false} otherwise.
	 *
	 * @return the boolean value that expresses whether or not character lands
	 *         in this motion.
	 */
	public boolean isLandingFlag() {
		return this.landingFlag;
	}

	/**
	 * Returns the current image of the character.
	 *
	 * @param nowFrame
	 *            the current frame
	 * @return the current image of the character
	 */
	public Image getImage(int nowFrame) {
		return imageList.get((frameNumber - nowFrame) % frameNumber);
	}

	/**
	 * Sets the name of this motion.
	 *
	 * @param motionName
	 *            the motion name to be set
	 */
	public void setMotionName(String motionName) {
		this.actionName = motionName;
	}

	/**
	 * Set the number of frames in this motion.
	 *
	 * @param frameNumber
	 *            the number of frames to be set
	 */
	public void setFrameNumber(int frameNumber) {
		this.frameNumber = frameNumber;
	}

	/**
	 * Sets the {@link #speedX} setting, which stores the horizontal speed
	 * applied to a character using this motion.
	 *
	 * @param speedX
	 *            the value to assign to the {@link #speedX} setting
	 */
	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	/**
	 * Sets the {@link #speedY} setting, which stores the vertical speed applied
	 * to a character using this motion.
	 *
	 * @param speedY
	 *            the value to assign to the {@link #speedY} setting
	 */
	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}

	/**
	 * Sets the hit area of this attack.
	 *
	 * @param attackHitArea
	 *            the hit area of this attack
	 */
	public void setAttackHitArea(HitArea attackHitArea) {
		this.attackHitArea = attackHitArea;
	}

	/**
	 * Sets the character hit area of this motion.
	 *
	 * @param characterHitArea
	 *            the character hit area of this motion
	 */
	public void setCharacterHitArea(HitArea characterHitArea) {
		this.characterHitArea = characterHitArea;
	}

	/**
	 * Sets the horizontal speed of the attack hit box.
	 *
	 * @param attackSpeedX
	 *            the horizontal speed of the attack hit box
	 */
	public void setAttackSpeedX(int attackSpeedX) {
		this.attackSpeedX = attackSpeedX;
	}

	/**
	 * Sets the vertical speed of the attack hit box.
	 *
	 * @param attackSpeedY
	 *            the vertical speed of the attack hit box
	 */
	public void setAttackSpeedY(int attackSpeedY) {
		this.attackSpeedY = attackSpeedY;
	}

	/**
	 * Sets the number of startup frames required for this motion to become
	 * active
	 *
	 * @param attackStartUp
	 *            the number of startup frames required for this motion to
	 *            become active
	 */
	public void setAttackStartUp(int attackStartUp) {
		this.attackStartUp = attackStartUp;
	}

	/**
	 * Sets the number of active frames.
	 *
	 * @param attackActive
	 *            the number of active frames
	 */
	public void setAttackActive(int attackActive) {
		this.attackActive = attackActive;
	}

	/**
	 * Sets the value of the damage to the unguarding opponent when it is hit by
	 * this motion.
	 *
	 * @param attackHitDamage
	 *            the value of the damage to the unguarding opponent when it is
	 *            hit by this motion
	 */
	public void setAttackHitDamage(int attackHitDamage) {
		this.attackHitDamage = attackHitDamage;
	}

	/**
	 * Sets the value of the damage to the guarding opponent when it is hit by
	 * this motion.
	 *
	 * @param attackGuardDamage
	 *            the value of the damage to the guarding opponent when it is
	 *            hit by this motion
	 */
	public void setAttackGuardDamage(int attackGuardDamage) {
		this.attackGuardDamage = attackGuardDamage;
	}

	/**
	 * Sets the value of the energy added to the character when it uses this
	 * motion
	 *
	 * @param attackStartAddEnergy
	 *            the value of the energy added to the character when it uses
	 *            this motion.
	 */
	public void setAttackStartAddEnergy(int attackStartAddEnergy) {
		this.attackStartAddEnergy = attackStartAddEnergy;
	}

	/**
	 * Sets the value of the energy added to the character when this motion hits
	 * the opponent.
	 *
	 * @param attackHitAddEnergy
	 *            the value of the energy added to the character when this
	 *            motion hits the opponent
	 */
	public void setAttackHitAddEnergy(int attackHitAddEnergy) {
		this.attackHitAddEnergy = attackHitAddEnergy;
	}

	/**
	 * Sets the value of the energy added to the character when this motion is
	 * guarded by the guarding opponent.
	 *
	 * @param attackGuardAddEnergy
	 *            the value of the energy added to the character when this
	 *            motion is guarded by the guarding opponent
	 */
	public void setAttackGuardAddEnergy(int attackGuardAddEnergy) {
		this.attackGuardAddEnergy = attackGuardAddEnergy;
	}

	/**
	 * Sets the value of the energy given to the opponent when it is hit by this
	 * motion.
	 *
	 * @param attackGiveEnergy
	 *            the value of the energy given to the opponent when it is hit
	 *            by this motion
	 */
	public void setAttackGiveEnergy(int attackGiveEnergy) {
		this.attackGiveEnergy = attackGiveEnergy;
	}

	/**
	 * Sets the change in the horizontal speed of the opponent when it is hit by
	 * this motion.
	 *
	 * @param attackImpactX
	 *            the change in the horizontal speed of the opponent when it is
	 *            hit by this motion
	 */
	public void setAttackImpactX(int attackImpactX) {
		this.attackImpactX = attackImpactX;
	}

	/**
	 * Sets the change in the vertical speed of the opponent when it is hit by
	 * this motion
	 *
	 * @param attackImpactY
	 *            the change in the vertical speed of the opponent when it is
	 *            hit by this motion
	 */
	public void setAttackImpactY(int attackImpactY) {
		this.attackImpactY = attackImpactY;
	}

	/**
	 * Sets the number of frames that the guarding opponent needs to resume to
	 * its normal status after being hit by this motion.
	 *
	 * @param attackGiveGuardRecov
	 *            the number of frames that the guarding opponent needs to
	 *            resume to its normal status after being hit by this motion
	 */
	public void setAttackGiveGuardRecov(int attackGiveGuardRecov) {
		this.attackGiveGuardRecov = attackGiveGuardRecov;
	}

	/**
	 * Sets the value of the attack type:<br>
	 * 1 = high,<br>
	 * 2 = middle,<br>
	 * 3 = low,<br>
	 * 4 = throw,<br>
	 *
	 * @param attackType
	 *            the value of the attack type
	 */
	public void setAttackType(int attackType) {
		this.attackType = attackType;
	}

	/**
	 * Sets the {@link #control} flag. If the flag is false, a character using
	 * this motion cannot be controlled until this motion finishes.
	 *
	 * @param control
	 *            the boolean value to give to the flag
	 */
	public void setControl(boolean control) {
		this.control = control;
	}

	/**
	 * Sets the flag whether this motion can push down the opponent when it is
	 * hit by this motion.
	 *
	 * @param attackDownProp
	 *            the flag whether this motion can push down the opponent when
	 *            it is hit by this motion
	 */
	public void setAttackDownProp(boolean attackDownProp) {
		this.attackDownProp = attackDownProp;
	}

	/**
	 * Sets the {@link #cancelAbleFrame} setting. This indicates the first frame
	 * when this motion can be cancelled.
	 *
	 * @param cancelAbleFrame
	 *            the value to assign to the {@link #cancelAbleFrame} setting
	 */
	public void setCancelAbleFrame(int cancelAbleFrame) {
		this.cancelAbleFrame = cancelAbleFrame;
	}

	/**
	 * Sets the {@link #cancelAbleMotionLevel} setting. This indicates the
	 * maximum level that a motion can have to be able to cancel this motion.
	 * During cancelable frames, a motion whose level is below this value can
	 * cancel the current motion.
	 *
	 * @param cancelAbleMotionLevel
	 *            the value to assign to the {@link #cancelAbleMotionLevel}
	 *            setting
	 */
	public void setCancelAbleMotionLevel(int cancelAbleMotionLevel) {
		this.cancelAbleMotionLevel = cancelAbleMotionLevel;
	}

	/**
	 * Sets the {@link #motionLevel} of this motion.
	 *
	 * @param motionLevel
	 *            the value to assign to the {@link #motionLevel} setting
	 */
	public void setMotionLevel(int motionLevel) {
		this.motionLevel = motionLevel;
	}

	/**
	 * Sets the {@link #state} setting, which represents the state assigned to
	 * the character at the end of this motion.
	 *
	 * @param state
	 *            the value to assign to the {@link #state} setting
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Sets the {@link #landingFlag} setting, a flag encoding whether the
	 * Landing motion can cancel this motion.
	 *
	 * @param landingFlag
	 *            the value to assign to the {@link #landingFlag} setting
	 */
	public void setLandingFlag(boolean landingFlag) {
		this.landingFlag = landingFlag;
	}
}
