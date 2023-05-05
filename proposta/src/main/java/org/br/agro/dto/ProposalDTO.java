package org.br.agro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class ProposalDTO {

    private Long proposalId;

    private String customer;

    private BigDecimal priceTonne;

    private String product;

}
