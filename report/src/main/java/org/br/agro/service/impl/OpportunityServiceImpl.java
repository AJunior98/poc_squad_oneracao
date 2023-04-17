package org.br.agro.service.impl;

import org.br.agro.dto.OpportunityDTO;
import org.br.agro.dto.ProposalDTO;
import org.br.agro.dto.QuotationDTO;
import org.br.agro.entity.OpportunityEntity;
import org.br.agro.entity.QuotationEntity;
import org.br.agro.repository.OpportunityRepository;
import org.br.agro.repository.QuotationRepository;
import org.br.agro.service.OpportunityService;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ApplicationScoped
@Traced
public class OpportunityServiceImpl implements OpportunityService {

    @Inject
    QuotationRepository quotationRepository;

    @Inject
    OpportunityRepository opportunityRepository;

    @Override
    public void buildOpportunity(ProposalDTO proposal) {
        List<QuotationEntity> quotationEntities = quotationRepository.findAll().list();
        Collections.reverse(quotationEntities);

        OpportunityEntity opportunity = new OpportunityEntity();
        opportunity.setDate(new Date());
        opportunity.setProposalId(proposal.getProposalId());
        opportunity.setCustomer(proposal.getCustomer());
        opportunity.setPriceTonne(proposal.getPriceTonne());
        opportunity.setProduct(proposal.getProduct());
        opportunity.setLastDollarQuotation(quotationEntities.get(0).getCurrencyPrice());

        opportunityRepository.persist(opportunity);
    }

    @Override
    @Transactional
    public void saveQuotation(QuotationDTO quotation) {
        QuotationEntity createQuotation = new QuotationEntity();
        createQuotation.setDate(new Date());
        createQuotation.setCurrencyPrice(quotation.getCurrencyPrice());

        quotationRepository.persist(createQuotation);
    }

    @Override
    public List<OpportunityDTO> generateOpportunityData() {
        List<OpportunityDTO> opportunities = new ArrayList<>();
        opportunityRepository.findAll().stream().forEach(item -> {
            opportunities.add(OpportunityDTO.builder()
                    .proposalId(item.getProposalId())
                    .customer(item.getCustomer())
                    .product(item.getProduct())
                    .priceTonne(item.getPriceTonne())
                    .lastDollarQuotation(item.getLastDollarQuotation())
                    .build());
        });

        return opportunities;
    }

}
