/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import commonTasks.dto.ArticleDTO;
import commonTasks.dto.VenteDetailsDTO;
import dal.TCalendrier;
import dal.TFamille;
import dal.TFamilleStock;
import dal.TSuggestionOrderDetails;
import dal.TUser;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.EntityManager;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Kobena
 */
@Local
//@Remote
public interface SuggestionService {

    void makeSuggestionAuto(TFamilleStock OTFamilleStock, TFamille famille, EntityManager emg);

    void makeSuggestionAuto(String OTPreenregistrement);

    Integer getQuantityReapportByCodeGestionArticle(TFamilleStock OTFamilleStock, TFamille famille, EntityManager emg);

    Integer quantiteVendue(LocalDate dtDEBUT, LocalDate dtFin, String produitId, EntityManager emg);

    List<TCalendrier> nombresJourVente(LocalDate begin, EntityManager emg);

    List<TSuggestionOrderDetails> findFamillesBySuggestion(String suggestionId);

    int verifierProduitDansLeProcessusDeCommande(TFamille famille);

    /**
     * SUGGESTION DES 20/80
     *
     * @param datas
     * @return
     * @throws JSONException
     */
    JSONObject makeSuggestion(List<VenteDetailsDTO> datas) throws JSONException;

    JSONObject makeSuggestionFromArticleInvendus(List<ArticleDTO> datas,TUser tu) throws JSONException;
    
}
