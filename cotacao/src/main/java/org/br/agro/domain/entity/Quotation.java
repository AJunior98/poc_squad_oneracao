package org.br.agro.domain.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Getter
@Setter
@ToString
public class Quotation {

    private UUID id;

    private LocalDateTime date;

    private BigDecimal currencyPrice;

    private String pctChange;

    private String pair;
}
