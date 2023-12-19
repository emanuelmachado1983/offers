package com.emanuel.offers.application.service;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.emanuel.offers.application.repository.EmailRepository;
import com.emanuel.offers.application.repository.OfferRepository;
import com.emanuel.offers.domain.model.ChosenOffer;

public class JobServiceTest {
	JobService jobService;
	
	private OfferRepository offersRepository = Mockito.mock(OfferRepository.class);
	private EmailRepository emailRepository = Mockito.mock(EmailRepository.class);
	private OfferService offerService = Mockito.mock(OfferService.class);
	
	@BeforeEach
	void setup() {
		jobService = JobService.builder()
			.offersRepository(offersRepository)
			.emailRepository(emailRepository)
			.offerService(offerService)
			.build();
	}
	
	@Test
	void try_offers_again() {
		Mockito.when(offersRepository.setOffersThatToMuchTimeTryingToEnd()).thenReturn(
				List.of(ChosenOffer.builder()
							.userCode("userCode")
							.paymentMethod("DEBIT")
							.offers(List.of(1L))
							.build()
						)
				);
		
		Mockito.when(offersRepository.getChosenOffersToTryAgain()).thenReturn(
				List.of(ChosenOffer.builder()
							.userCode("userCode")
							.paymentMethod("DEBIT")
							.offers(List.of(1L))
							.build()
						)
				);
		
		jobService.doJobToTryOffersAgain();
		
		Mockito.verify(emailRepository, Mockito.times(2)).sendEmail(Mockito.anyString(), Mockito.any());
		
	}
}
