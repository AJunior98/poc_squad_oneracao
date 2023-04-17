package org.br.agro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyPriceDTO {

    public USDBRL USDBRL;

}
