package com.emanuel.offers.infra.dbo.mapper;

import org.mapstruct.Mapper;

import com.emanuel.offers.domain.model.Offer;
import com.emanuel.offers.infra.dbo.model.OfferEntity;

@Mapper(componentModel = "spring")
public interface OfferEntityMapper {
	Offer toDto(OfferEntity offerEntity);
	
	OfferEntity toEntity(Offer offer);
}
