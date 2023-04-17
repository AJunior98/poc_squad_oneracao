package org.br.agro.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.br.agro.client.CurrencyPriceClient;
import org.br.agro.dto.CurrencyPriceDTO;
import org.br.agro.dto.USDBRL;
import org.br.agro.utils.ConversorObjUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.when;

@QuarkusTest
public class QuotationServiceImplTest {

    @InjectMock
    @RestClient
    CurrencyPriceClient currencyPriceClient;

    private CurrencyPriceDTO currencyPriceDTO;

    @BeforeEach
    void setUp() {
        currencyPriceDTO = new CurrencyPriceDTO();
        currencyPriceDTO.setUSDBRL((USDBRL) ConversorObjUtils.jsonToObject(ConversorObjUtils.getJsonCotacaoInfo(), USDBRL.class));
    }

    @Test
    public void testGetCurrencyPrice() {
        when(currencyPriceClient.getPriceByPair("USD-BRL")).thenReturn(currencyPriceDTO);
        Assertions.assertEquals("4.9065", currencyPriceDTO.getUSDBRL().getBid());
    }

}