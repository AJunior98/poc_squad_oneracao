package org.br.agro.service;

import org.br.agro.dto.OpportunityDTO;

import javax.enterprise.context.ApplicationScoped;
import java.io.ByteArrayInputStream;
import java.util.List;

@ApplicationScoped
public interface ReportService {

    ByteArrayInputStream generateCSVOpportunityReport();

    List<OpportunityDTO> getOpportunitiesData();

}
