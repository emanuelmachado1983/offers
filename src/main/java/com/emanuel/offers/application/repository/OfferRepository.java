package com.emanuel.offers.application.repository;

import java.util.List;

import com.emanuel.offers.domain.model.ChosenOffer;
import com.emanuel.offers.domain.model.Offer;

public interface OfferRepository {
	List<Offer> getOffers();
	
	List<Offer> getOffers(Integer importance, Integer urgency, String category);
	
	public void saveOffers(ChosenOffer chosenOffers, Boolean validationUser, Boolean validationPaymentMethod, Boolean continueTrying);
	
	List<ChosenOffer> getChosenOffersToTryAgain();
	
	List<ChosenOffer> setOffersThatToMuchTimeTryingToEnd();
}
