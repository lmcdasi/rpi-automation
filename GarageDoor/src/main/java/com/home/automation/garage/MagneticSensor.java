package com.home.automation.garage;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class MagneticSensor {
	private static Logger logger = Logger.getLogger(MagneticSensor.class.getCanonicalName());

	private static GpioController gpio = GpioFactory.getInstance();

	private static GpioPinDigitalInput mntsensorpin = null;

	public MagneticSensor(String gpioPin) throws IllegalArgumentException, IllegalAccessException, UnknownPinName {
		Field[] gpioField = null;

		if(gpioPin != null) {
			gpioField = RaspiPin.class.getDeclaredFields();
			for (int i = 0; i < gpioField.length; i++) {
				if (gpioField[i].getName().contains(gpioPin)) {
					if(logger.isLoggable(Level.FINE)) {
						logger.fine("Setup GPIO PIN: " + gpioPin + " as magnetic sensor: MagneticSensor_" + i);
					}
					mntsensorpin = gpio.provisionDigitalInputPin((Pin) gpioField[i].get(RaspiPin.class), "MagneticSensor" + i, PinPullResistance.PULL_UP);
				}
			}
			
			if(mntsensorpin == null) {
				logger.severe("Cannot set magnetic gpio pin - magnetic sensor(s) non functional");
				throw new UnknownPinName("Unknown pin: " + gpioPin + " in configuration file");
			}
		}
	}
	
	public GpioPinDigitalInput getGpioPinDigitalInput() {
		return mntsensorpin;
	}
	
	public boolean isHigh() {
		return mntsensorpin.isHigh();
	}
	
	public boolean isLow() {
		return mntsensorpin.isLow();
	}

	public PinState getState() {
		return mntsensorpin.getState();
	}
	
	public boolean isState(PinState state) {
		return mntsensorpin.isState(state);
	}
}