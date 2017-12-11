package python;

/** リプレイの再生時の処理を管理するインタフェース */
public interface StateInhibitor {

	/** リプレイの再生を1フレーム行う */
	void replayUpdate();

}
