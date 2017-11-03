package gamescene;

import java.io.File;

import enumerate.GameSceneName;
import manager.GraphicManager;
import manager.InputManager;
import struct.Key;

public class HomeMenu extends GameScene {

	// 表示する項目数
	private final int NUMBER_OF_ITEM = 3;
	MenuItem[] mi = new MenuItem[NUMBER_OF_ITEM];
	private String[] replayName;


	// 現在のカーソル位置
	private int cursorPosition;
	// 現在選択されているreplayのIndex
	private int replayIndex;

	private final String REPLAY_PATH = "./log/replay/";

	public HomeMenu(){
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

		mi[0] = new MenuItem("FIGHT : ", 50, 50, 0);
		mi[1] = new MenuItem("REPLAY : ", 50, 100, 1);
		mi[2] = new MenuItem("EXIT : ", 50, 310, 2);

		cursorPosition = 0;
		replayIndex = 0;

		// get file list from the folder
		String path = REPLAY_PATH;
		File dir = new File(path);
		File[] files = dir.listFiles();

		files = dir.listFiles();
		int nFiles = files != null ? files.length : 0;
		replayName = new String[nFiles];
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
			 	replayName[i] = String.valueOf(fileFullPath);
			}
		}
		if(nFiles == 0){
			replayName = new String[1];
			replayName[0] = "None";
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
				FightingMenu fightingMenu = new FightingMenu();  //次のシーンのコンストラクタ作成
				this.setTransitioFlag(true);    //現在のシーンからの遷移要求をtrueに
				this.setNextGameScene(fightingMenu);       //次のシーンをセットする
			}
			break;

		case 1:
			if(key.R==true){
				if(replayIndex == replayName.length - 1) replayIndex = 0;
				else replayIndex++;
			}
			if(key.L==true){
				if(replayIndex == 0) replayIndex = replayName.length - 1;
				else replayIndex--;
			}
			if(key.A==true){
				//リプレイの呼び出し処理を
			}
			break;

		case 2:
			if(key.A){
				//終了処理を
			}

			break;

		default:
			break;
		}

		this.drawScreen();

	}

	public void drawScreen() {
		GraphicManager.getInstance().drawString(mi[0].getString(), mi[0].getCoordinateX(), mi[0].getCoordinateY());
		GraphicManager.getInstance().drawString(mi[1].getString() + replayName[replayIndex], mi[1].getCoordinateX(), mi[1].getCoordinateY());
		GraphicManager.getInstance().drawString(mi[2].getString(), mi[2].getCoordinateX(), mi[2].getCoordinateY());
		GraphicManager.getInstance().drawString("=>", mi[cursorPosition].getCoordinateX() - 30, mi[cursorPosition].getCoordinateY());
	}
	@Override
	public void close() {
		System.out.println("Menu close");

	}

}
