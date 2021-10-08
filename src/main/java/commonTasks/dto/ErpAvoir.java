/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import dal.TBonLivraison;
import dal.TFamille;
import dal.TMotifRetour;
import dal.TRetourFournisseurDetail;
import util.DateConverter;

/**
 *
 * @author koben
 */
public class ErpAvoir {

    private String cip, libelle, dateAvoir, numeroBl, natureReclamation;
    private Integer quantite, prixAchatHt;

    public String getCip() {
        return cip;
    }

    public void setCip(String cip) {
        this.cip = cip;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDateAvoir() {
        return dateAvoir;
    }

    public void setDateAvoir(String dateAvoir) {
        this.dateAvoir = dateAvoir;
    }

    public String getNumeroBl() {
        return numeroBl;
    }

    public void setNumeroBl(String numeroBl) {
        this.numeroBl = numeroBl;
    }

    public String getNatureReclamation() {
        return natureReclamation;
    }

    public void setNatureReclamation(String natureReclamation) {
        this.natureReclamation = natureReclamation;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public Integer getPrixAchatHt() {
        return prixAchatHt;
    }

    public void setPrixAchatHt(Integer prixAchatHt) {
        this.prixAchatHt = prixAchatHt;
    }

    public ErpAvoir(TRetourFournisseurDetail o) {
        TFamille famille = o.getLgFAMILLEID();
        this.cip = famille.getIntCIP();
        this.libelle = famille.getStrNAME();
        this.dateAvoir = DateConverter.convertDateToDD_MM_YYYY_HH_mm(o.getDtUPDATED());
        TBonLivraison b = o.getLgRETOURFRSID().getLgBONLIVRAISONID();
        if (b != null) {
            this.numeroBl = b.getStrREFLIVRAISON();
        }
        TMotifRetour motifRetour = o.getLgMOTIFRETOUR();
        if (motifRetour != null) {
            this.natureReclamation = motifRetour.getStrLIBELLE();
        }

        this.quantite = o.getIntNUMBERRETURN();
        this.prixAchatHt = o.getIntPAF();
    }

}
