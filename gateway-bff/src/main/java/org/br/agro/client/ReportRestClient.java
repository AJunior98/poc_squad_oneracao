package org.br.agro.client;

import io.quarkus.oidc.token.propagation.reactive.AccessTokenRequestReactiveFilter;
import org.br.agro.dto.OpportunityDTO;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@RegisterRestClient(baseUri = "http://localhost:8082")
@Path("/api/opportunity")
@RegisterProvider(AccessTokenRequestReactiveFilter.class)
@ApplicationScoped
public interface ReportRestClient {

    @GET
    @Path("/data")
    List<OpportunityDTO> requestOpportunitiesData();
}
