package com.home.automation.bluetooth;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;

import com.home.automation.common.cmds.GarageDoor;
import com.home.automation.common.config.GarageConfiguration;
import com.home.automation.garage.DoorRelay;
import com.home.automation.garage.DoorState;

public class ProcessConnection extends Thread {
	private static final Logger logger = Logger.getLogger(ProcessConnection.class.getCanonicalName());
	private static GarageConfiguration myGarageConfig = GarageConfiguration.getGarageConfiguration();

	private static DoorRelay garageDoor = null;
	
	private StreamConnection mConnection = null;
	private OutputStream outputStream = null;
	private Server bluetoothServer = null;

	protected final static String OPEN = "OPEN_GARAGE_DOOR";
	protected final static String CLOSE = "CLOSE_GARAGE_DOOR";
	
	public ProcessConnection(StreamConnection connection, Server bluetoothServer) throws Exception
	{
		try {
			garageDoor = DoorRelay.getDoorRelayInstance();
		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			throw new Exception(e.fillInStackTrace());
		}
		
		this.bluetoothServer = bluetoothServer;
		mConnection = connection;
	}

	@Override
	public void run() {
		logger.info("Connected: ProcessConnectionThread running.");

		try {
	        RemoteDevice remoteDev = RemoteDevice.getRemoteDevice(mConnection);
	        String remoteName = remoteDev.getFriendlyName(true);
	        String remoteAddress = remoteDev.getBluetoothAddress();
	        logger.info("Connected to remote device name: " + remoteName + " at address: " + remoteAddress);

			InputStream inputStream = mConnection.openInputStream();
			
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Connected: waiting for input");
			}
			
			BufferedInputStream inputS = new BufferedInputStream(inputStream);
			byte[] buffer = new byte[GarageDoor.getCmdSize()];
			int length = 0;
			while((length = inputS.read(buffer)) != -1) {
				String cmd = new String(buffer, 0, length);			
				processCommand(cmd);
			}
			
			mConnection.close();
	        logger.info("Disconnected from remote device name: " + remoteName + " at address: " + remoteAddress);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Got excepton: ", e);

			try {			
				if (mConnection != null) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Closing connection: " + mConnection.toString());
					}
					
					mConnection.close();
				}
			} catch (IOException iox) {
				logger.log(Level.SEVERE, "Got excepton: ", iox);			}
		}		
	}

	private DoorState processCommand(String command) throws InterruptedException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Processing command: " + command);
		}

		DoorState executionState = DoorState.UNKNOWN;
		try {
			if(myGarageConfig.getIsRelayInstalled() && (command.compareTo(GarageDoor.ACTIVATE_RELAY) == 0)) {
				executionState = garageDoor.activateRelay();
			} else if ((myGarageConfig.getIsCloseMagnetInstalled() || myGarageConfig.getIsOpenMagnetInstalled()) 
					&& (command.compareTo(GarageDoor.GET_MAGNETIC_SENSOR_STATUS) == 0)) {
				executionState = garageDoor.getDoorState();
			} else if(logger.isLoggable(Level.FINE)) {
					logger.severe("Neither Relay nor magnetic sensors set in the configuration - cannot execute command: " + command);
			}
		} catch (InterruptedException iex) {
			iex.printStackTrace();
		}
		
		return executionState;
	}
}