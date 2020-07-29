/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import dal.TUser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import toolkits.parameters.commonparameter;

/**
 *
 * @author Kobena
 */
public class SalesParams implements Serializable {

    private String typeVenteId, natureVenteId, remiseId, userVendeurId, stockId, produitId,typeDepoId;
    private int qte, qteServie, qteUg=0;
    private String  bonRef="";
    private boolean sansBon;
    private TUser userId;
    private String venteId;
    private String itemId;
    private Integer itemPu,remiseDepot=0;
    private boolean devis=false,depot=false;
    private String clientId;
    private String ayantDroitId;
    private Integer montantTp,totalRecap;
    private String statut=commonparameter.statut_is_Process;
    private String emplacementId;
    private List<TiersPayantParams> tierspayants = new ArrayList<>();

    public String getEmplacementId() {
        return emplacementId;
    }

    public void setEmplacementId(String emplacementId) {
        this.emplacementId = emplacementId;
    }

    public Integer getRemiseDepot() {
        return remiseDepot;
    }

    public void setRemiseDepot(Integer remiseDepot) {
        this.remiseDepot = remiseDepot;
    }

    public String getTypeDepoId() {
        return typeDepoId;
    }

    public void setTypeDepoId(String typeDepoId) {
        this.typeDepoId = typeDepoId;
    }

    public boolean isDepot() {
        return depot;
    }

    public void setDepot(boolean depot) {
        this.depot = depot;
    }
    
    public String getAyantDroitId() {
        return ayantDroitId;
    }

    public String getBonRef() {
        return bonRef;
    }

    public void setBonRef(String bonRef) {
        this.bonRef = bonRef;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setAyantDroitId(String ayantDroitId) {
        this.ayantDroitId = ayantDroitId;
    }

    public Integer getMontantTp() {
        return montantTp;
    }

    public void setMontantTp(Integer montantTp) {
        this.montantTp = montantTp;
    }

    public Integer getTotalRecap() {
        return totalRecap;
    }

    public void setTotalRecap(Integer totalRecap) {
        this.totalRecap = totalRecap;
    }

    public String getVenteId() {
        return venteId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setVenteId(String venteId) {
        this.venteId = venteId;
    }

    public String getTypeVenteId() {
        return typeVenteId;
    }

    public boolean isDevis() {
        return devis;
    }

    public void setDevis(boolean devis) {
        this.devis = devis;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Integer getItemPu() {
        return itemPu;
    }

    public void setItemPu(Integer itemPu) {
        this.itemPu = itemPu;
    }

    public boolean isSansBon() {
        return sansBon;
    }

    public void setSansBon(boolean sansBon) {
        this.sansBon = sansBon;
    }

    public TUser getUserId() {
        return userId;
    }

    public void setUserId(TUser userId) {
        this.userId = userId;
    }

    public SalesParams() {
    }

    public SalesParams(String typeVenteId, String natureVenteId, String remiseId, String userVendeurId, String stockId, String produitId, int qte, int qteServie, int qteUg) {
        this.typeVenteId = typeVenteId;
        this.natureVenteId = natureVenteId;
        this.remiseId = remiseId;
        this.userVendeurId = userVendeurId;
        this.stockId = stockId;
        this.produitId = produitId;
        this.qte = qte;
        this.qteServie = qteServie;
        this.qteUg = qteUg;
    }

    public void setTypeVenteId(String typeVenteId) {
        this.typeVenteId = typeVenteId;
    }

    public String getNatureVenteId() {
        return natureVenteId;
    }

    public void setNatureVenteId(String natureVenteId) {
        this.natureVenteId = natureVenteId;
    }

    public String getRemiseId() {
        return remiseId;
    }

    public void setRemiseId(String remiseId) {
        this.remiseId = remiseId;
    }

    public String getUserVendeurId() {
        return userVendeurId;
    }

    public void setUserVendeurId(String userVendeurId) {
        this.userVendeurId = userVendeurId;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getProduitId() {
        return produitId;
    }

    public void setProduitId(String produitId) {
        this.produitId = produitId;
    }

    public int getQte() {
        return qte;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public int getQteServie() {
        return qteServie;
    }

    public void setQteServie(int qteServie) {
        this.qteServie = qteServie;
    }

    public int getQteUg() {
        return qteUg;
    }

    public void setQteUg(int qteUg) {
        this.qteUg = qteUg;
    }

    public List<TiersPayantParams> getTierspayants() {
        return tierspayants;
    }

    public void setTierspayants(List<TiersPayantParams> tierspayants) {
        this.tierspayants = tierspayants;
    }

    @Override
    public String toString() {
        return "SalesParams{" + "typeVenteId=" + typeVenteId + ", natureVenteId=" + natureVenteId + ", remiseId=" + remiseId + ", userVendeurId=" + userVendeurId + ", stockId=" + stockId + ", produitId=" + produitId + ", qte=" + qte + ", qteServie=" + qteServie + ", qteUg=" + qteUg + ", tierspayants=" + tierspayants + '}';
    }

}
