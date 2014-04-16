package com.home.automation.garage;

public class UnknownPinName extends RuntimeException {
	private static final long serialVersionUID = -4201477742510959681L;

	public UnknownPinName() {
		super();
	}

	public UnknownPinName(String message) {
		super(message);
	}

	public UnknownPinName(Throwable cause) {
		super(cause);
	}

	public UnknownPinName(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownPinName(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
