package com.home.automation.common.config;

import java.io.File;

import javax.bluetooth.UUID;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

public class GarageConfiguration {
	private static final Logger logger = Logger.getLogger(GarageConfiguration.class.getCanonicalName());
	
	// MQTT Server URI
	private String serverURI = null;
	// MQTT Client ID
	private String mqttClientId = null;
	// SSL port
	private String sslPortNo = null;
	// UUID used to provide service towards blts apps
	private UUID uuid = null;
	// relay is installed
	private boolean isRelayInstalled = false;
	// relay assigned gpio pin
	private String bltoutpin = null;
	// magnetic sensor gpio pin - monitoring door close
	private String mntcloseoutpin = null;
	private boolean isCloseMagnetInstalled = false;
	// magnetic sensor gpio pin - monitoring door open
	private String mntopenoutpin = null;
	private boolean isOpenMagnetInstalled = false;
	// door movement doorMovementTimer
	private int doorMovementTimer = 6000;
	
	private static GarageConfiguration __instance = new GarageConfiguration();
	
	private GarageConfiguration() {
		try {
			readConfigData();
		} catch (BluetoothStateException e) {
			logger.error("Exception:", e);
			System.exit(-1);
		}
	}

	public static GarageConfiguration getGarageConfiguration() {
		return __instance;
	}
	
	public String getServerURI() {
		return serverURI;
	}
	public void setServerURI(XMLConfiguration config) {
		serverURI = new String (config.getString("mqtt.uri.name", "tcp://localhost:1883"));
	}
	
	public String getMqttClientId() {
		return mqttClientId;
	}
	public void setMqttClientId(XMLConfiguration config) {
		mqttClientId = new String(config.getString("mqtt.id", "unknown"));
	}
	
	public void setSslPortNo(XMLConfiguration config) {
		sslPortNo = new String (config.getString("ssl.port", "9443"));
	}
	public String getSslPortNo() {
		return sslPortNo;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	public void setUUID(XMLConfiguration config) throws BluetoothStateException {
		uuid = new UUID(config.getString("garage.bluetooth.uuid", LocalDevice.getLocalDevice().getBluetoothAddress()), false);
	}
	
	public String getRelayGpioPin() {
		return bltoutpin;
	}
	public void setRelayGpioPin(XMLConfiguration config) {
		bltoutpin = config.getString("relay.bltoutpin", "GPIO_07");
	}
	
	public boolean getIsCloseMagnetInstalled() {
		return isCloseMagnetInstalled;
	}
	public void setIsCloseMagnetInstalled(XMLConfiguration config) {
		isCloseMagnetInstalled = config.getBoolean("magneticsensor.close.enabled");
	}
	
	public String getMagnetSensorClosePin() {
		return mntcloseoutpin;
	}
	public void setMagnetSensorClosePin(XMLConfiguration config) {
		mntcloseoutpin = config.getString("magneticsensor.close.pin", "GPIO_11");
	}
	
	public String getMagnetSensorOpenPin() {
		return mntopenoutpin;
	}
	public boolean getIsOpenMagnetInstalled() {
		return isOpenMagnetInstalled;
	}
	
	public void setIsOpenMagnetInstalled(XMLConfiguration config) {
		isOpenMagnetInstalled = config.getBoolean("magneticsensor.open.enabled");
	}
	public void setMagnetSensorOpenPin(XMLConfiguration config) {
		mntopenoutpin = config.getString("magneticsensor.open.pin", "GPIO_13");
	}
	
	public boolean getIsRelayInstalled() {
		return isRelayInstalled;
	}
	public void setIsRelayInstalled(XMLConfiguration config) {
		isRelayInstalled = config.getBoolean("relay.installed", false);
	}
	
	public int getDoorMovementTimer() {
		return doorMovementTimer;
	}
	public void setDoorMovementTimer(XMLConfiguration config) {
		doorMovementTimer = config.getInteger("garage.delay", 6000);
	}
	
	public void readConfigData() throws BluetoothStateException {
		String configFile = System.getProperty("home.automation.config.file");
		
		if (configFile != null) {
			File confd = new File(configFile);
			if (confd.exists() && confd.isFile() && confd.canRead()) {
				try {
					XMLConfiguration config = new XMLConfiguration(confd);
					
					setUUID(config);
					setIsRelayInstalled(config);
					setIsCloseMagnetInstalled(config);
					setIsOpenMagnetInstalled(config);
					setRelayGpioPin(config);
					setMagnetSensorClosePin(config);
					setMagnetSensorOpenPin(config);
					setDoorMovementTimer(config);
					setSslPortNo(config);
					
				} catch (ConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
