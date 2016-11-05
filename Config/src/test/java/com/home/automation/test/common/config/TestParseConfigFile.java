package com.home.automation.test.common.config;

import java.util.logging.Logger;

import com.home.automation.common.config.GarageConfiguration;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestParseConfigFile extends TestCase {
	private static Logger logger = Logger.getLogger(TestParseConfigFile.class.getCanonicalName());

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TestParseConfigFile( String testName )
    {
        super( testName );
    }
    
    /**
     * @return the suite of tests being tested
     */
	public static Test suite() {
		return new TestSuite(TestParseConfigFile.class);
	}

    /**
     * Rigourous Test :-)
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public void testApp() throws IllegalArgumentException, IllegalAccessException
    {
    	GarageConfiguration myGarageConfig = GarageConfiguration.getGarageConfiguration();
    	
    	logger.info("====== GARAGE SETUP =====");
    	logger.info("Bluetooth base UUID:  " + myGarageConfig.getUUID());
    	logger.info("Close Magnetic sensor installed: " + myGarageConfig.getIsCloseMagnetInstalled());
    	if(myGarageConfig.getIsCloseMagnetInstalled()) {
    		logger.info("GPIO PIN used for Close Mangnetic Sensor: " + myGarageConfig.getMagnetSensorClosePin());
    	}
    	logger.info("Open Magnetic sensor installed: " + myGarageConfig.getIsOpenMagnetInstalled());
    	if(myGarageConfig.getIsOpenMagnetInstalled()) {
    		logger.info("GPIO PIN used for Open Mangnetic Sensor: " + myGarageConfig.getMagnetSensorOpenPin());
    	}
    	logger.info("Door Relay installed: " + myGarageConfig.getIsRelayInstalled());
    	if(myGarageConfig.getIsRelayInstalled()) {
    		logger.info("GPIO PIN used for Door Relay Sensor: " + myGarageConfig.getRelayGpioPin());
    	}
    	logger.info("Door delay movement timer: " + myGarageConfig.getDoorMovementTimer());
    	logger.info("SSL port no: " + myGarageConfig.getSslPortNo());
    }
}
