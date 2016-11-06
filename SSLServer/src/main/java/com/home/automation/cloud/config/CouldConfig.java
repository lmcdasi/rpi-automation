package com.home.automation.cloud.config;

import java.io.File;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

public class CouldConfig {
	private static final Logger logger = Logger.getLogger(CouldConfig.class.getCanonicalName());
	
	private static CouldConfig __instance = new CouldConfig();
	
	private String keyStore = null;
    private String keyPasswd = null;
    private String storeType = null;
    private String cloudProto = null;
	private String serverPort = null;
	private String homeURL = null;
	private String discover = null;
	private String turnOn = null;
	private String turnOff = null;
	
	public CouldConfig() {
		try {
			readConfigData();
		} catch (Exception ex) {
			logger.error("Exception:", ex);
		}
	}
	
	public static CouldConfig getCouldConfiguration() {
		return __instance;
	}
	
	public String getKeyStore() {
		return keyStore;
	}
	public void setKeystore(XMLConfiguration config) {
		keyStore = new String (config.getString("keystore.file", "unknown.jks"));
	}
	
	public String getKeyPasswd() {
		return keyPasswd;
	}
	public void setKeyPasswd(XMLConfiguration config) {
		keyPasswd = new String (config.getString("keystore.passwd", "setyourpasswd"));
	}
	
	public String getStoreType() {
		return storeType;
	}
	public void setStoreType(XMLConfiguration config) {
		storeType = new String (config.getString("storetype", "setyourpasswd"));
	}
	
	public String getCloudProto() {
		return cloudProto;
	}
	public void setCloudProto(XMLConfiguration config) {
		cloudProto = new String (config.getString("cloudproto", "TLS"));
	}
	
	public String getServerPort() {
		return serverPort;
	}
	public void setServerPort(XMLConfiguration config) {
		serverPort = new String (config.getString("port", "443"));
	}
	
	public String getHomeURL() {
		return homeURL;
	}
	public void setHomeURL(XMLConfiguration config) {
		homeURL = new String (config.getString("home.url", "yourmainpageurl"));
	}
	
	public String getDiscover() {
		return discover;
	}
	public void setDiscover(XMLConfiguration config) {
		discover = new String (config.getString("home.discover", "yourmainpageurl"));
	}
	
	public String getTurnOn() {
		return turnOn;
	}
	public void setTurnOn(XMLConfiguration config) {
		turnOn = new String (config.getString("home.turnOn", "yourmainpageurl"));
	}
	
	public String getTurnOff() {
		return turnOff;
	}
	public void setTurnOff(XMLConfiguration config) {
		turnOff = new String (config.getString("home.turnOff", "yourmainpageurl"));
	}
	
	public void readConfigData() throws Exception {
		String configFile = System.getProperty("home.automation.cloud.config");
		
		if (configFile != null) {
			File confd = new File(configFile);
			if (confd.exists() && confd.isFile() && confd.canRead()) {
				XMLConfiguration config = new XMLConfiguration(confd);
				setKeystore(config);
				setKeyPasswd(config);
				setStoreType(config);
				setCloudProto(config);
				setServerPort(config);
				setHomeURL(config);
				setTurnOn(config);
				setTurnOff(config);
			}
		}
	}
}
