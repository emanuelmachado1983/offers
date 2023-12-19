package com.emanuel.offers.infra.kafka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.emanuel.offers.infra.decision.platform.model.MessageDecision;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaProducerService {
	private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, Object message) {
        kafkaTemplate.send(topic, toJson(message));
    }
    
    private static String toJson(Object miObjeto) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(miObjeto);
        } catch (JsonProcessingException e) {
            //TODO: should contemplate this error. For motive of time I will let this for later.
            e.printStackTrace();
            return null;
        }
    }
    
}
