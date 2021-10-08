/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import commonTasks.dto.AjustementDTO;
import commonTasks.dto.AjustementDetailDTO;
import commonTasks.dto.Params;
import commonTasks.dto.SalesStatsParams;
import dal.TEmplacement;
import dal.TFamille;
import dal.TFamilleStock;
import dal.TMouvement;
import dal.TPreenregistrement;
import dal.TPreenregistrementDetail;
import dal.TUser;
import java.util.List;
import java.util.Optional;
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
public interface MvtProduitService {

    void updatefamillenbvente(TFamille famille, int qty, boolean updatable, EntityManager emg);

    Optional<TMouvement> findMouvement(TFamille OTFamille, String action, String typeAction, String emplacementId, EntityManager emg);

    void createSnapshotMouvementArticle(TFamille OTFamille, int qty, TUser ooTUser, TFamilleStock familleStock, String emplacementId, EntityManager emg);

    void saveMvtArticle(TFamille tf, TUser ooTUser, TFamilleStock familleStock, int qty, String emplacementId, EntityManager emg);

    void updateVenteStock(String idVente);

    public void updateVenteStock(TPreenregistrement tp, List<TPreenregistrementDetail> list, EntityManager emg);

    public void updateVenteStockDepot(TPreenregistrement tp, List<TPreenregistrementDetail> list, EntityManager emg, TEmplacement depot) throws Exception;

    void updateStockDepot(TUser ooTUser, TPreenregistrement op, TEmplacement OTEmplacement, EntityManager emg) throws Exception;

    void saveMvtArticleAddProduct(TFamille tf, TUser ooTUser, TFamilleStock familleStock, Integer qty, Integer initStock, TEmplacement emplacementId, EntityManager emg) throws Exception;

    JSONObject creerAjustement(Params params) throws JSONException;

    JSONObject ajusterProduitAjustement(Params params) throws JSONException;

    JSONObject modifierProduitAjustement(Params params) throws JSONException;

    JSONObject cloreAjustement(Params params) throws JSONException;

    JSONObject findOneAjustement(String idAjustement) throws JSONException;

    JSONObject removeAjustementDetail(String id) throws JSONException;

    JSONObject annulerAjustement(String id) throws JSONException;

    JSONObject ajsutements(SalesStatsParams params) throws JSONException;

    JSONObject ajsutementsDetails(SalesStatsParams params, String idAjustement) throws JSONException;

    void saveMvtArticle(String action, String typeAction, TFamille tf, TUser ooTUser, TFamilleStock familleStock, Integer qty, Integer intiQty, TEmplacement emplacementId, EntityManager emg);

    void saveMvtArticle(String action, String typeAction, TFamille tf, TUser ooTUser, Integer qty, Integer intiQty, Integer finalQty, TEmplacement emplacementId, EntityManager emg);

    JSONObject deconditionner(Params params) throws JSONException;

    JSONObject validerRetourFournisseur(Params params) throws JSONException;

    TFamilleStock updateStock(TFamille tf, TEmplacement emplacementId, int qty, int ug, EntityManager em);

    int updateStockReturnInitStock(TFamille tf, TEmplacement emplacementId, int qty, int ug, EntityManager em);

    JSONObject loadetourFournisseur(
            String dtStart,
            String dtEnd,
            int start,
            int limit,
            String fourId,
            String query, boolean cunRemove) throws JSONException;

    JSONObject validerRetourDepot(String retourId, TUser user) throws JSONException;

    List<AjustementDTO> getAllAjustements(SalesStatsParams params);

    List<AjustementDetailDTO> getAllAjustementDetailDTOs(SalesStatsParams params);

}
