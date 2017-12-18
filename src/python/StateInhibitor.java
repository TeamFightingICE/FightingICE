package python;

/**
 * リプレイ再生時の処理を扱うインタフェース．
 */
public interface StateInhibitor {

	/**
	 * リプレイの再生を1フレーム行う．
	 */
	void replayUpdate();

}
