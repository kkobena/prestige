/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import commonTasks.dto.ClotureVenteParams;
import commonTasks.dto.Params;
import dal.MvtTransaction;
import dal.TPreenregistrement;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TPreenregistrementDetail;
import dal.TUser;
import java.io.File;
import java.util.List;
import javax.ejb.Local;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Kobena
 */
@Local

public interface GenerateTicketService {

    JSONObject lunchPrinterForTicket(String idVente) throws JSONException;

    String buildLineBarecode(String data);

    File buildBarecode(String data);

    JSONObject printerForTicket(String p, TUser user) throws JSONException;

    List<String> generateDataSummaryVno(TPreenregistrement p, MvtTransaction mvtTransaction);

    JSONObject lunchPrinterForTicket(ClotureVenteParams clotureVenteParams) throws JSONException;

    JSONObject lunchPrinterForTicketVo(ClotureVenteParams clotureVenteParams) throws JSONException;

    JSONObject lunchPrinterForTicketDepot(ClotureVenteParams clotureVenteParams) throws JSONException;

    JSONObject lunchPrinterForTicketVo(String p) throws JSONException;

    JSONObject ticketReglementDiffere(String idDossier) throws JSONException;

    List<TPreenregistrementDetail> listeVenteByIdVente(String id);

    List<TPreenregistrementCompteClientTiersPayent> listeVenteTiersPayantsByIdVente(String id);

    List<String> generateDataSeller(TPreenregistrement p);

    List<String> generateData(TPreenregistrement p);

    List<String> generateDataVenteSupprime(TPreenregistrement p);

    List<String> generateDataTiersPayant(TPreenregistrement p);

    List<String> generateDataTiersPayant(TPreenregistrement p, List<TPreenregistrementCompteClientTiersPayent> lstT);

    List<String> generateDataSummaryVno(TPreenregistrement p);

    List<String> generateDataSummaryVo(TPreenregistrement p);

    List<String> generateDataSummaryVo(TPreenregistrement p, ClotureVenteParams clotureVenteParams);

    List<String> generateCommentaire(TPreenregistrement p, MvtTransaction mvtTransaction);

    JSONObject lunchPrinterForTicketDepot(String id) throws JSONException;

    JSONObject ticketZ(Params params) throws JSONException;

    JSONObject generateticket10(String venteId);

    void printReceintWithJasper(String venteId);
    
    JSONObject generateTicketOnFly(String venteId) throws JSONException;

    JSONObject generateVoTicketOnFly(ClotureVenteParams clotureVenteParams);

    JSONObject generateVoTicketOnFly(String venteId);

    JSONObject generateDepotTicketOnFly(String venteId);

    JSONObject ticketReglementCarnet(String idDossier) throws JSONException;
    
  
}
