package com.emanuel.offers.application.errors;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorMessageDto {
	private String errorMessage;
}	
