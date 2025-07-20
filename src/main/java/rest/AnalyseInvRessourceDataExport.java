
package rest;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import rest.service.AnalyseInvExportService;

/**
 *
 * @author airman
 */
@Path("v1")
public class AnalyseInvRessourceDataExport {

    private static final Logger LOG = Logger.getLogger(AnalyseInvRessourceDataExport.class.getName());

    @Inject
    private AnalyseInvExportService exportService;

    @GET
    @Path("analyse-inventaire-pdf")
    @Produces("application/pdf")
    public Response exportPdf(@QueryParam("inventaireId") String inventaireId,
            @QueryParam("inventaireName") String inventaireName, @QueryParam("filterType") String filterType) {
        try {
            byte[] pdfData = exportService.generatePdfReport(inventaireId, filterType);
            String fileName = "analyse_inventaire_"
                    + (inventaireName != null ? inventaireName.replaceAll("\\s+", "_") : inventaireId) + ".pdf";

            return Response.ok(pdfData, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"").build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error generating PDF report for inventory ID: " + inventaireId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("PDF generation failed: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("analyse-inventaire-excel")
    @Produces("application/vnd.ms-excel")
    public Response exportExcel(@QueryParam("inventaireId") String inventaireId,
            @QueryParam("inventaireName") String inventaireName, @QueryParam("filterType") String filterType) {
        try {
            byte[] excelData = exportService.generateExcelReport(inventaireId, filterType);
            String fileName = "analyse_inventaire_"
                    + (inventaireName != null ? inventaireName.replaceAll("\\s+", "_") : inventaireId) + ".xls";

            return Response.ok(excelData, "application/vnd.ms-excel")
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"").build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error generating Excel report for inventory ID: " + inventaireId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Excel generation failed: " + e.getMessage()).build();
        }
    }
}
