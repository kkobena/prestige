/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import dal.HMvtProduit;
import dal.TBonLivraisonDetail;
import dal.TUser;
import dal.TWarehouse;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import util.DateConverter;

/**
 *
 * @author DICI
 */
public class LotItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String operateur, dtCREATED, HEURE, peremption, grossiste;
    private Date dateOperation;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat heureFormat = new SimpleDateFormat("HH:mm");
    private String intNUMLOT;
    private Integer intNUMBERGRATUIT = 0, amount = 0, intNUMBER;

    public String getOperateur() {
        return operateur;
    }

    public Integer getIntNUMBER() {
        return intNUMBER;
    }

    public void setIntNUMBER(Integer intNUMBER) {
        this.intNUMBER = intNUMBER;
    }

    public void setOperateur(String operateur) {
        this.operateur = operateur;
    }

    public String getDtCREATED() {
        return dtCREATED;
    }

    public void setDtCREATED(String dtCREATED) {
        this.dtCREATED = dtCREATED;
    }

    public String getHEURE() {
        return HEURE;
    }

    public void setHEURE(String HEURE) {
        this.HEURE = HEURE;
    }

    public String getPeremption() {
        return peremption;
    }

    public void setPeremption(String peremption) {
        this.peremption = peremption;
    }

    public String getGrossiste() {
        return grossiste;
    }

    public void setGrossiste(String grossiste) {
        this.grossiste = grossiste;
    }

    public Date getDateOperation() {
        return dateOperation;
    }

    public void setDateOperation(Date dateOperation) {
        this.dateOperation = dateOperation;
    }

    public String getIntNUMLOT() {
        return intNUMLOT;
    }

    public void setIntNUMLOT(String intNUMLOT) {
        this.intNUMLOT = intNUMLOT;
    }

    public Integer getIntNUMBERGRATUIT() {
        return intNUMBERGRATUIT;
    }

    public void setIntNUMBERGRATUIT(Integer intNUMBERGRATUIT) {
        this.intNUMBERGRATUIT = intNUMBERGRATUIT;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public LotItemDTO(TWarehouse tw, TBonLivraisonDetail d) {
        TUser u = tw.getLgUSERID();
        this.operateur = u.getStrFIRSTNAME() + " " + u.getStrLASTNAME();
        this.dtCREATED = dateFormat.format(tw.getDtUPDATED());
        this.HEURE = heureFormat.format(tw.getDtUPDATED());
        try {
            this.peremption = dateFormat.format(tw.getDtPEREMPTION());
        } catch (Exception e) {
        }
        this.grossiste = tw.getLgGROSSISTEID().getStrLIBELLE();
        this.dateOperation = tw.getDtUPDATED();
        this.intNUMLOT = tw.getIntNUMLOT();
        this.intNUMBER = tw.getIntNUMBER();
        this.intNUMBERGRATUIT = tw.getIntNUMBERGRATUIT();
        this.amount = (tw.getIntNUMBER() * d.getIntPAF());
    }

    public LotItemDTO(HMvtProduit tw) {
        TUser u = tw.getLgUSERID();
        this.operateur = u.getStrFIRSTNAME() + " " + u.getStrLASTNAME();
        this.dtCREATED = tw.getMvtDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.HEURE = tw.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm"));
        this.dateOperation = DateConverter.convertLocalDateTimeToDate(tw.getCreatedAt());
        this.intNUMBER = tw.getQteMvt();
        this.amount = (tw.getQteMvt() * tw.getPrixAchat());
    }

    public LotItemDTO(HMvtProduit tw, TWarehouse warehouse) {
        TUser u = tw.getLgUSERID();
        this.operateur = u.getStrFIRSTNAME() + " " + u.getStrLASTNAME();
        this.dtCREATED = tw.getMvtDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.HEURE = tw.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm"));
        this.dateOperation = DateConverter.convertLocalDateTimeToDate(tw.getCreatedAt());
        this.intNUMBER = tw.getQteMvt();
        try {
            this.peremption = dateFormat.format(warehouse.getDtPEREMPTION());
        } catch (Exception e) {
        }
        this.intNUMLOT = warehouse.getIntNUMLOT();
        this.amount = (tw.getQteMvt() * tw.getPrixUn());
    }

}
