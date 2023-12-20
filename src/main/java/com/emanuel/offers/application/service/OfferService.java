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
		
		//First: Validate the user
		try {
			clientRepository.validateUser(userCode);
		} catch (UserNotValidated userNotValidated) {
			emailRepository.sendEmail(userCode, userNotValidated);
			throw userNotValidated;
		}  catch (Exception e) {
			//if there is an error in the validation of the user I save it in the database, but with the flags in false, and the flag of continue trying in true.
			//the job will try to repeat this method every 30 minutes for two hours.
			sendOffersToDatabase(userCode, paymentMethod, offers, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
			emailRepository.sendEmail(userCode, e);
			throw e;
		}
		
		//Then: Validate the payment method
		try {
			paymentsRepository.validatePaymentMethod(userCode, paymentMethod);
		} catch (PaymentMethodInvalid paymentMethodInvalid) {
			emailRepository.sendEmail(userCode, paymentMethodInvalid);
			throw paymentMethodInvalid;
		}  catch (Exception e) {
			//if there is an error in the validation I save it in the database, but with the flag in false, and with the flag of continue trying in true.
			//the job will try to repeat this method every 30 minutes for two hours.
			sendOffersToDatabase(userCode, paymentMethod, offers, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
			emailRepository.sendEmail(userCode, e);
			throw e;
		}
		
		//Then send the offers to the database
		sendOffersToDatabase(userCode, paymentMethod, offers, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);

		//Then send the notification to the Platform Decision
		decisionPlatformRepository.notification(userCode, offers, paymentMethod);
		
		//Then send the notificacion to the banking entities.
		bankingEntitiesRepository.notification(userCode, offers, paymentMethod);
	}
	
	
	private void sendOffersToDatabase(String userCode, String paymentMethod, List<Long> offers,
			Boolean validationUser, Boolean validationPaymentMethod, Boolean continueTrying) {
		offersRepository.saveOffers(ChosenOffer.builder().userCode(userCode).paymentMethod(paymentMethod).offers(offers).build() ,
				validationUser, validationPaymentMethod, continueTrying);
		
	}
	
}
