package com.home.automation.garage;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.home.automation.common.config.GarageConfiguration;
import com.home.automation.common.mqtt.client.AsyncMqttClient;
import com.home.automation.common.mqtt.Qos;
import com.home.automation.common.mqtt.TopicName;
import com.home.automation.garage.DoorState;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class DoorRelay {
	private static Logger logger = Logger.getLogger(DoorRelay.class.getCanonicalName());

	private static GarageConfiguration myGarageConfig = GarageConfiguration.getGarageConfiguration();

	private static volatile DoorState currentState = DoorState.UNKNOWN;

	private static GpioController gpio = GpioFactory.getInstance();
	private static GpioPinDigitalOutput bltoutpin = null;
	private static MagneticSensor mntopenoutpin = null;
	private static MagneticSensor mntcloseoutpin = null;

	private static DoorRelay myDoorRelay = null;
	private static final Object lock = new Object();
	private static final Object relayMonitoring = new Object();
	
	private static final AsyncMqttClient mqttClient = AsyncMqttClient.getInstance();
	
	private static class DoorMonitoring extends Thread {
		public DoorMonitoring() {}
		
		public void run() {
			String payload = null;
			
			while(true) {
				try {
					synchronized(relayMonitoring) {
						relayMonitoring.wait();
					}
					
					Thread.sleep(myGarageConfig.getDoorMovementTimer());
					
					switch(currentState) {
						case CLOSE_IN_PROGRESS:
							currentState = DoorState.CLOSED;
							payload = new String("Change state from CLOSE_IN_PROGRESS to CLOSED.");
							mqttClient.publish(TopicName.GarageDoorTopicName, Qos.deliverOnceWithConfirmation, payload.getBytes());
							break;
						case OPEN_IN_PROGRESS:
							currentState = DoorState.OPENED;
							payload = new String("Change state from OPEN_IN_PROGRESS TO OPENED.");
							mqttClient.publish(TopicName.GarageDoorTopicName, Qos.deliverOnceWithConfirmation, payload.getBytes());
							break;
						default:
							logger.severe("State unsupported: " + currentState + " while monitoring.");
					}
				} catch (InterruptedException e) {
					logger.severe("Door Movement monitoring thread exiting ...");
					break;
				}
			}
		}
	}

	private DoorRelay() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field[] gpioField = null;
		
		if(myGarageConfig.getIsRelayInstalled()) {
			String gpioPin = myGarageConfig.getRelayGpioPin();
			if(gpioPin != null) {
				gpioField = RaspiPin.class.getDeclaredFields();
				for (int i = 0; i < gpioField.length; i++) {
					if (gpioField[i].getName().contains(gpioPin)) {
						bltoutpin = gpio.provisionDigitalOutputPin((Pin) gpioField[i].get(RaspiPin.class), "GarageDoorRelay", PinState.HIGH);
						break;
					}
				}

				if(bltoutpin == null) {
					logger.severe("Cannot set relay gpio pin - relay not functional");
				}
			}
		}
		
		if (myGarageConfig.getIsCloseMagnetInstalled()) {
			mntcloseoutpin = new MagneticSensor(myGarageConfig.getMagnetSensorClosePin());
			mntcloseoutpin.getGpioPinDigitalInput().addListener(new GpioPinListenerDigital() {
	            @Override
	            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
	            	if(logger.isLoggable(Level.FINE)) {
	            		logger.fine(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
	            	}
	                currentState = getDoorState();
	            }
	        });			
		}
		
		if (myGarageConfig.getIsOpenMagnetInstalled()) {
			try {
				mntopenoutpin = new MagneticSensor(myGarageConfig.getMagnetSensorOpenPin());
				mntopenoutpin.getGpioPinDigitalInput().addListener(new GpioPinListenerDigital() {
		            @Override
		            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		                if(logger.isLoggable(Level.FINE)) {
		                	logger.fine("--> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
		                }
		                currentState = getDoorState();
		            }
		        });
				
				
				currentState = getDoorState();

			} catch (UnknownPinName upnex) {
				logger.severe("Magnetic sensor setup not valid or absent: " + upnex.getMessage());
			}
		} else {
			currentState = DoorState.CLOSED;
		}
		
		Thread monitorThread = new Thread(new DoorMonitoring());
		monitorThread.start();		
	}
	
	public static DoorRelay getDoorRelayInstance() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		synchronized(lock) {
			if (myDoorRelay == null) {
				myDoorRelay = new DoorRelay();
			}
		}
		
		return myDoorRelay;
	}
	
	public DoorState activateRelay() throws InterruptedException {
		String payload = null;
		
		logger.info("activateRelay - called");
		
		if(myGarageConfig.getIsCloseMagnetInstalled()) {
			currentState = getDoorState();
		}
		
		if(myGarageConfig.getIsRelayInstalled()) {
			switch(currentState) {
				case CLOSED:
				case CLOSE_STOPED:
					if(logger.isLoggable(Level.FINE)) logger.fine("Door will be opened.");
					gpioTriggerAction();
					payload = new String("Change state from: " + currentState + " to: " + DoorState.OPEN_IN_PROGRESS + ".");
					currentState = DoorState.OPEN_IN_PROGRESS;
					mqttClient.publish(TopicName.GarageDoorTopicName, Qos.deliverOnceWithConfirmation, payload.getBytes());
					break;

				case CLOSE_IN_PROGRESS:
					if(logger.isLoggable(Level.FINE))
						logger.fine("Door close request will be stopped!");
					gpioTriggerAction();
					payload = new String("Change state from: " + currentState + " to: " + DoorState.CLOSE_STOPED + ".");
					currentState = DoorState.CLOSE_STOPED;
					mqttClient.publish(TopicName.GarageDoorTopicName, Qos.deliverOnceWithConfirmation, payload.getBytes());
					break;

				case OPENED:
				case OPEN_STOPED:
					if(logger.isLoggable(Level.FINE))
						logger.fine("Door will be closed");
					gpioTriggerAction();
					payload = new String("Change state from: " + currentState + " to: " + DoorState.CLOSE_IN_PROGRESS + ".");
					currentState = DoorState.CLOSE_IN_PROGRESS;
					mqttClient.publish(TopicName.GarageDoorTopicName, Qos.deliverOnceWithConfirmation, payload.getBytes());
					break;

				case OPEN_IN_PROGRESS:
					if(logger.isLoggable(Level.FINE))
						logger.fine("Door will be closed");
					currentState = DoorState.OPEN_STOPED;
					gpioTriggerAction();
					payload = new String("Change state from: " + currentState + " to: " + DoorState.OPEN_STOPED + ".");
					currentState = DoorState.OPEN_STOPED;
					mqttClient.publish(TopicName.GarageDoorTopicName, Qos.deliverOnceWithConfirmation, payload.getBytes());
					break;
				
				case UNKNOWN:
				default:
					logger.severe("No action performed - unknown door state!");
					break;
			}
		}
		
		return currentState;
	}
	
	public DoorState getDoorState() {
		DoorState nowState;
		
		boolean closed = myGarageConfig.getIsCloseMagnetInstalled() && mntcloseoutpin.isHigh();
		boolean opened = myGarageConfig.getIsOpenMagnetInstalled() && mntopenoutpin.isHigh();
		boolean moving = (myGarageConfig.getIsCloseMagnetInstalled() && mntcloseoutpin.isLow()) ||
						 (myGarageConfig.getIsOpenMagnetInstalled() && mntopenoutpin.isLow());

		if (closed) {
			nowState = DoorState.CLOSED;
		} else if (opened) {
			nowState = DoorState.OPENED;
		} else if (moving) {
			nowState = currentState;

			if (!myGarageConfig.getIsCloseMagnetInstalled() ||
				!myGarageConfig.getIsOpenMagnetInstalled()) {
				synchronized(relayMonitoring) {
					relayMonitoring.notify();
				}
			}
		} else {
			nowState = DoorState.UNKNOWN;
		}
		
		return nowState;
	}
	
	private static void gpioTriggerAction() throws InterruptedException {
		logger.info("gpioTriggerAction - called");
		
		bltoutpin.low();
		Thread.sleep(1000);
		bltoutpin.high();
		
		if (!myGarageConfig.getIsCloseMagnetInstalled() && !myGarageConfig.getIsOpenMagnetInstalled()) {
			synchronized(relayMonitoring) {
				relayMonitoring.notify();
			}
		}
	}
}