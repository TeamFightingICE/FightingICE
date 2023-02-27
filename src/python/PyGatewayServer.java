package python;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
	 * @param python Python用のゲームシーン
	 */
	public PyGatewayServer(Python python) {
		GatewayServer.GatewayServerBuilder builder = new GatewayServer.GatewayServerBuilder(new PyManager(python));
		try {
			builder.javaAddress(InetAddress.getByName("0.0.0.0"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		builder.javaPort(LaunchSetting.py4jPort);
		this.gatewayServer = builder.build();
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
