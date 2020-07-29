/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.report.pdf;

import commonTasks.dto.ArticleDTO;
import commonTasks.dto.FamilleArticleStatDTO;
import commonTasks.dto.VenteDetailsDTO;
import dal.TOfficine;
import dal.TUser;
import enumeration.MargeEnum;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import rest.report.ReportUtil;
import rest.service.CommonService;
import rest.service.DataReporingService;
import rest.service.FicheArticleService;
import toolkits.utils.jdom;

/**
 *
 * @author DICI
 */
@Stateless
public class DataReporting {

    @EJB
    private DataReporingService dataReporingService;
    @EJB
    ReportUtil reportUtil;
    @EJB
    CommonService commonService;
    @EJB
    FicheArticleService ficheArticleService;

    public String margeProduitsVendus(String dtStart, String dtEnd, String codeFamile, Integer critere, String query, TUser tu, String codeRayon, String codeGrossiste, MargeEnum filtre) throws IOException {
        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(dtStart);
            dtEn = LocalDate.parse(dtEnd);
        } catch (Exception e) {
        }
        TOfficine oTOfficine = commonService.findOfficine();
        String scr_report_file = "rp_margeproduits";
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (!dtEn.isEqual(dtSt)) {
            P_PERIODE += " AU " + dtEn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        parameters.put("P_H_CLT_INFOS", "MARGES DES PRODUITS VENDUS " + P_PERIODE);
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH_mm_ss")) + ".pdf";
        List<FamilleArticleStatDTO> data = dataReporingService.margeProduitsVendus(dtStart, dtEnd, codeFamile, critere, query, tu, codeRayon, codeGrossiste, 0, 0, true, filtre).getRight();
        reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "marges_produit_" + report_generate_file, data);
        return "/data/reports/pdf/marges_produit_" + report_generate_file;
    }

    public String statsUnintesVendues(String dtStart, String dtEnd, String codeFamile, String query, TUser tu, String codeRayon, String codeGrossiste) throws IOException {
        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(dtStart);
            dtEn = LocalDate.parse(dtEnd);
        } catch (Exception e) {
        }
        TOfficine oTOfficine = commonService.findOfficine();
        String scr_report_file = "rp_statistic_unites_vendues";
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (!dtEn.isEqual(dtSt)) {
            P_PERIODE += " AU " + dtEn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        parameters.put("P_H_CLT_INFOS", "MARGES DES PRODUITS VENDUS " + P_PERIODE);
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH_mm_ss")) + ".pdf";
        List<FamilleArticleStatDTO> data = dataReporingService.statsUnintesVendues(dtStart, dtEnd, codeFamile, query, tu, codeRayon, codeGrossiste, 0, 0, true).getRight();
        reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "unites_vendues_" + report_generate_file, data);
        return "/data/reports/pdf/unites_vendues_" + report_generate_file;
    }

    public String statsUnintesVenduesparLaboratoire(String dtStart, String dtEnd, String codeFamile, String query, TUser tu, String codeRayon, String codeGrossiste, String laboratoireId) throws IOException {
        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(dtStart);
            dtEn = LocalDate.parse(dtEnd);
        } catch (Exception e) {
        }
        TOfficine oTOfficine = commonService.findOfficine();
        String scr_report_file = "rp_stat_unites_vendues_labo";
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (!dtEn.isEqual(dtSt)) {
            P_PERIODE += " AU " + dtEn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        parameters.put("P_H_CLT_INFOS", "STATISTIQUES DES PRODUITS VENDUS PAR LABORATOIRE " + P_PERIODE);
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH_mm_ss")) + ".pdf";
        List<FamilleArticleStatDTO> data = dataReporingService.statsUnintesVenduesparLaboratoire(dtStart, dtEnd, codeFamile, query, tu, codeRayon, codeGrossiste, laboratoireId, 0, 0, true).getRight();
        reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "statist_par_laboratoire_" + report_generate_file, data);
        return "/data/reports/pdf/statist_par_laboratoire_" + report_generate_file;
    }

    public String statsUnintesVenduesparGamme(String dtStart, String dtEnd, String codeFamile, String query, TUser tu, String codeRayon, String codeGrossiste, String gammeId) throws IOException {
        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(dtStart);
            dtEn = LocalDate.parse(dtEnd);
        } catch (Exception e) {
        }
        TOfficine oTOfficine = commonService.findOfficine();
        String scr_report_file = "rp_stat_unites_vendues_labo";
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (!dtEn.isEqual(dtSt)) {
            P_PERIODE += " AU " + dtEn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        parameters.put("P_H_CLT_INFOS", "STATISTIQUES DES PRODUITS VENDUS PAR GAMME " + P_PERIODE);
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH_mm_ss")) + ".pdf";
        List<FamilleArticleStatDTO> data = dataReporingService.statsUnintesVenduesparGamme(dtStart, dtEnd, codeFamile, query, tu, codeRayon, codeGrossiste, gammeId, 0, 0, true).getRight();
        reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "statist_par_gamme_" + report_generate_file, data);
        return "/data/reports/pdf/statist_par_gamme_" + report_generate_file;
    }

    public String statsArticlesInvendus(String dtStart, String dtEnd, String codeFamile, String query, TUser tu, String codeRayon, String codeGrossiste, int stock, MargeEnum stockFiltre) throws IOException {
        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(dtStart);
            dtEn = LocalDate.parse(dtEnd);
        } catch (Exception e) {
        }
        TOfficine oTOfficine = commonService.findOfficine();
        String scr_report_file = "rp_articles_invendus";
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (!dtEn.isEqual(dtSt)) {
            P_PERIODE += " AU " + dtEn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        parameters.put("P_H_CLT_INFOS", "ARTICLES NON VENDUS " + P_PERIODE);
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH_mm_ss")) + ".pdf";
        Comparator<ArticleDTO> comparator = Comparator.comparing(ArticleDTO::getFilterId)
                .thenComparing(ArticleDTO::getLibelle);

        List<ArticleDTO> data = dataReporingService.statsArticlesInvendus(dtStart, dtEnd, codeFamile, query, tu, codeRayon, codeGrossiste, stock, stockFiltre, 0, 0, true);
        data.sort(comparator);

        reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "articles_invendus_" + report_generate_file, data);
        return "/data/reports/pdf/articles_invendus_" + report_generate_file;
    }

    public String articleSurStock(String codeFamile, String query, TUser tu, String codeRayon, String codeGrossiste, int nbreMois, int nbreConsommation) throws IOException {

        TOfficine oTOfficine = commonService.findOfficine();
        String scr_report_file = "rp_articles_surstock";
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + LocalDate.now().minusMonths(nbreMois).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " AU " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        parameters.put("P_H_CLT_INFOS", "PRODUITS DONT LE STOCK EST SUPERIEUR A " + nbreMois + " MOIS" + " \n " + P_PERIODE);
        LocalDate now = LocalDate.now();
        parameters.put("consommationsOne", now.minusMonths(1).format(DateTimeFormatter.ofPattern("MMMM")));
        parameters.put("consommationsTwo", now.minusMonths(2).format(DateTimeFormatter.ofPattern("MMMM")));
        parameters.put("consommationsThree", now.minusMonths(3).format(DateTimeFormatter.ofPattern("MMMM")));
        parameters.put("consommationUn", now.format(DateTimeFormatter.ofPattern("MMMM")));
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH_mm_ss")) + ".pdf";
        Comparator<ArticleDTO> comparator = Comparator.comparing(ArticleDTO::getFilterId)
                .thenComparing(ArticleDTO::getLibelle);
        List<ArticleDTO> data = ficheArticleService.articleSurStock(tu, query, codeFamile, codeRayon, codeGrossiste, nbreMois, nbreConsommation, 0, 0, true);
        data.sort(comparator);

        reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "rp_articles_surstock_" + report_generate_file, data);
        return "/data/reports/pdf/rp_articles_surstock_" + report_generate_file;
    }

    public String comparaisonStock(TUser tu, String query, MargeEnum filtreStock, MargeEnum filtreSeuil, String codeFamile, String codeRayon, String codeGrossiste, int stock, int seuil) throws IOException {

        TOfficine oTOfficine = commonService.findOfficine();
        String scr_report_file = "rp_comparaison_surstock";
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "";

        if (filtreStock != null && filtreStock != MargeEnum.ALL) {
            switch (filtreStock) {

                case EQUAL:
                    P_PERIODE += " DONT LE STOCK EST EQAL A " + stock;

                    break;
                case GREATER:
                    P_PERIODE += " DONT LE STOCK EST PLUS GRAND QUE  " + stock;
                    break;
                case GREATER_EQUAL:
                    P_PERIODE += " DONT LE STOCK EST PLUS GRAND OU EQAL A  " + stock;
                    break;
                case LESS:
                    P_PERIODE += " DONT LE STOCK EST INFERIEUR A  " + stock;
                    break;

                case LESS_EQUAL:
                    P_PERIODE += " DONT LE STOCK EST INFERIEUR OU EGAL A  " + stock;
                    break;
                case NOT:
                    P_PERIODE += " DONT LE STOCK DIFFERENT DE   " + stock;
                    break;
                case STOCK_LESS_THAN_SEUIL:
                    P_PERIODE += " DONT LE STOCK EST INFERIEUR AU SEUIL ";
                    break;
                default:
                    break;
            }
        }

        if (filtreSeuil != null && filtreSeuil != MargeEnum.ALL) {
            switch (filtreSeuil) {

                case EQUAL:
                    P_PERIODE += " AVEC UN SEUIL EQAL A " + seuil;
                    break;
                case GREATER:
                    P_PERIODE += " AVEC UN SEUIL SUPERIEUR  A " + seuil;
                    break;
                case GREATER_EQUAL:
                    P_PERIODE += " AVEC UN SEUIL SUPERIEUR OU EGAL A " + seuil;
                    break;
                case LESS:
                    P_PERIODE += " AVEC UN SEUIL INFERIEUR  A " + seuil;
                    break;

                case LESS_EQUAL:
                    P_PERIODE += " AVEC UN SEUIL INFERIEUR OU EGAL  A " + seuil;
                    break;
                case NOT:
                    P_PERIODE += " AVEC UN SEUIL DIFFERENT DE  " + seuil;
                    break;

                default:
                    break;
            }
        }

        parameters.put("P_H_CLT_INFOS", "LISTE DES ARTICLES  \n " + P_PERIODE);
        LocalDate now = LocalDate.now();
        parameters.put("consommationsOne", now.minusMonths(1).format(DateTimeFormatter.ofPattern("MMMM")));
        parameters.put("consommationsTwo", now.minusMonths(2).format(DateTimeFormatter.ofPattern("MMMM")));
        parameters.put("consommationsThree", now.minusMonths(3).format(DateTimeFormatter.ofPattern("MMMM")));
        parameters.put("consommationUn", now.format(DateTimeFormatter.ofPattern("MMMM")));

        parameters.put("consommationsFour", now.minusMonths(4).format(DateTimeFormatter.ofPattern("MMMM")));
        parameters.put("consommationsFive", now.minusMonths(5).format(DateTimeFormatter.ofPattern("MMMM")));
        parameters.put("consommationsSix", now.minusMonths(6).format(DateTimeFormatter.ofPattern("MMMM")));

        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH_mm_ss")) + ".pdf";
        Comparator<ArticleDTO> comparator = Comparator.comparing(ArticleDTO::getFilterId)
                .thenComparing(ArticleDTO::getLibelle);
        List<ArticleDTO> data = ficheArticleService.comparaisonStock(tu, query, filtreStock, filtreSeuil, codeFamile, codeRayon, codeGrossiste, stock, seuil, 0, 0, true);
        data.sort(comparator);

        reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "rp_comparaison_tock_" + report_generate_file, data);
        return "/data/reports/pdf/rp_comparaison_tock_" + report_generate_file;
    }
 public String produitConsomamation(TUser tu, String query, String dtStart, String dtEnd, String id,String libelle,String cip) throws IOException {

        TOfficine oTOfficine = commonService.findOfficine();
        String scr_report_file = "rp_consoarticle_comparaison";
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + LocalDate.parse(dtStart).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " AU " + LocalDate.parse(dtEnd).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        parameters.put("P_H_CLT_INFOS", "CONSOMMATION DE L'ARTICLE\n   "+libelle+"\n CODE CIP :  "+cip+" "
                + P_PERIODE);
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss")) + ".pdf";
//        Comparator<ArticleDTO> comparator = Comparator.comparing(ArticleDTO::getFilterId)
//                .thenComparing(ArticleDTO::getLibelle);
        List<VenteDetailsDTO> data = ficheArticleService.produitConsomamation(tu, query, dtStart, dtEnd, id);
//        data.sort(comparator);
        reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "consoarticle_comparaison_" + report_generate_file, data);
        return "/data/reports/pdf/consoarticle_comparaison_" + report_generate_file;
    }
}
