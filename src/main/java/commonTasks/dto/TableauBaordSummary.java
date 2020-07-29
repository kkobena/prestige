/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import java.io.Serializable;

/**
 *
 * @author DICI
 */
public class TableauBaordSummary implements Serializable {

    private static final long serialVersionUID = 1L;
    Integer montantTTC = 0, montantNet = 0, montantRemise = 0,
            montantEsp = 0,
            montantCredit = 0,
            nbreVente = 0, montantAchatOne = 0, montantAchatTwo = 0,
            montantAchatThree = 0, montantAchatFour = 0, montantAchatFive = 0,
            montantAchat = 0, montantAchatNet = 0,montantAvoir=0;
    double ratioVA = 0.0, rationAV = 0.0;

    public Integer getMontantTTC() {
        return montantTTC;
    }

    public void setMontantTTC(Integer montantTTC) {
        this.montantTTC = montantTTC;
    }

    public Integer getMontantNet() {
        return montantNet;
    }

    public Integer getMontantAvoir() {
        return montantAvoir;
    }

    public void setMontantAvoir(Integer montantAvoir) {
        this.montantAvoir = montantAvoir;
    }

    public void setMontantNet(Integer montantNet) {
        this.montantNet = montantNet;
    }

    public Integer getMontantRemise() {
        return montantRemise;
    }

    public void setMontantRemise(Integer montantRemise) {
        this.montantRemise = montantRemise;
    }

    public Integer getMontantEsp() {
        return montantEsp;
    }

    public void setMontantEsp(Integer montantEsp) {
        this.montantEsp = montantEsp;
    }

    public Integer getMontantCredit() {
        return montantCredit;
    }

    public void setMontantCredit(Integer montantCredit) {
        this.montantCredit = montantCredit;
    }

    public Integer getNbreVente() {
        return nbreVente;
    }

    public void setNbreVente(Integer nbreVente) {
        this.nbreVente = nbreVente;
    }

    public Integer getMontantAchatOne() {
        return montantAchatOne;
    }

    public void setMontantAchatOne(Integer montantAchatOne) {
        this.montantAchatOne = montantAchatOne;
    }

    public Integer getMontantAchatTwo() {
        return montantAchatTwo;
    }

    public void setMontantAchatTwo(Integer montantAchatTwo) {
        this.montantAchatTwo = montantAchatTwo;
    }

    public Integer getMontantAchatThree() {
        return montantAchatThree;
    }

    public void setMontantAchatThree(Integer montantAchatThree) {
        this.montantAchatThree = montantAchatThree;
    }

    public Integer getMontantAchatFour() {
        return montantAchatFour;
    }

    public void setMontantAchatFour(Integer montantAchatFour) {
        this.montantAchatFour = montantAchatFour;
    }

    public Integer getMontantAchatFive() {
        return montantAchatFive;
    }

    public void setMontantAchatFive(Integer montantAchatFive) {
        this.montantAchatFive = montantAchatFive;
    }

    public Integer getMontantAchat() {
        return montantAchat;
    }

    public void setMontantAchat(Integer montantAchat) {
        this.montantAchat = montantAchat;
    }

    public Integer getMontantAchatNet() {
        return montantAchatNet;
    }

    public void setMontantAchatNet(Integer montantAchatNet) {
        this.montantAchatNet = montantAchatNet;
    }

    public double getRatioVA() {
        return ratioVA;
    }

    public void setRatioVA(double ratioVA) {
        this.ratioVA = ratioVA;
    }

    public double getRationAV() {
        return rationAV;
    }

    public void setRationAV(double rationAV) {
        this.rationAV = rationAV;
    }
    
}
