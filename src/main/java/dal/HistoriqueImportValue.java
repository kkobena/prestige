/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import java.io.Serializable;

/**
 *
 * @author koben
 */
public class HistoriqueImportValue implements Serializable {

    private static final long serialVersionUID = 1L;
    private int prixPaf;
    private int prixUni;
    private int qty;
    private int stockInit;
    private int montantAchat;
    private int montantVente;
    private int stockOfDay;
    private String cip;
    private String libelle;

    public int getPrixPaf() {
        return prixPaf;
    }

    public void setPrixPaf(int prixPaf) {
        this.prixPaf = prixPaf;
    }

    public int getPrixUni() {
        return prixUni;
    }

    public HistoriqueImportValue prixPaf(int prixPaf) {
        this.prixPaf = prixPaf;
        return this;
    }

    public void setPrixUni(int prixUni) {
        this.prixUni = prixUni;
    }

    public HistoriqueImportValue prixUni(int prixUni) {
        this.prixUni = prixUni;
        return this;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public HistoriqueImportValue qty(int qty) {
        this.qty = qty;
        return this;
    }

    public int getStockInit() {
        return stockInit;
    }

    public void setStockInit(int stockInit) {
        this.stockInit = stockInit;
    }

    public HistoriqueImportValue stockInit(int stockInit) {
        this.stockInit = stockInit;
        return this;
    }

    public int getMontantAchat() {
        return montantAchat;
    }

    public void setMontantAchat(int montantAchat) {
        this.montantAchat = montantAchat;
    }

    public HistoriqueImportValue montantAchat(int montantAchat) {
        this.montantAchat = montantAchat;
        return this;
    }

    public int getMontantVente() {
        return montantVente;
    }

    public void setMontantVente(int montantVente) {
        this.montantVente = montantVente;
    }

    public HistoriqueImportValue montantVente(int montantVente) {
        this.montantVente = montantVente;
        return this;
    }

    public int getStockOfDay() {
        return stockOfDay;
    }

    public void setStockOfDay(int stockOfDay) {
        this.stockOfDay = stockOfDay;
    }

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

    public HistoriqueImportValue stockOfDay(int stockOfDay) {
        this.stockOfDay = stockOfDay;
        return this;
    }

    public HistoriqueImportValue libelle(String libelle) {
        this.libelle = libelle;
        return this;
    }

    public HistoriqueImportValue cip(String cip) {
        this.cip = cip;
        return this;
    }

    public HistoriqueImportValue() {
    }

}
