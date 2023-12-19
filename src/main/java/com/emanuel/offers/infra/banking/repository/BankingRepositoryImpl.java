package com.emanuel.offers.infra.banking.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.emanuel.offers.application.repository.BankingEntitiesRepository;
import com.emanuel.offers.infra.banking.model.BankingMessage;
import com.emanuel.offers.infra.kafka.service.KafkaProducerService;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BankingRepositoryImpl implements BankingEntitiesRepository {
	
	private final KafkaProducerService kafkaProducerService;
	
	@Value("$decision.banking.entities.topic")
	private String topic;
	
	@Override
	public void notification(String userCode, List<Long> offers, String paymentMethod) {
		kafkaProducerService.sendMessage(topic, BankingMessage.builder().userCode(userCode).paymentMethod(paymentMethod).offers(offers).build());
	}

}
