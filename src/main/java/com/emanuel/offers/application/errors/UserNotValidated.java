package com.emanuel.offers.application.errors;

public class UserNotValidated extends Exception {

	private static final long serialVersionUID = 1L;

	public UserNotValidated(String message) {
		super(message);
	}
}
