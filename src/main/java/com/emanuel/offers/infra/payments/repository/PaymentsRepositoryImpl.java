package com.emanuel.offers.infra.payments.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import com.emanuel.offers.application.errors.PaymentMethodError;
import com.emanuel.offers.application.errors.PaymentMethodInvalid;
import com.emanuel.offers.application.repository.PaymentsRepository;
import com.emanuel.offers.application.service.OfferService;
import com.emanuel.offers.infra.client.model.ResultValidationClient;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentsRepositoryImpl implements PaymentsRepository {
	@Autowired
	WebClient webClient;
	
	@Value("$payments.uri")
	private String url;
	
	private final Logger logger = LoggerFactory.getLogger(OfferService.class);

	@Override
	public void validatePaymentMethod(String userCode, String paymentMethod)
			throws PaymentMethodInvalid, PaymentMethodError {
		try {
			logger.info("Validating payment method ".concat(userCode));
			
			ResultValidationClient response = webClient.get().uri(url).retrieve().bodyToMono(ResultValidationClient.class).block();
			if (!"YES".equalsIgnoreCase(response.getCode())) {
				throw new PaymentMethodInvalid("User is not valid for this payment method.");
			}
		} catch (Exception e) {
			throw new PaymentMethodError("Error of payment method for user: ".concat(userCode).concat(" \n").concat(e.getMessage()));
		}
	}
}
