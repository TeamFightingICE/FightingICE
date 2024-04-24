package struct;

import java.util.ArrayList;
import java.util.List;

import fighting.Character;
import fighting.Motion;

public class CharacterExtraData {
	
	private HitArea preprocessedHitArea;
	private MotionData motionData;
	private List<AttackData> projectileAttack;
	private List<Boolean> isProjectileLive;
	
	/**
	 * The class constructor.
	 */
	public CharacterExtraData() {
		
	}
	
	public CharacterExtraData(Character character) {
		this.preprocessedHitArea = character.getPreprocessedHitArea();

		List<Motion> motionList = character.getMotionList();
		int actionOrdinal = character.getAction().ordinal();
		this.motionData = new MotionData(motionList.get(actionOrdinal));
		
		this.projectileAttack = new ArrayList<>();
		this.isProjectileLive = new ArrayList<>();
		for (int i = 0; i < character.getProjectileAttack().length; i++) {
			if (character.getProjectileAttack()[i] != null) {
				this.projectileAttack.add(new AttackData(character.getProjectileAttack()[i]));
				this.isProjectileLive.add(character.getIsProjectileLive()[i]);
			} else {
				this.projectileAttack.add(null);
				this.isProjectileLive.add(false);
			}
		}
	}
	
	public HitArea getPreprocessedHitArea() {
		return this.preprocessedHitArea;
	}
	
	public MotionData getMotionData() {
		return this.motionData;
	}
	
	public List<AttackData> getProjectileAttack() {
		return this.projectileAttack;
	}
	
	public List<Boolean> getIsProjectileLive() {
		return this.isProjectileLive;
	}
	
	public void toProto() {
		// TODO
	}
	
}
