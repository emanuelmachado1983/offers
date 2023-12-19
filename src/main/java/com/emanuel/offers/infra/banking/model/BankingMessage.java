package com.emanuel.offers.infra.banking.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BankingMessage implements Serializable {

	private static final long serialVersionUID = 10001L;

	String userCode;
	String paymentMethod;
	List<Long> offers;
}
