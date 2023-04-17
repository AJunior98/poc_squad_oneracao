package org.br.agro.client;

import io.quarkus.oidc.token.propagation.reactive.AccessTokenRequestReactiveFilter;
import org.br.agro.dto.ProposalDetailsDTO;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@RegisterRestClient(baseUri = "http://localhost:8081")
@Path("/api/proposal")
@RegisterProvider(AccessTokenRequestReactiveFilter.class)
@ApplicationScoped
public interface ProposalRestClient {

    @GET
    @Path("/{id}")
    ProposalDetailsDTO getProposalDetailsById(@PathParam("id") long proposalId);

    @POST
    Response createProposal(ProposalDetailsDTO proposalDetails);

    @DELETE
    @Path("/{id}")
    Response removeProposal(@PathParam("id") long id);

}
