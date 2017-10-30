package manager;

public class SoundManager {

	private static  SoundManager  soundManager = new  SoundManager();

	private  SoundManager() {
		System.out.println("Create instance: " + SoundManager.class.getName());
	}

	public static  SoundManager getInstance() {
		return  soundManager;
	}

}
