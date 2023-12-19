package com.emanuel.offers.application.repository;

import java.util.List;

public interface DecisionPlatformRepository {
	void notification(String userCode, List<Long> offers, String paymentMethod);
}
