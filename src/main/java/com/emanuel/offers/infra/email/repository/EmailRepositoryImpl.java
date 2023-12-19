package com.emanuel.offers.infra.email.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.emanuel.offers.application.repository.EmailRepository;
import com.emanuel.offers.infra.email.model.Email;
import com.emanuel.offers.infra.kafka.service.KafkaProducerService;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmailRepositoryImpl implements EmailRepository {
	
	private final KafkaProducerService kafkaProducerService;
	
	@Value("$email.topic")
	private String topic;

	@Override
	public void sendEmail(String userCode, Object message) {
		kafkaProducerService.sendMessage(topic, Email.builder().userCode(userCode).objectToSend(message).build());
		
	}

}
