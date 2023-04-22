package org.br.agro.domain.entity;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Getter
@Setter
@ToString
public class Quotation {

    private UUID id;

    private Date date;

    private BigDecimal currencyPrice;

    private String pctChange;

    private String pair;
}
