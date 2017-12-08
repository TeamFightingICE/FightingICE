package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import setting.LaunchSetting;
import struct.FrameData;



public class DebugActionData {

	/** 行動名とその行動の総フレーム数のHashMap */
	private Map<String, Integer> p1MotionData = new HashMap<String, Integer>();
	private Map<String, Integer> p2MotionData = new HashMap<String, Integer>();

	/**カウントする行動名のリスト*/
	private static final String[] motionName = {
			"FORWARD_WALK", "DASH", "BACK_STEP", "JUMP", "FOR_JUMP", "BACK_JUMP",
			"STAND_GUARD", "CROUCH_GUARD", "AIR_GUARD", "THROW_A", "THROW_B", "STAND_A",
			"STAND_B", "CROUCH_A","CROUCH_B", "AIR_A", "AIR_B", "AIR_DA",
			"AIR_DB", "STAND_FA", "STAND_FB", "CROUCH_FA", "CROUCH_FB", "AIR_FA",
			"AIR_FB", "AIR_UA", "AIR_UB", "STAND_D_DF_FA", "STAND_D_DF_FB", "STAND_F_D_DFA",
			"STAND_F_D_DFB", "STAND_D_DB_BA", "STAND_D_DB_BB", "AIR_D_DF_FA", "AIR_D_DF_FB", "AIR_F_D_DFA",
			"AIR_F_D_DFB", "AIR_D_DB_BA","AIR_D_DB_BB", "STAND_D_DF_FC"
		};

	private int[] p1MotinoCount = new int[motionName.length];
	private int[] p2MotionCount = new int[motionName.length];
	private FileWriter p1FileWriter, p2FileWriterr;
	private PrintWriter p1PrintWriter, p2PrintWriter;
	private BufferedReader p1BufferedReader, p2BufferedReader;
	private int check1, check2;

	/**DebugActionDataクラスの唯一のインスタンス*/
	private static DebugActionData instance;

	/**  DebugActionDataクラス唯一のインスタンスを取得するgetterメソッド
	 *
	 * @return DebugActionDatクラスの唯一のインスタンス
	 */
    public static DebugActionData getInstance(){
	   	if(instance == null) instance = new DebugActionData();
		return instance;
    }

    /** コンストラクタ */
    private DebugActionData() {
    	String p1CharacterName = LaunchSetting.characterNames[0];
    	String p2CharacterName = LaunchSetting.characterNames[1];

    	File newdir = new File("debugActionData");
		newdir.mkdir();
		Logger.getAnonymousLogger().log(Level.INFO,"start debug action mode...");
		try {
			p1FileWriter = new FileWriter("debugActionData/p1ActionFile.csv", true);
			p1PrintWriter = new PrintWriter(new BufferedWriter(p1FileWriter));
			p2FileWriterr = new FileWriter("debugActionData/p2ActionFile.csv", true);
			p2PrintWriter = new PrintWriter(new BufferedWriter(p2FileWriterr));

			p1BufferedReader = new BufferedReader(new FileReader("debugActionData/p1ActionFile.csv"));
			p2BufferedReader = new BufferedReader(new FileReader("debugActionData/p2ActionFile.csv"));
			check1 = p1BufferedReader.read();
			check2 = p2BufferedReader.read();
			p1BufferedReader.close();
			p2BufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (check1 == -1 || check2 == -1) {
			p1PrintWriter.println();
			p2PrintWriter.println();
			for (int i = 0; i < motionName.length; i++) {
				p1PrintWriter.print(motionName[i] + ",");
				p2PrintWriter.print(motionName[i] + ",");
			}
		}
		p1PrintWriter.println();
		p2PrintWriter.println();
		p1PrintWriter.println();
		p2PrintWriter.println();

		//p1の各アクションの総フレーム数をp1MotionDataに読み込む
		 try {
			 BufferedReader br = new BufferedReader( new FileReader("data/characters/"+p1CharacterName+"/Motion.csv") );
			 String s;
			 while( (s = br.readLine()) != null ) {
				 String array[] = s.split( "," ); //カンマで分割
				 for(int i = 0; i < motionName.length; i++){
					 if( motionName[i].equals(array[0])) {
						 p1MotionData.put(motionName[i], Integer.parseInt(array[1]));
					 }
				 }
			 }
			 br.close();
		 } catch( Exception e )  {
			 System.out.println( e );
		 }
		//p2の各アクションの総フレーム数をp2MotionDataに読み込む
		 try {
			 BufferedReader br = new BufferedReader( new FileReader("data/characters/"+p2CharacterName+"/Motion.csv") );
			 String s;
			 while( (s = br.readLine()) != null ) {
				 String array[] = s.split( "," );
				 for(int i = 0; i < motionName.length; i++){
					 if( motionName[i].equals(array[0])) {
						 p2MotionData.put(motionName[i], Integer.parseInt(array[1]));
					 }
				 }
			 }
			 br.close();
		 } catch( Exception e )  {
			 System.out.println( e );
		 }
    }
    /** P1とP2の行った各アクションの数を数える*/
    public void countPlayerAction(FrameData fd) {
		boolean p1CountFlag = false;
		boolean p2CountFlag = false;
		String p1Action = fd.getCharacter(true).getAction().name();
		String p2Action = fd.getCharacter(false).getAction().name();

		for (int i = 0; i < motionName.length; i++) {
			if ((p1Action.equals(motionName[i])) && (fd.getCharacter(true).getRemainingFrame() == p1MotionData.get(motionName[i])-1)) {
				p1MotinoCount[i]++;
				p1CountFlag = true;
			}
			if ((p2Action.equals(motionName[i])) && (fd.getCharacter(false).getRemainingFrame() == p2MotionData.get(motionName[i])-1)) {
				p2MotionCount[i]++;
				p2CountFlag = true;
			}
			if (p1CountFlag == true && p2CountFlag == true)
				break;
		}
	}
    /** P1とP2の行った各アクションの数のデータをCSVに出力する */
    public void outputActionCount(){
		for (int i = 0; i < motionName.length; i++) {
			p1PrintWriter.print(p1MotinoCount[i] + ",");
			p2PrintWriter.print(p2MotionCount[i] + ",");
		}
		p1PrintWriter.println();
		p1PrintWriter.flush();
		p2PrintWriter.println();
		p2PrintWriter.flush();
		Arrays.fill(p1MotinoCount, 0);
		Arrays.fill(p2MotionCount, 0);
    }

    public void closeAllWriters(){
		p1PrintWriter.close();
		p2PrintWriter.close();
    }










}
