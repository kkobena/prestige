/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import commonTasks.dto.Params;
import dal.TOrder;
import dal.TUser;
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
public interface CommandeService {

    JSONObject cloturerBonLivraison(String id, TUser user) throws JSONException;

    void closureOrder(TOrder OTOrder, EntityManager em);

    JSONObject cloturerInvetaire(String inventaireId, TUser user) throws JSONException;

    String generateCIP(String int_CIP);

    JSONObject createProduct(Params params) throws JSONException;
    
    default int calculPrixMoyenPondereReception(int ancienStock, int ancienPrixAchat, int nouveauStock, int nouveauPrixAchat) {
        return (ancienStock * ancienPrixAchat) + (nouveauStock * nouveauPrixAchat) / (ancienStock + nouveauStock);
    }
    String genererReferenceCommande();
}
