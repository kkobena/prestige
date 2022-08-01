/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import dal.TFamille;
import dal.TSuggestionOrderDetails;
import dal.TUser;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.SuggestionService;
import toolkits.parameters.commonparameter;

/**
 *
 * @author kkoffi
 */
@Path("v1/suggestion")
@Produces("application/json")
@Consumes("application/json")
public class SuggestionRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    SuggestionService suggestionService;

    @GET
    @Path("csv")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response exportToCsv(@QueryParam("id") String suggestionId) {
        StreamingOutput output = (OutputStream out) -> {
            try {
                List<TSuggestionOrderDetails> detailses = suggestionService.findFamillesBySuggestion(suggestionId);
                Writer writer = new OutputStreamWriter(out, "UTF-8");

                try ( CSVPrinter printer = CSVFormat.EXCEL
                        .withDelimiter(';').withHeader(ArticleHeader.class).print(writer)) {

                    detailses.forEach(f -> {
                        try {
                            TFamille OFamille = f.getLgFAMILLEID();
                            printer.printRecord(OFamille.getIntCIP(), f.getIntNUMBER());
                        } catch (IOException ex) {
                            Logger.getLogger(SuggestionRessource.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });

                }
            } catch (IOException ex) {
                throw new WebApplicationException("File Not Found !!");
            }
        };
        String filename = "suggestion_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("H_mm_ss")) + ".csv";
        return Response
                .ok(output, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "attachment; filename = " + filename)
                .build();

    }

    enum ArticleHeader {
        CIP, QTE
    }

    @GET
    @Path("qty-detail/{id}")
    public Response tvastatCriterion(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = suggestionService.findCHDetailStock(id, tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
        return Response.ok().entity(json.toString()).build();
    }

}
