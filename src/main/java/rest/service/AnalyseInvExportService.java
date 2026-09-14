
package rest.service;

import java.io.IOException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author airman
 */
public interface AnalyseInvExportService {

    byte[] generatePdfReport(String inventaireId, String filterType) throws JRException;

    byte[] generateExcelReport(String inventaireId, String filterType) throws IOException;

    byte[] generateAdvancedExcelReport(String inventaireId) throws IOException;

    /** Edition PDF de l'onglet « Synthèse & recommandations » (retours du 13/09). */
    byte[] generateAdvancedPdfReport(String inventaireId) throws JRException;

}