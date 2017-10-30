package image;

import enumerate.Action;

public class CharacterActionImage {

	private String characterName;

	private Action action;

	private int frameNumber;

	private Image[] actionImage;


	public CharacterActionImage(String characterName, Action action, int frameNumber, Image[] actionImage){
		this.characterName = characterName;
		this.action = action;
		this.frameNumber = frameNumber;
		this.actionImage = actionImage;
	}


	public String getCharacterName(){
		return this.characterName;
	}

	public Action getAction(){
		return this.action;
	}

	public int getFrameNumber(){
		return this.frameNumber;
	}

	public Image[] getActionImage(){
		return this.actionImage;
	}

}
