package com.emanuel.offers.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.emanuel.offers.application.errors.EmptyOffersSelectedException;
import com.emanuel.offers.application.errors.PaymentMethodError;
import com.emanuel.offers.application.errors.PaymentMethodInvalid;
import com.emanuel.offers.application.errors.UserNotValidated;
import com.emanuel.offers.application.errors.UserValidationError;
import com.emanuel.offers.application.repository.BankingEntitiesRepository;
import com.emanuel.offers.application.repository.ClientRepository;
import com.emanuel.offers.application.repository.DecisionPlatformRepository;
import com.emanuel.offers.application.repository.EmailRepository;
import com.emanuel.offers.application.repository.OfferRepository;
import com.emanuel.offers.application.repository.PaymentsRepository;
import com.emanuel.offers.domain.model.ChosenOffer;
import com.emanuel.offers.domain.model.Offer;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Builder
public class OfferService {
	
	private final OfferRepository offersRepository;
	private final ClientRepository clientRepository;
	private final PaymentsRepository paymentsRepository;
	private final DecisionPlatformRepository decisionPlatformRepository;
	private final BankingEntitiesRepository bankingEntitiesRepository;
	private final EmailRepository emailRepository;
	
	public List<Offer> getOffers() {
		return offersRepository.getOffers();		
	};
	
	public List<Offer> getOffers(Integer importance, Integer urgency, String category) {
		return offersRepository.getOffers(importance, urgency, category);
	};
	
	public void chooseOffers(String userCode, String paymentMethod, List<Long> offers) throws 
		EmptyOffersSelectedException, UserNotValidated, UserValidationError, PaymentMethodInvalid, PaymentMethodError {
		if (offers.isEmpty()) {
			throw new EmptyOffersSelectedException();
		}
		try {
			clientRepository.validateUser(userCode);
		} catch (UserNotValidated userNotValidated) {
			emailRepository.sendEmail(userCode, userNotValidated);
			throw userNotValidated;
		}  catch (Exception e) {
			sendOffersToDatabase(userCode, paymentMethod, offers, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
			emailRepository.sendEmail(userCode, e);
			throw e;
		}
		
		try {
			paymentsRepository.validatePaymentMethod(userCode, paymentMethod);
		} catch (PaymentMethodInvalid paymentMethodInvalid) {
			emailRepository.sendEmail(userCode, paymentMethodInvalid);
			throw paymentMethodInvalid;
		}  catch (Exception e) {
			sendOffersToDatabase(userCode, paymentMethod, offers, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
			emailRepository.sendEmail(userCode, e);
			throw e;
		}
		sendOffersToDatabase(userCode, paymentMethod, offers, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);

		decisionPlatformRepository.notification(userCode, offers, paymentMethod);
		
		bankingEntitiesRepository.notification(userCode, offers, paymentMethod);
	}
	
	
	private void sendOffersToDatabase(String userCode, String paymentMethod, List<Long> offers,
			Boolean validationUser, Boolean validationPaymentMethod, Boolean continueTrying) {
		offersRepository.saveOffers(ChosenOffer.builder().userCode(userCode).paymentMethod(paymentMethod).offers(offers).build() ,
				validationUser, validationPaymentMethod, continueTrying);
		
	}
	
}
