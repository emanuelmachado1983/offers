package com.emanuel.offers.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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

public class OfferServiceTest {
	
	OfferService offerService;
	
	private OfferRepository offersRepository = Mockito.mock(OfferRepository.class);
	private ClientRepository clientRepository = Mockito.mock(ClientRepository.class);
	private PaymentsRepository paymentsRepository = Mockito.mock(PaymentsRepository.class);
	private DecisionPlatformRepository decisionPlatformRepository = Mockito.mock(DecisionPlatformRepository.class);
	private BankingEntitiesRepository bankingEntitiesRepository = Mockito.mock(BankingEntitiesRepository.class);
	private EmailRepository emailRepository = Mockito.mock(EmailRepository.class);
	
	@BeforeEach
	void setup() {
		offerService = OfferService.builder()
				.offersRepository(offersRepository)
				.clientRepository(clientRepository)
				.paymentsRepository(paymentsRepository)
				.decisionPlatformRepository(decisionPlatformRepository)
				.bankingEntitiesRepository(bankingEntitiesRepository)
				.emailRepository(emailRepository)
				.build();
	}
	
	@Test
	void get_offers() {
		offerService.getOffers();
		Mockito.verify(offersRepository, Mockito.times(1)).getOffers();
	}
	
	@Test
	void get_offers__with_filters() {
		offerService.getOffers(1,1,"Sports");
		Mockito.verify(offersRepository, Mockito.times(1)).getOffers(1,1,"Sports");
	}
	
	@Test
	void choose_offers__no_offer_choosed() throws EmptyOffersSelectedException, UserNotValidated, UserValidationError, PaymentMethodInvalid, PaymentMethodError {
		assertThrows(EmptyOffersSelectedException.class, ()->{ offerService.chooseOffers("userCode", "DEBIT", new ArrayList<>());});
	}
	
	@Test
	void choose_offers__user_not_valid() throws EmptyOffersSelectedException, UserNotValidated, UserValidationError, PaymentMethodInvalid, PaymentMethodError {
		Mockito.doThrow(UserNotValidated.class).when(clientRepository).validateUser("userCode");
		
		assertThrows(UserNotValidated.class, ()->{ offerService.chooseOffers("userCode", "DEBIT", mockOffers());});
		
		Mockito.verify(emailRepository, Mockito.times(1)).sendEmail(Mockito.anyString(), Mockito.any());
	}
	
	@Test
	void choose_offers__error_validating_user__after_that_saving_the_offers_to_the_database() throws EmptyOffersSelectedException, UserNotValidated, UserValidationError, PaymentMethodInvalid, PaymentMethodError {
		List<Long> offers = mockOffers();
		Mockito.doThrow(UserValidationError.class).when(clientRepository).validateUser("userCode");
		
		assertThrows(UserValidationError.class, ()->{ offerService.chooseOffers("userCode", "DEBIT", offers);});
		
		Mockito.verify(emailRepository, Mockito.times(1)).sendEmail(Mockito.anyString(), Mockito.any());
		Mockito.verify(offersRepository, Mockito.times(1)).saveOffers(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
	}
	
	@Test
	void choose_offers__payment_not_valid() throws EmptyOffersSelectedException, UserNotValidated, UserValidationError, PaymentMethodInvalid, PaymentMethodError {
		Mockito.doThrow(PaymentMethodInvalid.class).when(paymentsRepository).validatePaymentMethod("userCode", "DEBIT");
		
		assertThrows(PaymentMethodInvalid.class, ()->{ offerService.chooseOffers("userCode", "DEBIT", mockOffers());});
		
		Mockito.verify(emailRepository, Mockito.times(1)).sendEmail(Mockito.anyString(), Mockito.any());
	}
	
	@Test
	void choose_offers__error_payments__after_that_saving_the_offers_to_the_database() throws EmptyOffersSelectedException, UserNotValidated, UserValidationError, PaymentMethodInvalid, PaymentMethodError {
		List<Long> offers = mockOffers();
		Mockito.doThrow(PaymentMethodError.class).when(paymentsRepository).validatePaymentMethod("userCode", "DEBIT");
		
		assertThrows(PaymentMethodError.class, ()->{ offerService.chooseOffers("userCode", "DEBIT", offers);});
		
		Mockito.verify(emailRepository, Mockito.times(1)).sendEmail(Mockito.anyString(), Mockito.any());
		Mockito.verify(offersRepository, Mockito.times(1)).saveOffers(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
	}
	
	
	@Test
	void choose_offers__sent_to_database_ok() throws EmptyOffersSelectedException, UserNotValidated, UserValidationError, PaymentMethodInvalid, PaymentMethodError {
		offerService.chooseOffers("userCode", "DEBIT", mockOffers());
		
		Mockito.verify(offersRepository, Mockito.times(1)).saveOffers(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
		Mockito.verify(decisionPlatformRepository, Mockito.times(1)).notification(Mockito.anyString(), Mockito.anyList(), Mockito.anyString());
		Mockito.verify(bankingEntitiesRepository, Mockito.times(1)).notification(Mockito.anyString(), Mockito.anyList(), Mockito.anyString());
		
	}
	
	
	private List<Long> mockOffers() {
		return List.of(1L ,2L ,3L);
	}
	
}
