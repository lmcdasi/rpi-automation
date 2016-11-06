package com.home.automation.common.mqtt.client;

import java.sql.Timestamp;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.home.automation.common.config.GarageConfiguration;

public class AsyncMqttClient implements MqttCallback {
	private static final Logger logger = Logger.getLogger(AsyncMqttClient.class.getCanonicalName());
	private static GarageConfiguration myGarageConfig = GarageConfiguration.getGarageConfiguration();
	
	private static AsyncMqttClient __instance = new AsyncMqttClient();
	
	private MqttAsyncClient myClient = null;
	private MqttConnectOptions myClientOptions = null;
	
	private AsyncMqttClient() {}
	
	public static AsyncMqttClient getInstance() {
		return __instance;
	}
	
	public boolean connectAsync() {
		try {
			if(logger.isDebugEnabled()) {
	        	logger.debug("Connecting to " + myGarageConfig.getServerURI() + " with conf Id: " + myGarageConfig.getMqttClientId());
	        }
			
			myClient = new MqttAsyncClient(myGarageConfig.getServerURI(), myGarageConfig.getMqttClientId());
			
	        if(logger.isDebugEnabled()) {
	        	logger.debug("With client ID " + myClient.getClientId());
	        }
	        
	        myClient.connect();		
			myClient.setCallback(this);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public void publish(String topicName, int qos, byte[] payload) {
        String time = new Timestamp(System.currentTimeMillis()).toString();
        logger.info("Publishing at: " + time + " to topic \"" + topicName + "\" qos " + qos);
        
        // Construct the message to send
        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);
        
        try {
			myClient.publish(topicName, message, null, null);
		} catch (MqttException ex) {
			logger.log(Level.FATAL, "MQTT publis exception: ", ex);
		}
    }

	@Override
	public void connectionLost(Throwable clex) {
		logger.fatal("Connection Lost Exception: ", clex);
		
		if(!myClient.isConnected()) {
			try {
				myClient.connect();
			} catch (MqttException e) {
				logger.fatal("Unable to reconnect - mqtt services are down: ", e);
			}
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		try {
            logger.info("Delivery complete callback: Publish Completed " + token.getMessage());
		} catch (Exception ex) {
            logger.fatal("Exception in delivery complete callback: " + ex);
		}	
	}

	@Override
	public void messageArrived(String arg0, MqttMessage message) throws Exception {
		String time = new Timestamp(System.currentTimeMillis()).toString();
		if(logger.isDebugEnabled()) {
			logger.debug("Receive message from MQTT server at: " + time);
			logger.debug("Content: " + message.getPayload().toString());
		}
	}
}
