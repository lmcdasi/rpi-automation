package com.home.automation.test.garage;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import com.home.automation.common.config.GarageConfiguration;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class TestGpioReflection extends TestCase
{
	private static Logger logger = Logger.getLogger(TestGpioReflection.class.getCanonicalName());
	
	private static GarageConfiguration myGarageConfig = GarageConfiguration.getGarageConfiguration();
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TestGpioReflection( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TestGpioReflection.class );
    }

    /**
     * Rigourous Test :-)
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public void testApp() throws IllegalArgumentException, IllegalAccessException
    {
		Field[] gpioField = null;
		
		if(myGarageConfig.getIsRelayInstalled()) {
			String gpioPin = myGarageConfig.getRelayGpioPin();
			if(gpioPin != null) {
				gpioField = RaspiPin.class.getDeclaredFields();
				for (int i = 0; i < gpioField.length; i++) {
					if (gpioField[i].getName().contains(gpioPin)) {
						//bltoutpin = gpio.provisionDigitalOutputPin((Pin) gpioField[i].get(RaspiPin.class), "GarageDoorRelay", PinState.HIGH);
						Pin testPin = (Pin) gpioField[i].get(RaspiPin.class);
						logger.info("Reflection return address of GPIO pin: " + gpioPin + " is: " + System.identityHashCode(testPin));
						logger.info("RPIPIN Gpio " + gpioPin + " address is: " + System.identityHashCode(RaspiPin.GPIO_07));
						
						assertTrue(System.identityHashCode(RaspiPin.GPIO_07) == System.identityHashCode(testPin));
						break;
					}
				}
			}
		}
		
		if(myGarageConfig.getIsCloseMagnetInstalled()) {
			String closeGpioPin = myGarageConfig.getMagnetSensorClosePin();
			if(closeGpioPin != null) {
				gpioField = RaspiPin.class.getDeclaredFields();
				for (int i = 0; i < gpioField.length; i++) {
					if (gpioField[i].getName().contains(closeGpioPin)) {
						//mntopenoutpin = gpio.provisionDigitalInputPin((Pin) gpioField[i].get(RaspiPin.class), "MagneticSensorOpen", PinPullResistance.PULL_DOWN);
						Pin testPin = (Pin) gpioField[i].get(RaspiPin.class);
						logger.info("Reflection return address of GPIO pin: " + closeGpioPin + " is: " + System.identityHashCode(testPin));
						logger.info("RPIPIN Gpio " + RaspiPin.GPIO_11.getName() + " address is: " + System.identityHashCode(RaspiPin.GPIO_11));

						assertTrue(System.identityHashCode(RaspiPin.GPIO_11) == System.identityHashCode(testPin));
					}
				}
			}
		}
		
		if(myGarageConfig.getIsOpenMagnetInstalled()) {
			String openGpioPin = myGarageConfig.getMagnetSensorOpenPin();
			if(openGpioPin != null) {
				gpioField = RaspiPin.class.getDeclaredFields();
				for (int i = 0; i < gpioField.length; i++) {
					if (gpioField[i].getName().contains(openGpioPin)) {
						//mntopenoutpin = gpio.provisionDigitalInputPin((Pin) gpioField[i].get(RaspiPin.class), "MagneticSensorOpen", PinPullResistance.PULL_UP);
						Pin testPin = (Pin) gpioField[i].get(RaspiPin.class);
						logger.info("Reflection return address of GPIO pin: " + openGpioPin + " is: " + System.identityHashCode(testPin));
						logger.info("RPIPIN Gpio " + RaspiPin.GPIO_13.getName() + " address is: " + System.identityHashCode(RaspiPin.GPIO_13));
						
						assertTrue(System.identityHashCode(RaspiPin.GPIO_13) == System.identityHashCode(testPin));
					}
				}
			}
		}

        assertTrue( true );
    }
}
