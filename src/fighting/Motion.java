package fighting;

import java.util.ArrayList;

import enumerate.State;
import image.CharacterActionImage;
import image.Image;
import manager.GraphicManager;
import struct.HitArea;

public class Motion {

	/**
	 * This motion's name
	 */
	public String actionName;

	/**
	 * The number of frames in this motion
	 */
	public int frameNumber;

	/**
	 * The speed value in the horizontal direction that will be applied to the
	 * character when it does this motion
	 */
	public int speedX;

	/**
	 * The speed value in the vertical direction that will be applied to the
	 * character when it does this motion
	 */
	public int speedY;

	/**
	 * The information on the hit box (boundary box in other games)
	 */
	public HitArea characterHitArea;

	/**
	 * The resulting state after running this motion
	 */
	public State state;

	/**
	 * The information on the attack hit box
	 */
	public HitArea attackHitArea;

	/**
	 * The horizontal speed of the attack hit box
	 */
	public int attackSpeedX;

	/**
	 * The vertical speed of the attack hit box
	 */
	public int attackSpeedY;

	/**
	 * The number of startup frames
	 */
	public int attackStartUp;

	/**
	 * The number of active frames
	 */
	public int attackActive;

	/**
	 * Not in use!
	 */
	public int attackInterval;

	/**
	 * Not in use!
	 */
	public int attackRepeat;

	/**
	 * The value of the damage to the unguarding opponent when it is hit by this
	 * skill
	 */
	public int attackHitDamage;

	/**
	 * The value of the damage to the guarding opponent when it is hit by this
	 * skill
	 */
	public int attackGuardDamage;

	/**
	 * The value of the energy added to the character when it uses this skill
	 */
	public int attackStartAddEnergy;

	/**
	 * The value of the energy added to the character when this skill hits the
	 * opponent
	 */
	public int attackHitAddEnergy;

	/**
	 * The value of the energy added to the character when this skill is guarded
	 * by the guarding opponent.
	 */
	public int attackGuardAddEnergy;

	/**
	 * The value of the energy given to the opponent when it is hit by this
	 * skill
	 */
	public int attackGiveEnergy;

	/**
	 * The change in the horizontal speed of the opponent when it is hit by this
	 * skill
	 */
	public int attackImpactX;
	/**
	 * The change in the vertical speed of the opponent when it is hit by this
	 * skill
	 */
	public int attackImpactY;

	/**
	 * The number of frames that the guarding opponent needs to resume to its
	 * normal status when it is hit by this skill
	 */
	public int attackGiveGuardRecov;

	/**
	 * The value of attack type: 1 = high, 2 = middle, 3 = low, 4 = throw,
	 */
	public int attackType;

	/**
	 * The flag whether this skill can push down the opponent when it is hit by
	 * this skill
	 */
	public boolean attackDownProperty;

	/**
	 * The value of the first frame that the character can cancel this motion.
	 * <br>
	 * If this motion has reached this timing, it can be canceled with a motion
	 * having a lower value of motionLevel; if this motion has no cancelable
	 * period, the returned value will be -1.
	 */
	public int cancelAbleFrame;

	/**
	 * The value of the level that can cancel this motion; during cancelable
	 * frames, any motion whose level is below this value can cancel this motion
	 */
	public int cancelAbleMotionLevel;

	/**
	 * The value of this motion's level
	 *
	 * @see #cancelAbleMotionLevel
	 * @see #cancelAbleFrame
	 */
	public int motionLevel;

	/**
	 * The flag whether this character can run a motion with the motion's
	 * command
	 */
	public boolean control;

	/**
	 * The flag whether a landing motion can cancel this motion
	 */
	public boolean landingFlag;

	/**
	 * This motion's image file name
	 */
	public String imageName;

	/**
	 * This is a Vector of data structure for image .
	 */
	private ArrayList<Image> imageList;

	public Motion(String[] data, String characterName) {
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
		this.attackInterval = Integer.valueOf(data[16]);
		this.attackRepeat = Integer.valueOf(data[17]);
		this.attackActive = Integer.valueOf(data[18]);
		this.attackHitDamage = Integer.valueOf(data[19]);
		this.attackGuardDamage = Integer.valueOf(data[20]);
		this.attackStartAddEnergy = Integer.valueOf(data[21]);
		this.attackHitAddEnergy = Integer.valueOf(data[22]);
		this.attackGuardAddEnergy = Integer.valueOf(data[23]);
		this.attackGiveEnergy = Integer.valueOf(data[24]);
		this.attackImpactX = Integer.valueOf(data[25]);
		this.attackImpactY = Integer.valueOf(data[26]);
		this.attackGiveGuardRecov = Integer.valueOf(data[27]);
		this.attackType = Integer.valueOf(data[28]);
		this.attackDownProperty = Boolean.valueOf(data[29]);
		this.cancelAbleFrame = Integer.valueOf(data[30]);
		this.cancelAbleMotionLevel = Integer.valueOf(data[31]);
		this.motionLevel = Integer.valueOf(data[32]);
		this.control = Boolean.valueOf(data[33]);
		this.landingFlag = Boolean.valueOf(data[34]);

		setMotionImage(characterName);
	}

	private void setMotionImage(String characterName) {
		this.imageList = new ArrayList<Image>();
		ArrayList<CharacterActionImage> temp = GraphicManager.getInstance().getCharacterImageContainer();
		int index = temp.indexOf(new CharacterActionImage(characterName, this.actionName));

		if (index == -1) {
			System.out.println("対応する画像がありません");

		} else {
			// アクション名に対応する画像を読み込む
			// ここでinvertedPlayerによって画素を変更する？
			Image[] image = temp.get(index).getActionImage();

			for (Image img : image) {
				this.imageList.add(img);
			}

		}

	}

}
