
/**
 *
 * @author airman
 */

package rest.service.dto;

import java.io.Serializable;

public class GroupeInvoiceDTO implements Serializable {

    private Integer lg_GROUPE_ID;
    private Integer NBFACTURES;
    private String STATUT;
    private boolean ACTION_REGLER_FACTURE;
    private String CODEFACTURE;
    private Long AMOUNT;
    private String str_LIB;
    private Long MONTANTRESTANT;
    private Long AMOUNTPAYE;
    private String DATECREATION;

    public Integer getLg_GROUPE_ID() {
        return lg_GROUPE_ID;
    }

    public void setLg_GROUPE_ID(Integer lg_GROUPE_ID) {
        this.lg_GROUPE_ID = lg_GROUPE_ID;
    }

    public Integer getNBFACTURES() {
        return NBFACTURES;
    }

    public void setNBFACTURES(Integer NBFACTURES) {
        this.NBFACTURES = NBFACTURES;
    }

    public String getSTATUT() {
        return STATUT;
    }

    public void setSTATUT(String STATUT) {
        this.STATUT = STATUT;
    }

    public boolean isACTION_REGLER_FACTURE() {
        return ACTION_REGLER_FACTURE;
    }

    public void setACTION_REGLER_FACTURE(boolean ACTION_REGLER_FACTURE) {
        this.ACTION_REGLER_FACTURE = ACTION_REGLER_FACTURE;
    }

    public String getCODEFACTURE() {
        return CODEFACTURE;
    }

    public void setCODEFACTURE(String CODEFACTURE) {
        this.CODEFACTURE = CODEFACTURE;
    }

    public Long getAMOUNT() {
        return AMOUNT;
    }

    public void setAMOUNT(Long AMOUNT) {
        this.AMOUNT = AMOUNT;
    }

    public String getStr_LIB() {
        return str_LIB;
    }

    public void setStr_LIB(String str_LIB) {
        this.str_LIB = str_LIB;
    }

    public Long getMONTANTRESTANT() {
        return MONTANTRESTANT;
    }

    public void setMONTANTRESTANT(Long MONTANTRESTANT) {
        this.MONTANTRESTANT = MONTANTRESTANT;
    }

    public Long getAMOUNTPAYE() {
        return AMOUNTPAYE;
    }

    public void setAMOUNTPAYE(Long AMOUNTPAYE) {
        this.AMOUNTPAYE = AMOUNTPAYE;
    }

    public String getDATECREATION() {
        return DATECREATION;
    }

    public void setDATECREATION(String DATECREATION) {
        this.DATECREATION = DATECREATION;
    }
}
