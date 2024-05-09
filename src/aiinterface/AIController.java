package aiinterface;

import java.util.LinkedList;

import informationcontainer.RoundResult;
import manager.InputManager;
import setting.LaunchSetting;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.ScreenData;

/**
 * AIのスレッドや処理を管理するクラス．
 */
public class AIController extends Thread {

	private char deviceType;
    /**
     * AIに実装すべきメソッドを定義するインタフェース．
     */
    private AIInterface ai;
    
    //private GameData gameData;

    /**
     * The character's side flag.<br>
     * {@code true} if the character is P1, or {@code false} if P2.
     */
    private boolean playerNumber;

    /**
     * 対戦が始まっているかどうかを表すフラグ．
     */
    private boolean isFighting;

    /**
     * 入力されたキー．
     */
    private Key key;

    /**
     * 遅れフレーム．
     */
    private final static int DELAY = 15;

    /**
     * フレームデータを格納するリスト．
     */
    private LinkedList<FrameData> framesData;

    /**
     * 画面情報を格納したデータ．
     */
    private ScreenData screenData;

    private AudioData audioData;

    /**
     * 各AIの処理を同時に始めるための同期用オブジェクト．
     */
    private Object waitObj;
    
    private boolean isRoundEnd;
    private RoundResult roundResult;
    
    //private List<Double> durations = new ArrayList<>();

    /**
     * 引数に指定されたAIインタフェースをセットし，AIControllerを初期化するクラスコンストラクタ．
     *
     * @param ai AIに実装すべきメソッドを定義するインタフェース
     * @see AIInterface
     */
    public AIController(AIInterface ai) {
        this.ai = ai;
        this.deviceType = InputManager.DEVICE_TYPE_AI;
    }
    /**
     * 引数で与えられたパラメータをセットし，初期化を行う．
     *
     * @param waitFrame    各AIの処理を同時に始めるための同期用オブジェクト
     * @param gameData     ステージの画面幅や最大HPなどの，ゲーム内で不変の情報を格納したクラスのインスタンス
     * @param playerNumber the character's side flag.<br>
     *                     {@code true} if the character is P1, or {@code false} if P2.
     * @see GameData
     */
    public void initialize(Object waitFrame, GameData gameData, boolean playerNumber) {
        this.waitObj = waitFrame;
        this.playerNumber = playerNumber;
        this.key = new Key();
        this.framesData = new LinkedList<FrameData>();
        this.clear();
        this.isFighting = true;
        this.isRoundEnd = false;
        
    	this.ai.initialize(gameData, playerNumber);
    }
    
    public Key input() {
    	return this.key;
    }
    
    @Override
    public void run() {
        while (isFighting) {
            synchronized (this.waitObj) {
                try {
                    this.waitObj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            if (isRoundEnd) {
            	this.ai.roundEnd(roundResult);
            	this.isRoundEnd = false;
            	this.roundResult = null;
            } else {
            	boolean isControl;

                try {
                    isControl = this.framesData.getLast().getCharacter(this.playerNumber).isControl();
                } catch (NullPointerException e) {
                    // while game is not started
                    isControl = false;
                }

                FrameData frameData = !this.framesData.isEmpty() ? new FrameData(this.framesData.removeFirst()) : new FrameData();
                
                // screen raw data isn't provided to sound-only AI
    	        if (!LaunchSetting.noVisual[this.playerNumber ? 0: 1]){
    	            this.ai.getScreenData(this.screenData);
    	        } else {
    	        	frameData.removeVisualData();
    	        }
            	this.ai.getInformation(frameData, isControl);
    	        this.ai.getAudioData(this.audioData);
    	        
    	        this.ai.processing();
    	        this.setInput(this.ai.input());
            }
	        
	        ThreadController.getInstance().notifyEndProcess(this.playerNumber);
        }
    }

    /**
     * AIからの入力情報を返す．<br>
     * 入力情報が無ければ空のキーを返す．
     *
     * @return AIからの入力情報
     * @see Key
     */
    public synchronized Key getInput() {
        if (this.key != null) {
            return this.key;
        } else {
            return new Key();
        }
    }

    /**
     * AIからの入力情報をセットする．
     *
     * @param key AIからの入力情報
     */
    public synchronized void setInput(Key key) {
        this.key = new Key(key);
    }

    /**
     * 対戦処理後のフレームデータをリストにセットする．<br>
     * リストのサイズがDELAYより大きければ，最も古いフレームデータを削除する．
     *
     * @param fd 対戦処理後のフレームデータ
     * @see FrameData
     */
    public synchronized void setFrameData(FrameData fd) {
        if (fd != null) {
            this.framesData.addLast(fd);
        } else {
            this.framesData.addLast(new FrameData());
        }

        while (this.framesData.size() > DELAY) {
            this.framesData.removeFirst();
        }
    }

    /**
     * 対戦処理後の画面情報をセットする．<br>
     *
     * @param screenData 対戦処理後の画面情報
     * @see ScreenData
     */
    public synchronized void setScreenData(ScreenData screenData) {
        this.screenData = screenData;
    }

    public synchronized void setAudioData(AudioData audioData) {
        this.audioData = audioData;
    }

    /**
     * リストに格納してあるフレームデータを削除する．<br>
     * その後，DELAY-1個の空のフレームデータをリストに格納する．
     */
    public synchronized void clear() {
        if (this.framesData != null) {
            this.framesData.clear();

            while (this.framesData.size() < DELAY) {
                this.framesData.add(new FrameData());
            }
        }
    }
    
    /**
     * 現在のラウンド終了時の結果をAIに渡す．
     *
     * @param roundResult 現在のラウンド終了時の結果
     * @see RoundResult
     */
    public synchronized void informRoundResult(RoundResult roundResult) {
    	this.isRoundEnd = true;
    	this.roundResult = roundResult;
    }

    /**
     * 対戦が終わったことを通知し，AIの終了処理を行う．
     */
    public synchronized void gameEnd() {
        this.isFighting = false;
        if (this.deviceType == InputManager.DEVICE_TYPE_AI) {
            this.ai.close();
    	}
        synchronized (this.waitObj) {
            this.waitObj.notifyAll();
        }
    }
}
