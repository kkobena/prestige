/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import dal.TBonLivraison;
import dal.TGrossiste;
import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 *
 * @author koben
 */
public class ErpAchatFournisseurDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String fournisseurId, fournisseurLibelle, mvtDate;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private String numeroBL;
    private long montantHT, montantTVA, montantTTC;
    private String numeroCommade;

    public String getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(String fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public String getFournisseurLibelle() {
        return fournisseurLibelle;
    }

    public void setFournisseurLibelle(String fournisseurLibelle) {
        this.fournisseurLibelle = fournisseurLibelle;
    }

    public String getMvtDate() {
        return mvtDate;
    }

    public void setMvtDate(String mvtDate) {
        this.mvtDate = mvtDate;
    }

    public String getNumeroBL() {
        return numeroBL;
    }

    public void setNumeroBL(String numeroBL) {
        this.numeroBL = numeroBL;
    }

    public long getMontantHT() {
        return montantHT;
    }

    public void setMontantHT(long montantHT) {
        this.montantHT = montantHT;
    }

    public long getMontantTVA() {
        return montantTVA;
    }

    public void setMontantTVA(long montantTVA) {
        this.montantTVA = montantTVA;
    }

    public long getMontantTTC() {
        return montantTTC;
    }

    public void setMontantTTC(long montantTTC) {
        this.montantTTC = montantTTC;
    }

    public ErpAchatFournisseurDTO(TBonLivraison b) {
        TGrossiste g = b.getLgORDERID().getLgGROSSISTEID();
        if (g != null) {
            this.fournisseurId = g.getLgGROSSISTEID();
            this.fournisseurLibelle = g.getStrLIBELLE();
        }
        this.numeroCommade = b.getLgORDERID().getStrREFORDER();
        this.mvtDate = dateFormat.format(b.getDtCREATED());
        this.numeroBL = b.getStrREFLIVRAISON();
        this.montantHT = b.getIntMHT().longValue();
        this.montantTVA = b.getIntTVA().longValue();
        this.montantTTC = b.getIntHTTC().longValue();
    }

    public String getNumeroCommade() {
        return numeroCommade;
    }

    public void setNumeroCommade(String numeroCommade) {
        this.numeroCommade = numeroCommade;
    }

    public ErpAchatFournisseurDTO() {
    }

}
