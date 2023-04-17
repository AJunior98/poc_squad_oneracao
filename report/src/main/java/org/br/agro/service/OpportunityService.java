package org.br.agro.service;

import org.br.agro.dto.OpportunityDTO;
import org.br.agro.dto.ProposalDTO;
import org.br.agro.dto.QuotationDTO;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface OpportunityService {

    void buildOpportunity(ProposalDTO proposal);

    void saveQuotation(QuotationDTO quotation);

    List<OpportunityDTO> generateOpportunityData();

}
