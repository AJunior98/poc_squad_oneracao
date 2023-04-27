package org.br.agro.controller;

import org.br.agro.dto.ProposalDetailsDTO;
import org.br.agro.service.ProposalService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/api/trade")
public class ProposalController {

    @Inject
    ProposalService proposalService;

    @GET
    @Path("/{id}")
    @RolesAllowed({"user","manager"})
    public Response getProposalDetailsById(@PathParam("id") long id){
        try {
            return Response.ok(proposalService.getProposalDetailsById(id), MediaType.APPLICATION_JSON).build();
        } catch (ServerErrorException errorException) {
            return Response.serverError().build();
        }
    }

    @POST
    @RolesAllowed("proposal-customer")
    public Response createNewProposal(ProposalDetailsDTO proposalDetails){
        int proposalRequestStatus = proposalService.createProposal(proposalDetails).getStatus();

        if(proposalRequestStatus > 199 || proposalRequestStatus < 205){
            return Response.ok().build();
        } else {
            return Response.status(proposalRequestStatus).build();
        }
    }

    @DELETE
    @Path("/remove/{id}")
    @RolesAllowed("manager")
    public Response removeProposal(@PathParam("id") long id){
        int proposalRequestStatus = proposalService.removeProposal(id).getStatus();

        if(proposalRequestStatus > 199 || proposalRequestStatus < 205){
            return Response.ok().build();
        } else {
            return Response.status(proposalRequestStatus).build();
        }
    }

}
