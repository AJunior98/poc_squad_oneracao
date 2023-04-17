package org.br.agro.service.impl;

import org.br.agro.client.ReportRestClient;
import org.br.agro.dto.OpportunityDTO;
import org.br.agro.service.ReportService;

import org.br.agro.utils.CSVHelper;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.util.List;


@ApplicationScoped
@Traced
public class ReportServiceImpl implements ReportService {

    @Inject
    @RestClient
    ReportRestClient reportRestClient;

    @Override
    public ByteArrayInputStream generateCSVOpportunityReport() {
        List<OpportunityDTO> opportunityData = reportRestClient.requestOpportunitiesData();
        return CSVHelper.OpportunitiesToCSV(opportunityData);
    }

    @Override
    public List<OpportunityDTO> getOpportunitiesData() {
        return reportRestClient.requestOpportunitiesData();
    }

}
