package com.home.automation.bluetooth;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import com.home.automation.common.config.GarageConfiguration;

public class Server extends Thread {
	private static final Logger logger = Logger.getLogger(Server.class.getCanonicalName());
	private static GarageConfiguration myGarageConfig = GarageConfiguration.getGarageConfiguration();

	public Server() {}

	@Override
	public void run() {
		logger.info("Bluetooth Server started.");
		waitForConnection();
	}

	private void waitForConnection() {
		LocalDevice localDev = null;
		StreamConnectionNotifier notifier = null;
		StreamConnection connection = null;
		
		logger.info("waitForConnection - called.");

		try {
			localDev = LocalDevice.getLocalDevice();
			
			UUID deviceUUID = myGarageConfig.getUUID();
						
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Bluetooth Device UUID set to: " + deviceUUID);
			}
			
			localDev.setDiscoverable(DiscoveryAgent.GIAC);
					
			String url = "btspp://localhost:" + deviceUUID.toString() + ";name=Garage Door Actuator";
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("SDP url: " + url);
			}

			notifier = (StreamConnectionNotifier) Connector.open(url, Connector.READ_WRITE, true);
			
			while (true) {
				try {
					logger.info("Waiting for connection ...");
					connection = notifier.acceptAndOpen();
				
					Thread processThread = new Thread(new ProcessConnection(
							connection, this));

					processThread.start();
				} catch (Exception ex) {
					logger.log(Level.SEVERE, "Server exception: ", ex);
					
					if (notifier != null) notifier.close();
					
					System.exit(-1);
				}
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Server exception: ", ex);
			return;			
		}
	}
	
	public static void main(String[] args) {	
		new Server().start();
	}
}