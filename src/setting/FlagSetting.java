package setting;

/**
 * ゲームの機能に関するフラグを扱うクラス．
 */
public class FlagSetting {

    /**
     * 繰り返し対戦を行うかどうかのフラグ．
     */
    public static boolean automationFlag = false;

    /**
     * AIの全組み合わせの対戦を行うかどうかのフラグ．
     */
    public static boolean allCombinationFlag = false;

    /**
     * 背景画像を使用するかどうかのフラグ．
     */
    public static boolean enableBackground = true;

    /**
     * Pythonを使用するかどうかのフラグ．
     */
    public static boolean py4j = false;

    /**
     * アクションを行った回数をログに出力するかどうかのフラグ．
     */
    public static boolean debugActionFlag = false;

    /**
     * 未使用．
     */
    public static boolean debugFrameDataFlag = false;

    /**
     * トレーニングモードを使用するかどうかのフラグ．
     */
    public static boolean trainingModeFlag = false;

    /**
     * P1,P2に最大HPを設定するかどうかのフラグ．
     */
    public static boolean limitHpFlag = false;

    /**
     * 音をミュートにするかどうかのフラグ．
     */
    public static boolean muteFlag = false;

    /**
     * jsonファイルに試合データを出力するかどうかのフラグ．
     */
    public static boolean jsonFlag = false;

    /**
     * エラーログを出力するかどうかのフラグ．
     */
    public static boolean outputErrorAndLogFlag = false;

    /**
     * FastModeで起動するかどうかのフラグ．
     */
    public static boolean fastModeFlag = false;

    /**
     * ゲームウィンドウを生成するかどうかのフラグ．
     */
    public static boolean enableWindow = true;

    /**
     * ラウンド終了時にスローモーションを発生させるかどうかのフラグ
     */
    public static boolean slowmotion = false;
    
    public static boolean grpc = true;
    public static boolean grpcAuto = false;
    public static boolean isGrpcAutoReady = false;
    
    public static boolean visualVisibleOnRender = true;
    public static boolean enableBuiltinSound = false;
    public static boolean enableReplaySound = false;
    public static boolean enableAudioPlayback = false;
    public static boolean saveSoundOnReplay = false;
    
}
