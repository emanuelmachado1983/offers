package com.emanuel.offers.infra.decision.platform.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.emanuel.offers.application.repository.DecisionPlatformRepository;
import com.emanuel.offers.infra.decision.platform.model.MessageDecision;
import com.emanuel.offers.infra.kafka.service.KafkaProducerService;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DecisionPlatformRepositoryImpl implements DecisionPlatformRepository {
	
	private final KafkaProducerService kafkaProducerService;
	
	@Value("$decision.platform.topic")
	private String topic;
	
	@Override
	public void notification(String userCode, List<Long> offers, String paymentMethod) {
		kafkaProducerService.sendMessage(topic, MessageDecision.builder().userCode(userCode).paymentMethod(paymentMethod).offers(offers).build());
		
	}

}
