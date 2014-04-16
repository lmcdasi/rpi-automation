package com.home.automation.common.cmds;

public class GarageDoor {
	public static final String ACTIVATE_RELAY = "ACTIVATE";
	public static final String GET_MAGNETIC_SENSOR_STATUS = "GET_MAGNETIC_SENSOR_STATUS";

	private GarageDoor() {}
	
	public static int getCmdSize() {
		return Math.max(ACTIVATE_RELAY.length(), GET_MAGNETIC_SENSOR_STATUS.length());
	}
}
