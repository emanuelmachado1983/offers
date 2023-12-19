package com.emanuel.offers.application.repository;

import com.emanuel.offers.application.errors.PaymentMethodError;
import com.emanuel.offers.application.errors.PaymentMethodInvalid;

public interface PaymentsRepository {
	void validatePaymentMethod(String userCode, String paymentMethod) throws PaymentMethodInvalid, PaymentMethodError;
}
