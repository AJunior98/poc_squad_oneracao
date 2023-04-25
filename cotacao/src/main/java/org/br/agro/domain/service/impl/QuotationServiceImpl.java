package org.br.agro.domain.service.impl;

import org.br.agro.application.web.client.CurrencyPriceClient;
import org.br.agro.application.web.dto.CurrencyPriceDTO;
import org.br.agro.application.web.dto.QuotationDTO;
import org.br.agro.domain.entity.Quotation;
import org.br.agro.domain.service.QuotationService;
import org.br.agro.infra.entity.converter.LocalDateTimeConverter;
import org.br.agro.infra.message.KafkaEvents;
import org.br.agro.infra.repository.QuotationRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.br.agro.infra.entity.converter.LocalDateTimeConverter.DD_MM_YYYY_HH_MM_SS_TRACO;

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

    private boolean updateCurrentInfoPrice(CurrencyPriceDTO currencyPriceInfo) {
        BigDecimal currentPrice = new BigDecimal(currencyPriceInfo.getUSDBRL().getBid());
        boolean updatePrice = false;

        List<Quotation> quotationList = quotationRepository.findAll();

        if(quotationList.isEmpty()){
            saveQuotation(currencyPriceInfo);
            updatePrice = true;
        } else {
            Quotation lastDollarPrice = quotationList
                    .get(quotationList.size() -1);
            if(currentPrice.floatValue() != lastDollarPrice.getCurrencyPrice().floatValue()){
                updatePrice = true;
                saveQuotation(currencyPriceInfo);
            }
        }
        return updatePrice;
    }

    private void saveQuotation(CurrencyPriceDTO currencyInfo){
        Quotation quotation = new Quotation();

        quotation.setDate(LocalDateTimeConverter.converteParaLocalDateTime(currencyInfo.getUSDBRL().getCreate_date(), DD_MM_YYYY_HH_MM_SS_TRACO));
        quotation.setCurrencyPrice(new BigDecimal(currencyInfo.getUSDBRL().getBid()));
        quotation.setPctChange(currencyInfo.getUSDBRL().getPctChange());
        quotation.setPair("USD-BRL");

        quotationRepository.save(quotation);
    }

    @Override
    public void cleanDataBase(){
        quotationRepository.deleteAll();
    }

}