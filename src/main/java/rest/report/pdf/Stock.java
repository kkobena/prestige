/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.report.pdf;

import commonTasks.dto.RuptureDetailDTO;
import commonTasks.dto.ValorisationDTO;
import commonTasks.dto.VenteDetailsDTO;
import dal.TOfficine;
import dal.TUser;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import rest.report.ReportUtil;
import rest.service.CaisseService;
import rest.service.CommonService;
import rest.service.OrderService;
import rest.service.ProduitService;
import toolkits.utils.jdom;

/**
 *
 * @author DICI
 */
@Stateless
public class Stock {

    @EJB
    ProduitService produitService;
    @EJB
    ReportUtil reportUtil;
    @EJB
    CommonService commonService;
    @EJB
    OrderService orderService;
       @EJB
    private CaisseService caisseService;

    public String valorisation(TUser tu, int mode, LocalDate dtSt, String lgGROSSISTEID, String lgFAMILLEARTICLEID, String lgZONEGEOID, String END, String BEGIN, String emplacementId) throws IOException {
        TOfficine oTOfficine = commonService.findOfficine();
        String scr_report_file = "rp_valorisation_stock_produit2";
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String P_SUBTITLE;
        switch (mode) {
            case 1:
                P_SUBTITLE = "VALORISATION  PAR FAMILLE D'ARTICLE ";
                break;
            case 2:
                P_SUBTITLE = "VALORISATION PAR EMPLACEMENT ";
                break;
            case 3:
                P_SUBTITLE = "VALORISATION PAR GROSSISTE ";
                break;
            default:
                P_SUBTITLE = "VALORISATION ";
                scr_report_file = "rp_valorisation_stock_produit";
                break;
        }

        parameters.put("P_H_CLT_INFOS", P_SUBTITLE + P_PERIODE);
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH_mm_ss")) + ".pdf";
        ValorisationDTO o = produitService.getValeurStockPdf(mode, dtSt, lgGROSSISTEID, lgFAMILLEARTICLEID, lgZONEGEOID, END, BEGIN, emplacementId);

        ValorisationDTO tva = o.getTvas();
        parameters.put("totalpa", o.getMontantFacture());
        parameters.put("totalpu", o.getMontantPu());
        parameters.put("tvaPu", o.getMontantFacture());
        parameters.put("tvaPu", tva.getMontantPu());
        parameters.put("tvaTarif", tva.getMontantTarif());
        parameters.put("tvaFact", tva.getMontantFacture());
        parameters.put("tvaPmd", tva.getMontantPmd());
        parameters.put("tvas", tva.getDatas());
        if (mode > 0) {
            parameters.put("totalPmd", o.getMontantPmd());
            parameters.put("totalTarif", o.getMontantTarif());

            reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "valorisation_" + report_generate_file, o.getDatas());
        } else {
            reportUtil.buildReportEmptyDs(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "valorisation_" + report_generate_file);
        }

        return "/data/reports/pdf/valorisation_" + report_generate_file;
    }

    public String venteUgDTO(TUser tu, LocalDate dtSt, LocalDate dtEnd, String query) throws IOException {
        TOfficine oTOfficine = commonService.findOfficine();
        String scr_report_file = "rp_vente_ugs";
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        parameters.put("P_H_CLT_INFOS", "VENTES UNITES GRATUITES " + P_PERIODE);
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH_mm_ss")) + ".pdf";
        List<VenteDetailsDTO> data = caisseService.venteUgDTO(dtSt, dtEnd, query);
        reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "rp_vente_ugs" + report_generate_file, data);
        return "/data/reports/pdf/rp_vente_ugs" + report_generate_file;
    }
  public String rupturePharmaMl(TUser tu, LocalDate dtSt, LocalDate dtEnd, String query, String grossisteId, String emplacementId) throws IOException {
        TOfficine oTOfficine = commonService.findOfficine();
        String scr_report_file = "rp_ruptures_pharmaml";
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        parameters.put("P_H_CLT_INFOS", "PRODUITS EN RUPTURES " + P_PERIODE);
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH_mm_ss")) + ".pdf";
        List<RuptureDetailDTO> data = orderService.listeRuptures(dtSt, dtEnd, query, grossisteId, emplacementId);
        reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "rp_ruptures_pharmaml_" + report_generate_file, data);
        return "/data/reports/pdf/rp_ruptures_pharmaml_" + report_generate_file;
    }
}
