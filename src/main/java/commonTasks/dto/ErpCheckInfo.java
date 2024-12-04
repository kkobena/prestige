/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commonTasks.dto;

import dal.TBonLivraisonDetail;
import dal.TFamille;
import dal.TFamilleStock;

import java.io.Serializable;

/**
 *
 * @author Hermann N'ZI
 */
public class ErpCheckInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String familleId;
    private String familleCip;
    private String familleLibelle;
    private Integer pachat;
    private Integer pvente;
    private Integer stock;
    private String emplacement;
    private String dateLast;
    private Integer qteLast;

    public String getDateLast() {
        return dateLast;
    }

    public void setDateLast(String dateLast) {
        this.dateLast = dateLast;
    }

    public Integer getQteLast() {
        return qteLast;
    }

    public void setQteLast(Integer qteLast) {
        this.qteLast = qteLast;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getFamilleId() {
        return familleId;
    }

    public void setFamilleId(String familleId) {
        this.familleId = familleId;
    }

    public String getFamilleCip() {
        return familleCip;
    }

    public void setFamilleCip(String familleCip) {
        this.familleCip = familleCip;
    }

    public String getFamilleLibelle() {
        return familleLibelle;
    }

    public void setFamilleLibelle(String familleLibelle) {
        this.familleLibelle = familleLibelle;
    }

    public Integer getPachat() {
        return pachat;
    }

    public void setPachat(Integer pachat) {
        this.pachat = pachat;
    }

    public Integer getPvente() {
        return pvente;
    }

    public void setPvente(Integer pvente) {
        this.pvente = pvente;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public ErpCheckInfo(TBonLivraisonDetail b, TFamilleStock fs) {

        TFamille f = b.getLgFAMILLEID();

        this.familleId = f.getLgFAMILLEID();
        this.familleCip = f.getIntCIP();
        this.familleLibelle = f.getStrNAME();
        this.pachat = f.getIntPAF();
        this.pvente = f.getIntPRICE();
        this.stock = fs.getIntNUMBER();
        this.emplacement = f.getLgZONEGEOID().getStrLIBELLEE();
        this.dateLast = f.getDtDATELASTENTREE().toString();
        this.qteLast = b.getIntQTERECUE();

    }

    public ErpCheckInfo() {
    }
}
