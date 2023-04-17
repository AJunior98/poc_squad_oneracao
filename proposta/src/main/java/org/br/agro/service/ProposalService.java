package org.br.agro.service;

import org.br.agro.dto.ProposalDetailsDTO;

public interface ProposalService {

    ProposalDetailsDTO findFullProposal(long id);

    void createNewProposal(ProposalDetailsDTO proposalDetailsDTO);

    void removeProposal(long id);

}
