package com.home.automation.common.mqtt;

public class Qos {
	public static int deliverOnceNoConfirmation = 0;
	public static int deliverAtLeastOnceWithConformation = 1;
	public static int deliverOnceWithConfirmation = 2;
	
	private Qos() {}
}
