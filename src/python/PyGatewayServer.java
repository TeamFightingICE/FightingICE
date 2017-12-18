package python;

import gamescene.Python;
import py4j.GatewayServer;
import setting.LaunchSetting;

/**
 * Javaの処理をPythonによって行うためのゲートウェイの管理を行うクラス.
 */
public class PyGatewayServer {

	/**
	 * Javaの処理をPythonで行うためのゲートウェイ．
	 */
	private GatewayServer gatewayServer;

	/**
	 * 引数で渡されたゲームシーンを用いてゲートウェイを作成するクラスコンストラクタ．
	 *
	 * @param Python用のゲームシーン
	 */
	public PyGatewayServer(Python python) {
		this.gatewayServer = new GatewayServer(new PyManager(python), LaunchSetting.py4jPort);
		this.gatewayServer.start();
	}

	/**
	 * ゲートウェイを閉じる．
	 */
	public void close() {
		this.gatewayServer.shutdown();
		LaunchSetting.pyGatewayServer = null;
	}

}
