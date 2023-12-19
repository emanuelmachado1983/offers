package com.emanuel.offers.application.repository;

public interface EmailRepository {
	void sendEmail (String userCode, Object message);
}
