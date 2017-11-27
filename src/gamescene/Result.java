package gamescene;

import java.util.ArrayList;

import enumerate.GameSceneName;
import informationcontainer.RoundResult;
import manager.InputManager;

public class Result extends GameScene {

	private ArrayList<RoundResult> roundResults;

	public Result() {
		this.roundResults = new ArrayList<RoundResult>();
	}

	public Result(ArrayList<RoundResult> roundResults) {
		this.roundResults = roundResults;
	}

	@Override
	public void initialize() {
		InputManager.getInstance().setSceneName(GameSceneName.RESULT);
	}

	/*
	 * public static final int[] COORDINATE_X = {50,150}; public static final
	 * int COORDINATE_Y = 50; /*Definition of winning, losing or draw public
	 * static String[][] winningBoard = new String[2][ROUND_MAX]; showing press
	 * Z to continue public static String Press_Key = "Press Z to continue";
	 * static final int ROUND_MAX = 3;
	 *
	 *
	 * int[][] scores = new int[2][ROUND_MAX];
	 *
	 *
	 *
	 * public void setScore(int[] score,int[] score2){ for(int i = 0 ; i <
	 * scores[0].length ; i++){ scores[0][i] = score[i]; } for(int i = 0 ; i <
	 * scores[1].length ; i++){ scores[1][i] = score2[i]; } }
	 *
	 * public void setScore(int[][] scores){ for(int i = 0 ; i < scores.length ;
	 * i++){ for(int j = 0; j < scores[i].length ; j++){ this.scores[i][j] =
	 * scores[i][j]; } } } public HomeMenu() { this.gameSceneName =
	 * GameSceneName.Home_Menu; this.isGameEndFlag = false;
	 * this.isTransitionFlag = false; this.nextGameScene = null; } public void
	 * render(GraphicManager gm) { for(int i = 0 ; i < scores[0].length && i <
	 * scores[1].length ; i++){ gm.drawString(String.valueOf(scores[0][i]),
	 * COORDINATE_X[0], COORDINATE_Y+i*200);
	 * gm.drawString(String.valueOf(scores[1][i]), COORDINATE_X[1],
	 * COORDINATE_Y+i*200); } for(int i=0;i<3;i++) { if
	 * (score[0][i]>score[i][1]) { /*show who wins or draw
	 * WinningBoard[0][i]=LaunchSetting.aiNames[0]; WinningBoard[1][i]="wins."
	 * gm.drawString(WinningBoard[0][i],COORDINATE_X[1]+100,COORDINATE_Y+i*200);
	 * gm.drawString(WinningBoard[1][i],COORDINATE_X[1]+110,COORDINATE_Y+i*200);
	 * } if(score[0][i]<score[i][1]) {
	 * WinningBoard[0][i]=LaunchSetting.aiNames[1]; WinningBoard[1][i]="wins."
	 * gm.drawString(WinningBoard[0][i],COORDINATE_X[1]+100,COORDINATE_Y+i*200);
	 * gm.drawString(WinningBoard[1][i],COORDINATE_X[1]+110,COORDINATE_Y+i*200);
	 * } else { WinningBoard[0][i]="Draw."
	 * gm.drawString(WinningBoard[0][i],COORDINATE_X[1]+100,COORDINATE_Y+i*200);
	 * } } gm.drawString(Press_Key,COORDINATE_X[0],COORDINATE_Y+800);
	 *
	 * } public void setDevice(int[] deviceType,String[] aiName){
	 * this.deviceType = deviceType; this.aiName = aiName; } public void
	 * outputResultLog(){ Calendar cal1 = Calendar.getInstance(); int year =
	 * cal1.get(Calendar.YEAR); int month = cal1.get(Calendar.MONTH) + 1; int
	 * day = cal1.get(Calendar.DATE); int logDataCount = 0;
	 *
	 * String[] deviceName = new String[deviceType.length]; int count = 0;
	 *
	 * for(int i = 0 ; i < deviceType.length ; i++){ if(deviceType[i] ==
	 * Input.DEVICE_TYPE_AI){ deviceName[i] = aiName[count]; count++; }else{
	 * deviceName[i] = "Human"; } }
	 *
	 * File file; do{ file = new File("./log/point/" + year + month + day + "_"
	 * +deviceName[0] + "_" + deviceName[1] + "_" + logDataCount + ".PLOG");
	 * logDataCount++; }while(file.exists()); try { PrintWriter pw = new
	 * PrintWriter(new BufferedWriter (new FileWriter(file))); for(int i = 0 ; i
	 * < scores[0].length ; i++){ pw.println( i + "," + scores[0][i] + "," +
	 * scores[1][i]); } pw.close(); } catch (IOException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } } }
	 *
	 * @Override public void initialize() {
	 *
	 * // TODO Auto-generated method stub }
	 *
	 * @Override public void update() { InputManager.get
	 *
	 * // TODO Auto-generated method stub render(gm); if(key.A) { HomeMenu
	 * homeMenu = new HomeMenu(); this.setTransitionFlag(true);
	 * this.setNextGameScene(homeMenu); } }
	 *
	 * @Override public void close() {
	 *
	 * // TODO Auto-generated method stub }
	 */
}
