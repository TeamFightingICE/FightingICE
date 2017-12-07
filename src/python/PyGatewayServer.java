package python;

import gamescene.Python;
import py4j.GatewayServer;
import setting.LaunchSetting;

public class PyGatewayServer {
	private GatewayServer gatewayServer;

	public PyGatewayServer(Python python) {
		this.gatewayServer = new GatewayServer(new PyManager(python), LaunchSetting.py4jPort);
		this.gatewayServer.start();
	}

	public void close() {
		this.gatewayServer.shutdown();
		LaunchSetting.pyGatewayServer = null;
	}

}
