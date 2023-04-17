package org.br.agro.message;

import io.smallrye.common.annotation.Blocking;
import org.br.agro.dto.ProposalDTO;
import org.br.agro.dto.QuotationDTO;
import org.br.agro.service.OpportunityService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;

public class KafkaEvent {

    private final Logger LOG = LoggerFactory.getLogger(KafkaEvent.class);

    @Inject
    OpportunityService opportunityService;

    @Incoming("proposal-channel")
    @Transactional
    public void receiveProposal(ProposalDTO proposal){
        LOG.info("-- Recebendo nova proposta do tópico Kafka --");
        opportunityService.buildOpportunity(proposal);
    }

    @Incoming("quotation-channel")
    @Blocking
    public void receiveQuotation(QuotationDTO quotation){
        LOG.info("-- Recebendo nova cotação de moeda do tópico Kafka --");
        opportunityService.saveQuotation(quotation);
    }

}
