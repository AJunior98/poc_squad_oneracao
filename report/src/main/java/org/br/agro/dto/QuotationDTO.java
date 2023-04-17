package org.br.agro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@Jacksonized
public class QuotationDTO {

    private Date date;

    private BigDecimal currencyPrice;

}
