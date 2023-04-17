package org.br.agro.service.impl;

import org.br.agro.dto.ProposalDTO;
import org.br.agro.dto.ProposalDetailsDTO;
import org.br.agro.entity.ProposalEntity;
import org.br.agro.message.KafkaEvents;
import org.br.agro.repository.ProposalRepository;
import org.br.agro.service.ProposalService;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Date;

@ApplicationScoped
@Traced
public class ProposalServiceImpl implements ProposalService {

    @Inject
    ProposalRepository proposalRepository;

    @Inject
    KafkaEvents kafkaEvent;

    @Override
    public ProposalDetailsDTO findFullProposal(long id) {
        ProposalEntity proposal = proposalRepository.findById(id);
        return ProposalDetailsDTO.builder()
                .proposalId(proposal.getId())
                .proposalValidityDays(proposal.getProposalValidityDays())
                .country(proposal.getCountry())
                .product(proposal.getProduct())
                .priceTonne(proposal.getPriceTonne())
                .customer(proposal.getCustomer())
                .tonnes(proposal.getTonnes())
                .build();
    }

    @Override
    @Transactional
    public void createNewProposal(ProposalDetailsDTO proposalDetailsDTO) {
        ProposalDTO proposal = buildAndSaveNewProposal(proposalDetailsDTO);
        kafkaEvent.sendNewKafkaEvent(proposal);
    }

    @Override
    @Transactional
    public void removeProposal(long id) {
        proposalRepository.deleteById(id);
    }

    @Transactional
    private ProposalDTO buildAndSaveNewProposal(ProposalDetailsDTO proposalDetailsDTO){
        try {
            ProposalEntity proposal = new ProposalEntity();

            proposal.setCreated(new Date());
            proposal.setProposalValidityDays(proposalDetailsDTO.getProposalValidityDays());
            proposal.setCountry(proposalDetailsDTO.getCountry());
            proposal.setProduct(proposalDetailsDTO.getProduct());
            proposal.setCustomer(proposalDetailsDTO.getCustomer());
            proposal.setPriceTonne(proposalDetailsDTO.getPriceTonne());
            proposal.setTonnes(proposalDetailsDTO.getTonnes());

            proposalRepository.persist(proposal);

            return ProposalDTO.builder()
                    .proposalId(proposalRepository.findByCustomer(proposal.getCustomer()).get().getId())
                    .priceTonne(proposal.getPriceTonne())
                    .customer(proposal.getCustomer())
                    .product(proposal.getProduct())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

}
