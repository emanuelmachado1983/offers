package com.emanuel.offers.infra.rest.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.emanuel.offers.infra.rest.dto.OfferDto;

public interface OfferController {
	ResponseEntity<List<OfferDto>> getOffers(Integer importance, Integer urgency, String category);
}
