package com.home.automation.cloud.auth;

public class AuthenticateDevice {
	private final String token;
	
	public AuthenticateDevice(String accessToken) {
		this.token = accessToken;
	}
	
	public AuthenticateState authDevice() {
		//curl -v -H 'Authorization: Bearer ' https://developer.amazon.com/api/appstore/beta_v1/apps
		return AuthenticateState.SUCCESS;
	}
}
