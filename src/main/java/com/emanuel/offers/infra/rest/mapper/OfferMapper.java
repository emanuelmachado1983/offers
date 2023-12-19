package com.emanuel.offers.infra.rest.mapper;

import org.mapstruct.Mapper;

import com.emanuel.offers.domain.model.Offer;
import com.emanuel.offers.infra.rest.dto.OfferDto;

@Mapper(componentModel = "spring")
public interface OfferMapper {
	OfferDto toDto(Offer offer);
	
	Offer toDomain(OfferDto offerDto);
}
