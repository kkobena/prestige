/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import commonTasks.dto.ArticleDTO;
import commonTasks.dto.VenteDetailsDTO;
import dal.TCalendrier;
import dal.TEmplacement;
import dal.TFamille;
import dal.TFamilleStock;
import dal.TSuggestionOrderDetails;
import dal.TUser;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;
import javax.persistence.EntityManager;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.dto.SuggestionDTO;
import rest.service.dto.SuggestionOrderDetailDTO;
import rest.service.dto.SuggestionsDTO;

/**
 *
 * @author Kobena
 */
@Local

public interface SuggestionService {

    void makeSuggestionAuto(TFamilleStock familleStock, TFamille famille);

    void makeSuggestionAuto(String preenregistrement);

    Integer getQuantityReapportByCodeGestionArticle(TFamilleStock familleStock, TFamille famille, EntityManager emg);

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

    JSONObject makeSuggestionFromArticleInvendus(List<ArticleDTO> datas, TUser tu) throws JSONException;

    JSONObject makeSuggestion(Set<VenteDetailsDTO> datas) throws JSONException;

    JSONObject findCHDetailStock(String idProduit, String emplacement);

    void proccessSuggetion(TFamille famille, TEmplacement emplacementId);

    void removeItem(String itemId);

    SuggestionDTO getSuggestionAmount(String suggestionId);

    void addItem(SuggestionOrderDetailDTO suggestionOrderDetail);

    void updateItemSeuil(SuggestionOrderDetailDTO suggestionOrderDetail);

    void updateItemQteCmde(SuggestionOrderDetailDTO suggestionOrderDetail);

    void updateItemQtePrixPaf(SuggestionOrderDetailDTO suggestionOrderDetail);

    void updateItemQtePrixVente(SuggestionOrderDetailDTO suggestionOrderDetail);

    SuggestionDTO create(SuggestionDTO suggestion);

    JSONObject fetch(String query, int start, int limit);

}
