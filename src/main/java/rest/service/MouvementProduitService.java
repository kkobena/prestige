/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import commonTasks.dto.Params;
import commonTasks.dto.SalesStatsParams;
import dal.TEmplacement;
import dal.TFamille;
import dal.TFamilleStock;
import dal.TPreenregistrement;
import dal.TPreenregistrementDetail;
import dal.TUser;
import dal.Typemvtproduit;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.EntityManager;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author DICI
 */
@Local
//@Remote
public interface MouvementProduitService {

    void saveMvtProduit(String pkey, Typemvtproduit typemvtproduit,
            TFamilleStock familleStock, TUser lgUSERID, TEmplacement emplacement,
            Integer qteMvt, Integer qteDebut, EntityManager emg, Integer valeurTva);

    void saveMvtProduit(String pkey, Typemvtproduit typemvtproduit,
            TFamille famille, TUser lgUSERID, TEmplacement emplacement,
            Integer qteMvt, Integer qteDebut, Integer qteFinale,
            EntityManager emg, Integer valeurTva, boolean checked);

    void saveMvtProduit(Integer prixUn, String pkey,
            Typemvtproduit typemvtproduit, TFamille famille,
            TUser lgUSERID, TEmplacement emplacement,
            Integer qteMvt, Integer qteDebut, Integer qteFinale,
            EntityManager emg, Integer valeurTva, boolean checked);

    void saveMvtProduit(String pkey, String typemvtproduit,
            TFamille famille, TUser lgUSERID, TEmplacement emplacement,
            Integer qteMvt, Integer qteDebut, Integer qteFinale, EntityManager emg, Integer valeurTva);

    void saveMvtProduit(Integer prixUn, Integer prixAchat, String pkey,
            String typemvtproduit, TFamille famille, TUser lgUSERID,
            TEmplacement emplacement, Integer qteMvt, Integer qteDebut,
            Integer qteFinale, EntityManager emg, Integer valeurTva);

    JSONObject creerAjustement(Params params) throws JSONException;

    JSONObject deconditionner(Params params) throws JSONException;

//    void deconditionner(String produitId, String produitParentId, Integer qty,String emplacementId);
    JSONObject ajusterProduitAjustement(Params params) throws JSONException;

    JSONObject modifierProduitAjustement(Params params) throws JSONException;

    TFamilleStock findStock(String OTFamille, TEmplacement emplacement, EntityManager emg);

    JSONObject cloreAjustement(Params params) throws JSONException;

    JSONObject findOneAjustement(String idAjustement) throws JSONException;

    JSONObject removeAjustementDetail(String id) throws JSONException;

    JSONObject annulerAjustement(String id) throws JSONException;

    JSONObject ajsutements(SalesStatsParams params) throws JSONException;

    JSONObject ajsutementsDetails(SalesStatsParams params, String idAjustement) throws JSONException;

    void updatefamillenbvente(TFamille famille, Integer qty, boolean updatable, EntityManager emg);

    void updateVenteStock(String idVente);

    void updateVenteStock(TPreenregistrement tp, List<TPreenregistrementDetail> list, EntityManager emg);

    void updateVenteStockDepot(TPreenregistrement tp, List<TPreenregistrementDetail> list, EntityManager emg, TEmplacement depot) throws Exception;

    void updateStockDepot(TUser ooTUser, TPreenregistrement op, TEmplacement OTEmplacement, EntityManager emg) throws Exception;

    Typemvtproduit getTypemvtproduitByID(String id, EntityManager emg);

    void updateVenteStock(TUser user, List<TPreenregistrementDetail> list) throws Exception;

}
