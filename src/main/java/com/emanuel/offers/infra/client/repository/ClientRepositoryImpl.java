package com.emanuel.offers.infra.client.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import com.emanuel.offers.application.errors.UserNotValidated;
import com.emanuel.offers.application.errors.UserValidationError;
import com.emanuel.offers.application.repository.ClientRepository;
import com.emanuel.offers.application.service.OfferService;
import com.emanuel.offers.infra.client.model.ResultValidationClient;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {
	@Autowired
	@Qualifier("ClientRepository")
	WebClient webClient;
	
	@Value("$client.uri")
	private String url;
	
	private final Logger logger = LoggerFactory.getLogger(OfferService.class);
	
	@Override
	public void validateUser(String userCode) throws UserNotValidated, UserValidationError {
		try {
			logger.info("Validating user ".concat(userCode));
			ResultValidationClient response = webClient.get().uri(url).retrieve().bodyToMono(ResultValidationClient.class).block();
			if (!"YES".equalsIgnoreCase(response.getCode())) {
				throw new UserNotValidated("User not validated ".concat(userCode));
			}
		} catch (Exception e) {
			throw new UserValidationError("Error with user ".concat(userCode).concat(" \n").concat(e.getMessage()));
		}
	}
	
}
