package com.emanuel.offers.infra.rest.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.emanuel.offers.application.service.OfferService;
import com.emanuel.offers.infra.rest.dto.OfferDto;
import com.emanuel.offers.infra.rest.mapper.OfferMapper;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Builder
public class OfferControllerImpl implements OfferController {
	
	private final OfferService offerService;
	private final OfferMapper offerMapper;
	

	@Override
	@GetMapping(path= "/offers")
	public ResponseEntity<List<OfferDto>> getOffers(@RequestParam(required=false) Integer importance, 
			@RequestParam(required=false) Integer urgency, @RequestParam(required=false) String category) {
		return new ResponseEntity<> (offerService.getOffers(importance, urgency, category).stream().map(x->offerMapper.toDto(x)).collect(Collectors.toList()), HttpStatus.OK);
	}
}
