package org.br.agro.message;

import org.br.agro.dto.ProposalDTO;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KafkaEvents {

    private final Logger LOG = LoggerFactory.getLogger(KafkaEvents.class);

    @Channel("proposal-channel")
    Emitter<ProposalDTO> quotationRequestEmitter;

    public void sendNewKafkaEvent(ProposalDTO quotation){
        LOG.info("-- Enviando Proposta para Tópico Kafka --");
        quotationRequestEmitter.send(quotation).toCompletableFuture().join();
    }


}