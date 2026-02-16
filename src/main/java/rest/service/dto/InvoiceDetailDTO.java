package rest.service.dto;

import java.io.Serializable;

/**
 *
 * @author airman
 */

public class InvoiceDetailDTO implements Serializable {

    private String lg_FACTURE_ID;
    private String str_CODE_FACTURE;
    private Integer int_NB_DOSSIER;
    private String dt_CREATED;
    private String str_STATUT;

    private String str_CUSTOMER_NAME;
    private String str_PERIODE;

    private Long dbl_MONTANT_CMDE;
    private Long dbl_MONTANT_RESTANT;
    private Long dbl_MONTANT_PAYE;

    private Long MONTANTREMISE;
    private Long MONTANTFORFETAIRE;
    private Long MONTANTBRUT;

    private boolean isChecked;

    public String getLg_FACTURE_ID() {
        return lg_FACTURE_ID;
    }

    public void setLg_FACTURE_ID(String lg_FACTURE_ID) {
        this.lg_FACTURE_ID = lg_FACTURE_ID;
    }

    public String getStr_CODE_FACTURE() {
        return str_CODE_FACTURE;
    }

    public void setStr_CODE_FACTURE(String str_CODE_FACTURE) {
        this.str_CODE_FACTURE = str_CODE_FACTURE;
    }

    public Integer getInt_NB_DOSSIER() {
        return int_NB_DOSSIER;
    }

    public void setInt_NB_DOSSIER(Integer int_NB_DOSSIER) {
        this.int_NB_DOSSIER = int_NB_DOSSIER;
    }

    public String getDt_CREATED() {
        return dt_CREATED;
    }

    public void setDt_CREATED(String dt_CREATED) {
        this.dt_CREATED = dt_CREATED;
    }

    public String getStr_STATUT() {
        return str_STATUT;
    }

    public void setStr_STATUT(String str_STATUT) {
        this.str_STATUT = str_STATUT;
    }

    public String getStr_CUSTOMER_NAME() {
        return str_CUSTOMER_NAME;
    }

    public void setStr_CUSTOMER_NAME(String str_CUSTOMER_NAME) {
        this.str_CUSTOMER_NAME = str_CUSTOMER_NAME;
    }

    public String getStr_PERIODE() {
        return str_PERIODE;
    }

    public void setStr_PERIODE(String str_PERIODE) {
        this.str_PERIODE = str_PERIODE;
    }

    public Long getDbl_MONTANT_CMDE() {
        return dbl_MONTANT_CMDE;
    }

    public void setDbl_MONTANT_CMDE(Long dbl_MONTANT_CMDE) {
        this.dbl_MONTANT_CMDE = dbl_MONTANT_CMDE;
    }

    public Long getDbl_MONTANT_RESTANT() {
        return dbl_MONTANT_RESTANT;
    }

    public void setDbl_MONTANT_RESTANT(Long dbl_MONTANT_RESTANT) {
        this.dbl_MONTANT_RESTANT = dbl_MONTANT_RESTANT;
    }

    public Long getDbl_MONTANT_PAYE() {
        return dbl_MONTANT_PAYE;
    }

    public void setDbl_MONTANT_PAYE(Long dbl_MONTANT_PAYE) {
        this.dbl_MONTANT_PAYE = dbl_MONTANT_PAYE;
    }

    public Long getMONTANTREMISE() {
        return MONTANTREMISE;
    }

    public void setMONTANTREMISE(Long MONTANTREMISE) {
        this.MONTANTREMISE = MONTANTREMISE;
    }

    public Long getMONTANTFORFETAIRE() {
        return MONTANTFORFETAIRE;
    }

    public void setMONTANTFORFETAIRE(Long MONTANTFORFETAIRE) {
        this.MONTANTFORFETAIRE = MONTANTFORFETAIRE;
    }

    public Long getMONTANTBRUT() {
        return MONTANTBRUT;
    }

    public void setMONTANTBRUT(Long MONTANTBRUT) {
        this.MONTANTBRUT = MONTANTBRUT;
    }

    public boolean isIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
