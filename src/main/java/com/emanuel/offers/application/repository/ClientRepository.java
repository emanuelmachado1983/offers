package com.emanuel.offers.application.repository;

import com.emanuel.offers.application.errors.UserNotValidated;
import com.emanuel.offers.application.errors.UserValidationError;

public interface ClientRepository {
	void validateUser(String userCode) throws UserNotValidated, UserValidationError;
}
