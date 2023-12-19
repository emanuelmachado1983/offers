package com.emanuel.offers.application.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.emanuel.offers.application.repository.EmailRepository;
import com.emanuel.offers.application.repository.OfferRepository;
import com.emanuel.offers.domain.model.ChosenOffer;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Builder
public class JobService {
	
	private final OfferRepository offersRepository;
	private final EmailRepository emailRepository;
	private final OfferService offerService;
	
	
	@Scheduled(cron="${schedule.repeat}")
	public void doJobToTryOffersAgain() {
		
		List<ChosenOffer> chosenOffersThatWhereToMuchTimeTrying = offersRepository.setOffersThatToMuchTimeTryingToEnd();
		for (ChosenOffer aux: chosenOffersThatWhereToMuchTimeTrying) {
			HashMap<String,String> message = new HashMap<>();
			message.put("message", "your offer couldn't be validated, please try again."); //TODO: look for the offer to do a better message.
			emailRepository.sendEmail(aux.getUserCode(), message);
		}
		
		List<ChosenOffer> chosenOffers = offersRepository.getChosenOffersToTryAgain();
		for (ChosenOffer chosenOffer: chosenOffers) {
			try {
				offerService.chooseOffers(chosenOffer.getUserCode(), chosenOffer.getPaymentMethod(), chosenOffer.getOffers());
			} catch(Exception e) {
				continue;
			}
			
			HashMap<String,String> message = new HashMap<>();
			message.put("message", "your offer has been made correctly."); //TODO: look for the offer to do a better message.
			emailRepository.sendEmail(chosenOffer.getUserCode(), message);
		}
	}
	
	
}
