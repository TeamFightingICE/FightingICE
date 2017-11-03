package image;

public class CharacterActionImage {

	private String characterName;

	private String actionName;

	private int frameNumber;

	private Image[] actionImage;


	public CharacterActionImage(String characterName, String actionName, int frameNumber, Image[] actionImage){
		this.characterName = characterName;
		this.actionName = actionName;
		this.frameNumber = frameNumber;
		this.actionImage = actionImage;
	}


	public String getCharacterName(){
		return this.characterName;
	}

	public String getActionName(){
		return this.actionName;
	}

	public int getFrameNumber(){
		return this.frameNumber;
	}

	public Image[] getActionImage(){
		return this.actionImage;
	}

}
