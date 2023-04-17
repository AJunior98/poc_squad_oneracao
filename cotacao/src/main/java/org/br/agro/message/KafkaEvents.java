package org.br.agro.message;

import org.br.agro.dto.QuotationDTO;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KafkaEvents {

    private final Logger LOG = LoggerFactory.getLogger(KafkaEvents.class);

    @Channel("quotation-channel")
    Emitter<QuotationDTO> quotationRequestEmitter;

    public void sendNewKafkaEvent(QuotationDTO quotation){
        LOG.info("-- Enviando Cotação para Tópico Kafka --");
        quotationRequestEmitter.send(quotation).toCompletableFuture().join();
    }

}
