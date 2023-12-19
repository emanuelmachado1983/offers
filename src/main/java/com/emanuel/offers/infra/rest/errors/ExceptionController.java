package com.emanuel.offers.infra.rest.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.emanuel.offers.application.errors.EmptyOffersSelectedException;
import com.emanuel.offers.application.errors.ErrorMessageDto;
import com.emanuel.offers.application.errors.PaymentMethodError;
import com.emanuel.offers.application.errors.PaymentMethodInvalid;
import com.emanuel.offers.application.errors.UserNotValidated;
import com.emanuel.offers.application.errors.UserValidationError;
import com.emanuel.offers.application.service.OfferService;

@ControllerAdvice
public class ExceptionController {
	private final Logger logger = LoggerFactory.getLogger(OfferService.class);
	
    @ExceptionHandler(UserNotValidated.class)
    @ResponseStatus(value=HttpStatus.FORBIDDEN)
    public ErrorMessageDto userNotValidated(Exception e) {
    	logger.error(e.getMessage());
        return getMessage("The user is not valid");
    }
    
    @ExceptionHandler(UserValidationError.class)
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageDto userValidationError(Exception e) {
    	logger.error(e.getMessage());
    	return getMessage("There was an error validating the user. During the next two hours we will send you an email with the result of your validation.");
    }
    
    @ExceptionHandler(PaymentMethodInvalid.class)
    @ResponseStatus(value=HttpStatus.FORBIDDEN)
    public ErrorMessageDto paymentMethodInvalid(Exception e) {
    	logger.error(e.getMessage());
        return getMessage("The payment method is not valid");
    }
    
    @ExceptionHandler(PaymentMethodError.class)
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageDto paymentMethodError(Exception e) {
    	logger.error(e.getMessage());
    	return getMessage("There was an error validating the payment method. During the next two hours we will send you an email with the result of your validation.");
    }
    
    @ExceptionHandler(EmptyOffersSelectedException.class)
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    public ErrorMessageDto emptyOffersSelectedException(Exception e) {
    	return getMessage("Don´t send an empty array of offers");
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(Exception e) {
        return new ResponseEntity<>("There is something wrong in your request, check the documentation.", HttpStatus.BAD_REQUEST);
    }
    
    private ErrorMessageDto getMessage(String message) {
    	return ErrorMessageDto.builder().errorMessage(message).build();
    }
}
