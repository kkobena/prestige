/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import dal.TFamille;
import dal.TSuggestionOrderDetails;
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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import rest.service.SuggestionService;

/**
 *
 * @author kkoffi
 */
@Path("v1/suggestion")
@Produces("application/json")
@Consumes("application/json")
public class SuggestionRessource {

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

                try (CSVPrinter printer = CSVFormat.EXCEL
                        .withDelimiter(';').withHeader(ArticleHeader.class).print(writer)) {
                    
                    detailses.forEach(f -> {
                        try {
                            TFamille OFamille = f.getLgFAMILLEID();
                            printer.printRecord(OFamille.getIntCIP(),f.getIntNUMBER());
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

}
