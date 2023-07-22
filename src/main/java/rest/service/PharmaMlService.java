/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import dal.TUser;
import java.time.LocalDate;
import javax.ejb.Local;
import org.json.JSONObject;

/**
 *
 * @author kkoffi
 */
@Local
public interface PharmaMlService {

    JSONObject envoiPharmaCommande(String commandeId, LocalDate dateLivraisonSouhaitee, int typeCommande,
            String typeCommandeExecptionel, String commentaire);

    JSONObject envoiPharmaInfosProduit(String commandeId);

    JSONObject lignesCommandeRetour(String commandeRef, String orderId);

    JSONObject renvoiPharmaCommande(String ruptureId, String grossiste, LocalDate dateLivraisonSouhaitee,
            int typeCommande, String typeCommandeExecptionel, String commentaire);

    JSONObject reponseRupture(String ruptureId, TUser u);

    void test();
}
