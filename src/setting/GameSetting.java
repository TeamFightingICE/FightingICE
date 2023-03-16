package setting;

/**
 * ゲームの画面幅、FPS、使うことができるキャラクターなどゲームの基本情報の設定を扱うクラス．
 */
public class GameSetting {

    /**
     * ゲーム画面の横幅．
     */
    public static final int STAGE_WIDTH = 960;

    /**
     * ゲーム画面の縦幅．
     */
    public static final int STAGE_HEIGHT = 640;
    
    public static final String TITLE_NAME = "DareFightingICE";

    /**
     * ゲームのFPS設定．
     */
    public static final int FPS = 60;

    /**
     * ゲームで使うことができるキャラクターを格納する配列．
     */
    public static final String[] CHARACTERS = {"ZEN", "GARNET", "LUD"};

    /**
     * ゲームの既定繰り返し回数を格納する配列．
     */
    public static final int[] REPEAT_NUMBERS = {1, 2, 3, 5, 10, 30, 50, 100};

    /**
     * 1ラウンドの総フレーム数．
     */
//	public static final int ROUND_FRAME_NUMBER = 3600;
    public static int ROUND_FRAME_NUMBER = 3600;

    /**
     * ラウンド間の休憩時間．
     */
    public static final int BREAKTIME_FRAME_NUMBER = 70;

    /**
     * 重力の影響を設定するパラメータ．
     */
    public static final int GRAVITY = 1;

    /**
     * 摩擦の影響を設定するパラメータ．
     */
    public static final int FRICTION = 1;

    /**
     * キー入力を保持する上限数．
     */
    public static final int INPUT_LIMIT = 30;

    /**
     * ラウンド数の上限数．
     */
    // public static final int ROUND_MAX = 3;
    public static int ROUND_MAX = 3;

    /**
     * コンボ継続を判定するためのフレーム数．<br>
     * ある攻撃がヒットしてから，次の攻撃がヒットするまでのフレーム数がこのフレーム数以下ならコンボが継続する．
     */
    public static final int COMBO_LIMIT = 30;

    /**
     * 追加フレーム(スローモーション用)
     */
    public static final int ROUND_EXTRAFRAME_NUMBER = 180;

    /**
     * Audio sampling rate
     */
    public static final int SOUND_SAMPLING_RATE = 48000;
    public static final int SOUND_RENDER_SIZE = GameSetting.SOUND_SAMPLING_RATE / GameSetting.FPS;
    public static final int SOUND_BUFFER_SIZE = (int) Math.pow(2, Math.ceil(Math.log(SOUND_RENDER_SIZE) / Math.log(2)));
}
