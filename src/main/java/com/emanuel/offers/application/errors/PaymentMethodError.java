package com.emanuel.offers.application.errors;

public class PaymentMethodError extends Exception {

	private static final long serialVersionUID = 1L;

	public PaymentMethodError(String message) {
		super(message);
	}
}
