package com.emanuel.offers.application.errors;

public class PaymentMethodInvalid extends Exception {

	private static final long serialVersionUID = 1L;

	public PaymentMethodInvalid(String message) {
		super(message);
	}
}
