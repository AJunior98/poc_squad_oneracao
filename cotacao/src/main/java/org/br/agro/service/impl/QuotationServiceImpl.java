package org.br.agro.service.impl;

import org.br.agro.client.CurrencyPriceClient;
import org.br.agro.dto.CurrencyPriceDTO;
import org.br.agro.dto.QuotationDTO;
import org.br.agro.entity.QuotationEntity;
import org.br.agro.message.KafkaEvents;
import org.br.agro.repository.QuotationRepository;
import org.br.agro.service.QuotationService;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class QuotationServiceImpl implements QuotationService {

    @Inject
    @RestClient
    CurrencyPriceClient currencyPriceClient;

    @Inject
    QuotationRepository quotationRepository;

    @Inject
    KafkaEvents kafkaEvents;

    @Override
    public void getCurrencyPrice() {
        CurrencyPriceDTO currencyPriceInfo = currencyPriceClient.getPriceByPair("USD-BRL");

        if(updateCurrentInfoPrice(currencyPriceInfo)){
            kafkaEvents.sendNewKafkaEvent(QuotationDTO
                    .builder()
                    .currencyPrice(new BigDecimal(currencyPriceInfo.getUSDBRL().getBid()))
                    .date(new Date())
                    .build());
        }
    }

    @Override
    public void cleanDataBase(){
        quotationRepository.deleteAll();
    }

    private boolean updateCurrentInfoPrice(CurrencyPriceDTO currencyPriceInfo) {
        BigDecimal currentPrice = new BigDecimal(currencyPriceInfo.getUSDBRL().getBid());
        boolean updatePrice = false;

        List<QuotationEntity> quotationList = quotationRepository.findAll().list();

        if(quotationList.isEmpty()){
            saveQuotation(currencyPriceInfo);
            updatePrice = true;
        } else {
            QuotationEntity lastDollarPrice = quotationList
                    .get(quotationList.size() -1);
            if(currentPrice.floatValue() != lastDollarPrice.getCurrencyPrice().floatValue()){
                updatePrice = true;
                saveQuotation(currencyPriceInfo);
            }
        }
        return updatePrice;
    }

    private void saveQuotation(CurrencyPriceDTO currencyInfo){
        QuotationEntity quotation = new QuotationEntity();

        quotation.setDate(new Date());
        quotation.setCurrencyPrice(new BigDecimal(currencyInfo.getUSDBRL().getBid()));
        quotation.setPctChange(currencyInfo.getUSDBRL().getPctChange());
        quotation.setPair("USD-BRL");

        quotationRepository.persist(quotation);
    }

}