package python;

import gamescene.Python;
import py4j.GatewayServer;
import setting.LaunchSetting;

/** Python側でJava側の処理を行うためのゲートウェイの管理を行うクラス. */
public class PyGatewayServer {

	/** Python側でJava側の処理を行うためのゲートウェイ */
	private GatewayServer gatewayServer;

	/** 指定されたゲームシーンでゲートウェイを作成するコンストラクタ */
	public PyGatewayServer(Python python) {
		this.gatewayServer = new GatewayServer(new PyManager(python), LaunchSetting.py4jPort);
		this.gatewayServer.start();
	}

	/** ゲートウェイを閉じる */
	public void close() {
		this.gatewayServer.shutdown();
		LaunchSetting.pyGatewayServer = null;
	}

}
