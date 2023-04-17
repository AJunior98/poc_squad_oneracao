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
public class ProposalDetailsDTO {

    private Long proposalId;

    private String customer;

    private BigDecimal priceTonne;

    private String product;

    private Integer tonnes;

    private String country;

    private Integer proposalValidityDays;

}
