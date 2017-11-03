package gamescene;

import java.io.File;

import enumerate.GameSceneName;
import manager.GraphicManager;
import manager.InputManager;
import struct.Key;

public class FightingMenu extends GameScene {

	// 表示する項目数
	private final int NUMBER_OF_ITEM = 7;
	MenuItem[] mi = new MenuItem[NUMBER_OF_ITEM];
	private static final int[] NUMBERS = {1,2,3,5,10,30,50,100};
	private static final String[] CHARACTERS = {"ZEN","GARNET","LUD"};
	private String[] aiName;

	// 現在のカーソル位置
	private int cursorPosition;
	private int numberIndex;
	private int[] playerIndex = new int[2];
	private int[] charcterIndex = new int[2];


	private final String AI_PATH = "./data/ai/";

	public FightingMenu(){
		//以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.MENU;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////
	}

	@Override
	public void initialize() {
		System.out.println("Menu initialize");
		// Initialization

		mi[0] = new MenuItem("PLAY : ", 50, 50, 0);
		mi[1] = new MenuItem("PLAYER1 : ", 75, 90, 1);
		mi[2] = new MenuItem("PLAYER2 : ", 75, 130, 2);
		mi[3] = new MenuItem("CHARACTER1 : ", 75, 170, 3);
		mi[4] = new MenuItem("CHARACTER2 : ", 75, 210, 4);
		mi[5] = new MenuItem("Repeat Count : ", 50, 260, 5);
		mi[6] = new MenuItem("RETURN : ", 50, 310, 6);

		cursorPosition = 0;
		playerIndex[0] = 0;
		playerIndex[1] = 0;
		charcterIndex[0] = 0;
		charcterIndex[1] = 0;
		numberIndex = 0;

		// get file list from the folder
		String path = AI_PATH;
		File dir = new File(path);
		File[] files = dir.listFiles();

		files = dir.listFiles();
		int nFiles = files != null ? files.length : 0;
		aiName = new String[nFiles];
		// get replay's full path
		for(int i = 0 ; i < nFiles ; i++){
			String buffer;
			char[] charBuffer;
			char[] fileFullPath;
			File file = files[i];
			buffer = file.toString();
			// replay record is stored as dat file
			if(buffer.endsWith(".dat")){
				int pathLength = path.length();
				buffer = files[i].toString();
				charBuffer = buffer.toCharArray();
				fileFullPath = new char[buffer.length()-path.length()-4];
				for(int j = 0; j < (buffer.length() - path.length() - 4);j++){
					fileFullPath[j] = charBuffer[pathLength];
					pathLength++;
				}
			 	aiName[i] = String.valueOf(fileFullPath);
			}
		}
		if(nFiles == 0){
			aiName = new String[1];
			aiName[0] = "None";
		}
	}

	@Override
	public void update() {
		Key key = InputManager.getInstance().getKeyData().getKeys()[0];

		if(key.U==true){
			if(cursorPosition==0){
				cursorPosition = mi[NUMBER_OF_ITEM-1].getCursorPosition();
			}
			else{
				cursorPosition = mi[cursorPosition-1].getCursorPosition();
			}

		}

		if(key.D==true){
			if(cursorPosition==NUMBER_OF_ITEM-1){
				cursorPosition = mi[0].getCursorPosition();
			}
			else{
				cursorPosition = mi[cursorPosition+1].getCursorPosition();
			}
		}

		switch(cursorPosition){
		case 0:
			if(key.A==true){
				// Playの呼び出し処理
			}
			break;

		case 1:
			// p1の操作方法
			break;

		case 2:
			// p2の操作方法
			break;

		case 3:
			if(key.R==true){
				if(charcterIndex[0] == CHARACTERS.length - 1) charcterIndex[0] = 0;
				else charcterIndex[0]++;
			}
			if(key.L==true){
				if(charcterIndex[0] == 0) charcterIndex[0] = CHARACTERS.length - 1;
				else charcterIndex[0]--;
			}
			break;

		case 4:
			if(key.R==true){
				if(charcterIndex[1] == CHARACTERS.length - 1) charcterIndex[1] = 0;
				else charcterIndex[1]++;
			}
			if(key.L==true){
				if(charcterIndex[1] == 0) charcterIndex[1] = CHARACTERS.length - 1;
				else charcterIndex[1]--;
			}
			break;

		case 5:
			if(key.R==true){
				if(numberIndex == NUMBERS.length - 1) numberIndex = 0;
				else numberIndex++;
			}
			if(key.L==true){
				if(numberIndex == 0) numberIndex = NUMBERS.length - 1;
				else numberIndex--;
			}
			break;

		case 6:
			if(key.A==true){
				HomeMenu homeMenu = new HomeMenu();  //次のシーンのコンストラクタ作成
				this.setTransitioFlag(true);    //現在のシーンからの遷移要求をtrueに
				this.setNextGameScene(homeMenu);       //次のシーンをセットする
			}
			break;

		default:
			break;
		}

		this.drawScreen();

	}

	public void drawScreen() {
		GraphicManager.getInstance().drawString(mi[0].getString(), mi[0].getCoordinateX(), mi[0].getCoordinateY());
		GraphicManager.getInstance().drawString(mi[1].getString(), mi[1].getCoordinateX(), mi[1].getCoordinateY());
		GraphicManager.getInstance().drawString(mi[2].getString(), mi[2].getCoordinateX(), mi[2].getCoordinateY());
		GraphicManager.getInstance().drawString(mi[3].getString()+CHARACTERS[charcterIndex[0]], mi[3].getCoordinateX(), mi[3].getCoordinateY());
		GraphicManager.getInstance().drawString(mi[4].getString()+CHARACTERS[charcterIndex[1]], mi[4].getCoordinateX(), mi[4].getCoordinateY());
		GraphicManager.getInstance().drawString(mi[5].getString()+NUMBERS[numberIndex], mi[5].getCoordinateX(), mi[5].getCoordinateY());
		GraphicManager.getInstance().drawString(mi[6].getString(), mi[6].getCoordinateX(), mi[6].getCoordinateY());
		GraphicManager.getInstance().drawString("=>", mi[cursorPosition].getCoordinateX() - 30, mi[cursorPosition].getCoordinateY());
	}
	@Override
	public void close() {
		System.out.println("Menu close");

	}

}