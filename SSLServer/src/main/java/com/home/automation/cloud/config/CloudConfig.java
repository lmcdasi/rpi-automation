package com.home.automation.cloud.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

public class CloudConfig {
	private static final Logger logger = Logger.getLogger(CloudConfig.class.getCanonicalName());
	
	private static CloudConfig __instance = new CloudConfig();
	
	private String keyStore = null;
    private String keyPasswd = null;
    private String storeType = null;
    private String cloudProto = null;
	private String serverPort = null;
	private String homeURL = null;
	private String discover = null;
	
	private List<Appliance> appliances = new ArrayList<Appliance>();
	
	public CloudConfig() {
		try {
			readConfigData();
		} catch (Exception ex) {
			logger.error("Exception:", ex);
		}
	}
	
	public static CloudConfig getCloudConfiguration() {
		return __instance;
	}
	
	public String getKeyStore() {
		return this.keyStore;
	}
	public void setKeystore(XMLConfiguration config) {
		this.keyStore = new String (config.getString("keystore.file", "unknown.jks"));
	}
	
	public String getKeyPasswd() {
		return this.keyPasswd;
	}
	public void setKeyPasswd(XMLConfiguration config) {
		this.keyPasswd = new String (config.getString("keystore.passwd", "setyourpasswd"));
	}
	
	public String getStoreType() {
		return this.storeType;
	}
	public void setStoreType(XMLConfiguration config) {
		this.storeType = new String (config.getString("storetype", "setyourpasswd"));
	}
	
	public String getCloudProto() {
		return this.cloudProto;
	}
	public void setCloudProto(XMLConfiguration config) {
		this.cloudProto = new String (config.getString("cloudproto", "TLS"));
	}
	
	public String getServerPort() {
		return this.serverPort;
	}
	public void setServerPort(XMLConfiguration config) {
		this.serverPort = new String (config.getString("port", "443"));
	}
	
	public String getHomeURL() {
		return this.homeURL;
	}
	public void setHomeURL(XMLConfiguration config) {
		this.homeURL = new String (config.getString("home.url", "yourmainpageurl"));
	}
	
	public String getDiscover() {
		return this.discover;
	}
	public void setDiscover(XMLConfiguration config) {
		this.discover = new String (config.getString("home.discover", "yourmainpageurl"));
	}
	
	public List<Appliance> getAppliances() {
		return this.appliances;
	}
	
	public void setAppliance(XMLConfiguration config) {
		List<HierarchicalConfiguration> devices = config.configurationsAt("home.appliances.appliance");
		
		for(HierarchicalConfiguration device : devices) {
			Appliance appliance = new Appliance();
			appliance.setApplianceId(device.getString("id"));
			appliance.setFriendlyDescription(device.getString("description"));
			appliance.setFriendlyName(device.getString("name"));
			appliance.setIsReachable(device.getBoolean("enabled"));
			appliance.setManufacturerName(device.getString("manufacturer"));
			appliance.setModelName(device.getString("model"));
			appliance.setVersion(device.getString("version"));
			
			List<HierarchicalConfiguration> actions = device.configurationsAt("actions.action");
			for (HierarchicalConfiguration action : actions) {
				appliance.setAction(action.getString(""));
			}

			this.appliances.add(appliance);
		}
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
				setDiscover(config);
				setAppliance(config);
			}
		}
	}
}
