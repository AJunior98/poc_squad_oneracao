package org.br.agro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@Jacksonized
public class OpportunityDTO {

    private Long proposalId;

    private String customer;

    private String product;

    private BigDecimal priceTonne;

    private BigDecimal lastDollarQuotation;

}
