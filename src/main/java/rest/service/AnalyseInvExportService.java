
package rest.service;

import java.io.IOException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author airman
 */
public interface AnalyseInvExportService {

    /**
     * Generer PDF 
     *
     * @param inventaireId
     *             inventaire id.
     * @param filterType
     *            FILTRE("tout", "avec", "sans").
    
     */
    byte[] generatePdfReport(String inventaireId, String filterType) throws JRException;

   /*
    * Generer EXCEL 
     * @param inventaireId
     *            inventaire id.
     * @param filterType
     *            FILTRE ("tout", "sans", "avec").
     
     */
    byte[] generateExcelReport(String inventaireId, String filterType) throws IOException;

}