/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import dal.TResumeCaisse;
import dal.TUser;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Objects;
import util.DateConverter;

/**
 *
 * @author DICI
 */
public class ResumeCaisseDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private String ldCAISSEID, dtCREATED, statut, dtUPDATED, strSTATUT, userFullName;
    private Integer intSOLDEMATIN = 0, soldeTotal = 0, intSOLDESOIR = 0, billetage = 0, ecart = 0, montantAnnule = 0;
    boolean cancel = false;

    public String getLdCAISSEID() {
        return ldCAISSEID;
    }

    public Integer getSoldeTotal() {
        return soldeTotal;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setSoldeTotal(Integer soldeTotal) {
        this.soldeTotal = soldeTotal;
    }

    public void setLdCAISSEID(String ldCAISSEID) {
        this.ldCAISSEID = ldCAISSEID;
    }

    public String getDtCREATED() {
        return dtCREATED;
    }

    public void setDtCREATED(String dtCREATED) {
        this.dtCREATED = dtCREATED;
    }

    public String getDtUPDATED() {
        return dtUPDATED;
    }

    public void setDtUPDATED(String dtUPDATED) {
        this.dtUPDATED = dtUPDATED;
    }

    public String getStrSTATUT() {
        return strSTATUT;
    }

    public void setStrSTATUT(String strSTATUT) {
        this.strSTATUT = strSTATUT;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public Integer getIntSOLDEMATIN() {
        return intSOLDEMATIN;
    }

    public void setIntSOLDEMATIN(Integer intSOLDEMATIN) {
        this.intSOLDEMATIN = intSOLDEMATIN;
    }

    public Integer getIntSOLDESOIR() {
        return intSOLDESOIR;
    }

    public void setIntSOLDESOIR(Integer intSOLDESOIR) {
        this.intSOLDESOIR = intSOLDESOIR;
    }

    public Integer getBilletage() {
        return billetage;
    }

    public void setBilletage(Integer billetage) {
        this.billetage = billetage;
    }

    public Integer getEcart() {
        return ecart;
    }

    public void setEcart(Integer ecart) {
        this.ecart = ecart;
    }

    public Integer getMontantAnnule() {
        return montantAnnule;
    }

    public void setMontantAnnule(Integer montantAnnule) {
        this.montantAnnule = montantAnnule;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.ldCAISSEID);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResumeCaisseDTO other = (ResumeCaisseDTO) obj;
        return Objects.equals(this.ldCAISSEID, other.ldCAISSEID);
    }

    public ResumeCaisseDTO(TResumeCaisse caisse, Integer montantBilletage, Integer montantAnnule, boolean cancel) {

        this.ldCAISSEID = caisse.getLdCAISSEID();
        this.dtCREATED = dateFormat.format(caisse.getDtCREATED());
        try {
            this.dtUPDATED = dateFormat.format(caisse.getDtUPDATED());
        } catch (Exception e) {
            this.dtUPDATED = "PAS ENCORE FERMEE";
        }
        TUser u = caisse.getLgUSERID();
        this.userFullName = u.getStrFIRSTNAME() + " " + u.getStrLASTNAME();
        this.billetage = montantBilletage;
        this.montantAnnule = Math.abs(montantAnnule);
        this.intSOLDEMATIN = caisse.getIntSOLDEMATIN();
        this.ecart = montantBilletage - (Math.abs(caisse.getIntSOLDESOIR()-caisse.getIntSOLDEMATIN()) - montantAnnule);
        this.cancel = cancel;
        this.soldeTotal = caisse.getIntSOLDESOIR();
        this.statut = caisse.getStrSTATUT();
        if (caisse.getStrSTATUT().equals(DateConverter.STATUT_IS_IN_USE)) {
            this.intSOLDESOIR = caisse.getIntSOLDESOIR();
            this.strSTATUT = "En cours d'utilisation ";
        } else {
            this.intSOLDESOIR = caisse.getIntSOLDESOIR() - caisse.getIntSOLDEMATIN();
            if (caisse.getStrSTATUT().equals(DateConverter.STATUT_PROCESS)) {
                this.strSTATUT = "Fermée ";

            }
        }

    }

}
