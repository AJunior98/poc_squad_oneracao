package org.br.agro.controller;

import org.br.agro.service.ReportService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@ApplicationScoped
@Path("/api/opportunity")
public class ReportController {

    @Inject
    ReportService reportService;

    @GET
    @Path("/report")
    @RolesAllowed({"user","manager"})
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response generateReport(){
        try {
            return Response.ok(reportService.generateCSVOpportunityReport(), MediaType.APPLICATION_OCTET_STREAM)
                    .header("content-disposition",
                            "attachment; filename = "+ new Date() +"--oportunidades-venda.csv").
                    build();
        } catch (ServerErrorException errorException) {
            return Response.serverError().build();
        }

    }

    @GET
    @Path("/data")
    @RolesAllowed({"user","manager"})
    public Response generateOpportunitiesData(){
        try {
            return Response.ok(reportService.getOpportunitiesData(), MediaType.APPLICATION_JSON).build();
        } catch (ServerErrorException errorException) {
            return Response.serverError().build();
        }

    }

}
