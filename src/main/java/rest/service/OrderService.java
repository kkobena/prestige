/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import commonTasks.dto.ArticleDTO;
import commonTasks.dto.GenererFactureDTO;
import commonTasks.dto.Params;
import commonTasks.dto.RuptureDTO;
import commonTasks.dto.RuptureDetailDTO;
import dal.Rupture;
import dal.RuptureDetail;
import dal.TFamille;
import dal.TFamilleGrossiste;
import dal.TGrossiste;
import dal.TOrder;
import dal.TOrderDetail;
import dal.TUser;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Local;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author DICI
 */
@Local
//@Remote
public interface OrderService {

    List<RuptureDetail> ruptureDetaisDtoByRupture(String idRupture);

    JSONObject creerBonLivraison(Params params) throws JSONException;

    TOrder findByRef(String reference, String idCommande);

    List<TOrderDetail> findByOrderId(String idCommande);

    void changeOrderStatuts(TOrder order);

    void removeItemsFromOrder(List<TOrderDetail> items);

    TOrderDetail findByCipAndOrderId(String codeCip, String idCommande);

    Rupture creerRupture(TOrder order);

    void creerRuptureItem(Rupture rupture, TFamille famille, int qty);

    JSONObject removeRupture(String id);

    List<RuptureDTO> listeRuptures(LocalDate dtStart, LocalDate dtEnd, String query, String grossisteId, int start, int limit, boolean all);

    JSONObject listeRuptures(LocalDate dtStart, LocalDate dtEnd, String query, String grossisteId, int start, int limit) throws JSONException;

    List<RuptureDetailDTO> listeRuptures(LocalDate dtStart, LocalDate dtEnd, String query, String grossisteId, String emplacementId);

    int findProduitStock(String idProduit, String emplacementId);

    JSONObject creerRupture(GenererFactureDTO datas) throws JSONException;

    TFamilleGrossiste findOrCreateFamilleGrossiste(TFamille famille, TGrossiste grossiste);

    TFamilleGrossiste finFamilleGrossisteByIdFamilleAndIdGrossiste(String id, String grossisteId);

    TFamilleGrossiste finFamilleGrossisteByFamilleCipAndIdGrossiste(String id, String grossisteId);

    TOrder createOrder(TGrossiste grossiste, TUser u);

    RuptureDetail ruptureDetaisByRuptureAndProduitId(String idRupture, String produitCip);

    TOrderDetail modificationProduitCommandeEncours(ArticleDTO dto,TUser user) throws Exception;

    JSONObject supprimerProduitCommandeEncours(String idCommande) throws JSONException;

}
