package com.home.automation.server;

import org.apache.log4j.Logger;

import com.home.automation.bluetooth.Server;
import com.home.automation.cloud.SSLServerApplication;
import com.home.automation.common.mqtt.client.AsyncMqttClient;

public class HomeServer {
	private static final Logger logger = Logger.getLogger(HomeServer.class.getCanonicalName());
	
	private static final HomeServer _instance = new HomeServer();
	
	private static HomeServer getInstance() {
		return _instance;
	}
	
	private void startMqtt() {
		logger.info("Async connect request towards MQTT server");
		AsyncMqttClient mqttClient = AsyncMqttClient.getInstance();
		mqttClient.connectAsync();
	}
	
	private void startBluetoothServer() {
		Server bluetoothSrv = new Server();
		
		bluetoothSrv.start();
	}
	
	private void startCloudServer() {
		(new Thread(new SSLServerApplication())).start();
	}
	
	public static void main(String[] args) {
		HomeServer.getInstance().startMqtt();
		HomeServer.getInstance().startBluetoothServer();
		HomeServer.getInstance().startCloudServer();
	}
}
