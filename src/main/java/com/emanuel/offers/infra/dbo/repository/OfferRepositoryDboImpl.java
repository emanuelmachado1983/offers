package com.emanuel.offers.infra.dbo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.emanuel.offers.application.repository.OfferRepository;
import com.emanuel.offers.application.service.OfferService;
import com.emanuel.offers.domain.model.ChosenOffer;
import com.emanuel.offers.domain.model.Offer;
import com.emanuel.offers.infra.dbo.mapper.OfferEntityMapper;
import com.emanuel.offers.infra.dbo.model.OfferUserEntity;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Builder
public class OfferRepositoryDboImpl implements OfferRepository {
	
	private final OfferEntityDboRepository offerEntityDboRepository;
	private final OfferEntityMapper offerEntityMapper;
	private final OfferUserDboRepository offerUserDboRepository;
	
	private final Logger logger = LoggerFactory.getLogger(OfferService.class);
	
	@Override
	public List<Offer> getOffers() {
		logger.info("Getting all offers.");
		return offerEntityDboRepository.findAll().stream().map(x->offerEntityMapper.toDto(x)).collect(Collectors.toList());
	}

	@Override
	public List<Offer> getOffers(Integer importance, Integer urgency, String category) {
		logger.info("Getting offers with filters.");
		offerEntityDboRepository.findOffersByFilters(importance, urgency, category);
		return offerEntityDboRepository.findOffersByFilters(importance, urgency, category).stream().map(x->offerEntityMapper.toDto(x)).collect(Collectors.toList());
	}

	@Override
	public void saveOffers(ChosenOffer chosenOffers, Boolean validationUser, Boolean validationPaymentMethod, Boolean continueTrying) {
		List<OfferUserEntity> list = offerUserDboRepository.getActiveChosenOffersByUser(chosenOffers.getUserCode());
		for (Long offer :  chosenOffers.getOffers()){
			List<OfferUserEntity> valueToFind = list.stream().filter(x-> x.getIdOffer().equals(offer)).collect(Collectors.toList());
			OfferUserEntity toSave = OfferUserEntity
										.builder()
										.userCode(chosenOffers.getUserCode())
										.idOffer(offer)
										.paymentMethod(chosenOffers.getPaymentMethod())
										.date(LocalDateTime.now())
										.validationUser(validationUser)
										.validationPaymentMethod(validationPaymentMethod)
										.continueTrying(continueTrying)
										.build();
			
			if (!valueToFind.isEmpty()) {
				toSave.setId(valueToFind.get(0).getIdOffer());
				toSave.setDate(valueToFind.get(0).getDate());
			}
			
			offerUserDboRepository.save(toSave);
							
		}
		
	}

	@Override
	public List<ChosenOffer> getChosenOffersToTryAgain() {
		List<OfferUserEntity> offersUsers = offerUserDboRepository.getOffersToTryAgain();
		
		return offersUsers.stream().map(x-> ChosenOffer.builder()
				.userCode(x.getUserCode())
				.paymentMethod(x.getPaymentMethod())
				.offers(List.of(x.getIdOffer()))  //TODO: here it would be better to put all the offers in the same list for the user.
				.build()).collect(Collectors.toList());
	}

	@Override
	public List<ChosenOffer> setOffersThatToMuchTimeTryingToEnd() {
		List<OfferUserEntity> offersUsers = offerUserDboRepository.getOffersThatAreToMuchTimeTrying(LocalDateTime.now().minusHours(2)); //Here I get the offers that are two hours trying.
		for (OfferUserEntity offer :  offersUsers){
			OfferUserEntity valueToSave = offerUserDboRepository.findById(offer.getId()).get();
			valueToSave.setContinueTrying(Boolean.FALSE);
		}
		
		return offersUsers.stream().map(x-> ChosenOffer.builder()
				.userCode(x.getUserCode())
				.paymentMethod(x.getPaymentMethod())
				.offers(List.of(x.getIdOffer()))  //TODO: here it would be better to put all the offers in the same list for the user.
				.build()).collect(Collectors.toList());
		
	}

	
}
