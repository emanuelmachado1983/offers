package com.emanuel.offers.infra.rest.dto;

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
public class OfferDto {
	Integer id;
    private String name;
    private Integer importance;
    private Integer duration;
    private Integer urgency;
    private String category;
}
