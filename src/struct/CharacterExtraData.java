package struct;

import java.util.List;

import fighting.Character;
import fighting.Motion;

public class CharacterExtraData {
	
	private HitArea preprocessedHitArea;
	private MotionData motionData;
	private AttackData[] projectileAttack;
	private boolean[] projectileLive;
	private boolean[] projectileHit;
	
	private void initialize() {
		this.projectileAttack = new AttackData[3];
		this.projectileLive = new boolean[3];
		this.projectileHit = new boolean[3];
	}
	
	/**
	 * The class constructor.
	 */
	public CharacterExtraData() {
		initialize();
	}
	
	public CharacterExtraData(Character character) {
		initialize();
		
		this.preprocessedHitArea = character.getPreprocessedHitArea();

		List<Motion> motionList = character.getMotionList();
		int actionOrdinal = character.getAction().ordinal();
		this.motionData = new MotionData(motionList.get(actionOrdinal));
		
		for (int i = 0; i < 3; i++) {
			if (character.getProjectileAttack(i) != null) {
				this.projectileAttack[i] = new AttackData(character.getProjectileAttack(i));
				this.projectileLive[i] = character.getProjectileLive(i);
				this.projectileHit[i] = character.getProjectileHit(i);
			}
		}
	}
	
	public CharacterExtraData(CharacterExtraData extraData) {
		initialize();
		
		this.preprocessedHitArea = new HitArea(extraData.getPreprocessedHitArea());
		this.motionData = new MotionData(extraData.getMotionData());
		
		for (int i = 0; i < 3; i++) {
			this.projectileAttack[i] = new AttackData(extraData.getProjectileAttack(i));
			this.projectileLive[i] = extraData.getProjectileLive(i);
			this.projectileHit[i] = extraData.getProjectileHit(i);
		}
	}
	
	public HitArea getPreprocessedHitArea() {
		return this.preprocessedHitArea;
	}
	
	public MotionData getMotionData() {
		return this.motionData;
	}
	
	public AttackData getProjectileAttack(int index) {
		return this.projectileAttack[index];
	}
	
	public boolean getProjectileLive(int index) {
		return this.projectileLive[index];
	}
	
	public boolean getProjectileHit(int index) {
		return this.projectileHit[index];
	}
	
	public void toProto() {
		// TODO
	}
	
}
