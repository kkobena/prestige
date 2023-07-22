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
    long montantTTC = 0, montantNet = 0, montantRemise = 0, montantEsp = 0, montantCredit = 0, nbreVente = 0,
            montantAchatOne = 0, montantAchatTwo = 0, montantAchatThree = 0, montantAchatFour = 0, montantAchatFive = 0,
            montantAchat = 0, montantAchatNet = 0, montantAvoir = 0;
    double ratioVA = 0.0, rationAV = 0.0;

    public long getMontantTTC() {
        return montantTTC;
    }

    public void setMontantTTC(long montantTTC) {
        this.montantTTC = montantTTC;
    }

    public long getMontantNet() {
        return montantNet;
    }

    public long getMontantAvoir() {
        return montantAvoir;
    }

    public void setMontantAvoir(long montantAvoir) {
        this.montantAvoir = montantAvoir;
    }

    public void setMontantNet(long montantNet) {
        this.montantNet = montantNet;
    }

    public long getMontantRemise() {
        return montantRemise;
    }

    public void setMontantRemise(long montantRemise) {
        this.montantRemise = montantRemise;
    }

    public long getMontantEsp() {
        return montantEsp;
    }

    public void setMontantEsp(long montantEsp) {
        this.montantEsp = montantEsp;
    }

    public long getMontantCredit() {
        return montantCredit;
    }

    public void setMontantCredit(long montantCredit) {
        this.montantCredit = montantCredit;
    }

    public long getNbreVente() {
        return nbreVente;
    }

    public void setNbreVente(long nbreVente) {
        this.nbreVente = nbreVente;
    }

    public long getMontantAchatOne() {
        return montantAchatOne;
    }

    public void setMontantAchatOne(long montantAchatOne) {
        this.montantAchatOne = montantAchatOne;
    }

    public long getMontantAchatTwo() {
        return montantAchatTwo;
    }

    public void setMontantAchatTwo(long montantAchatTwo) {
        this.montantAchatTwo = montantAchatTwo;
    }

    public long getMontantAchatThree() {
        return montantAchatThree;
    }

    public void setMontantAchatThree(long montantAchatThree) {
        this.montantAchatThree = montantAchatThree;
    }

    public long getMontantAchatFour() {
        return montantAchatFour;
    }

    public void setMontantAchatFour(long montantAchatFour) {
        this.montantAchatFour = montantAchatFour;
    }

    public long getMontantAchatFive() {
        return montantAchatFive;
    }

    public void setMontantAchatFive(long montantAchatFive) {
        this.montantAchatFive = montantAchatFive;
    }

    public long getMontantAchat() {
        return montantAchat;
    }

    public void setMontantAchat(long montantAchat) {
        this.montantAchat = montantAchat;
    }

    public long getMontantAchatNet() {
        return montantAchatNet;
    }

    public void setMontantAchatNet(long montantAchatNet) {
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
