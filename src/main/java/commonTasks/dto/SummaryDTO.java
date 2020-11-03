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
public class SummaryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    Integer montantTTC = 0, montantNet = 0, montantRemise = 0, pourcentage = 0, panierMoyen = 0,
            montantEsp = 0, montantCheque=0, montantVirement = 0,
            montantCB = 0, montantTp = 0, montantDiff = 0, nbreVente = 0, 
            fondCaisse = 0, montantRegDiff = 0,montantMobilePayment=0,
            montantRegleTp = 0, montantEntre = 0, 
            montantAchat = 0, montantSortie = 0, marge = 0,montantTva=0,montantHT=0;
    double ratioVA = 0.0, rationAV = 0.0;

    public Integer getMontantTTC() {
        return montantTTC;
    }

    public Integer getMontantTva() {
        return montantTva;
    }

    public Integer getMontantHT() {
        return montantHT;
    }

    public Integer getMontantMobilePayment() {
        return montantMobilePayment;
    }

    public void setMontantMobilePayment(Integer montantMobilePayment) {
        this.montantMobilePayment = montantMobilePayment;
    }

    public void setMontantHT(Integer montantHT) {
        this.montantHT = montantHT;
    }

    public void setMontantTva(Integer montantTva) {
        this.montantTva = montantTva;
    }

    public void setMontantTTC(Integer montantTTC) {
        this.montantTTC = montantTTC;
    }

    public Integer getMontantNet() {
        return montantNet;
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

    public Integer getPourcentage() {
        return pourcentage;
    }

    public void setPourcentage(Integer pourcentage) {
        this.pourcentage = pourcentage;
    }

    public Integer getPanierMoyen() {
        return panierMoyen;
    }

    public void setPanierMoyen(Integer panierMoyen) {
        this.panierMoyen = panierMoyen;
    }

    public Integer getMontantEsp() {
        return montantEsp;
    }

    public void setMontantEsp(Integer montantEsp) {
        this.montantEsp = montantEsp;
    }

    public Integer getMontantCheque() {
        return montantCheque;
    }

    public void setMontantCheque(Integer montantCheque) {
        this.montantCheque = montantCheque;
    }

    public Integer getMontantVirement() {
        return montantVirement;
    }

    public void setMontantVirement(Integer MontantVirement) {
        this.montantVirement = MontantVirement;
    }

    public Integer getMontantCB() {
        return montantCB;
    }

    public void setMontantCB(Integer montantCB) {
        this.montantCB = montantCB;
    }

    public Integer getMontantTp() {
        return montantTp;
    }

    public void setMontantTp(Integer montantTp) {
        this.montantTp = montantTp;
    }

    public Integer getMontantDiff() {
        return montantDiff;
    }

    public void setMontantDiff(Integer montantDiff) {
        this.montantDiff = montantDiff;
    }

    public Integer getNbreVente() {
        return nbreVente;
    }

    public void setNbreVente(Integer nbreVente) {
        this.nbreVente = nbreVente;
    }

    public Integer getFondCaisse() {
        return fondCaisse;
    }

    public void setFondCaisse(Integer fondCaisse) {
        this.fondCaisse = fondCaisse;
    }

    public Integer getMontantRegDiff() {
        return montantRegDiff;
    }

    public void setMontantRegDiff(Integer montantRegDiff) {
        this.montantRegDiff = montantRegDiff;
    }

    public Integer getMontantRegleTp() {
        return montantRegleTp;
    }

    public void setMontantRegleTp(Integer montantRegleTp) {
        this.montantRegleTp = montantRegleTp;
    }

    public Integer getMontantEntre() {
        return montantEntre;
    }

    public void setMontantEntre(Integer montantEntre) {
        this.montantEntre = montantEntre;
    }

    public Integer getMontantAchat() {
        return montantAchat;
    }

    public void setMontantAchat(Integer montantAchat) {
        this.montantAchat = montantAchat;
    }

    public Integer getMontantSortie() {
        return montantSortie;
    }

    public void setMontantSortie(Integer montantSortie) {
        this.montantSortie = montantSortie;
    }

    public Integer getMarge() {
        return marge;
    }

    public void setMarge(Integer marge) {
        this.marge = marge;
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

    @Override
    public String toString() {
        return "SummaryDTO{" + "montantTTC=" + montantTTC + ", montantNet=" + montantNet + ", montantRemise=" + montantRemise + ", pourcentage=" + pourcentage + ", panierMoyen=" + panierMoyen + ", montantEsp=" + montantEsp + ", montantCheque=" + montantCheque + ", MontantVirement=" + montantVirement + ", montantCB=" + montantCB + ", montantTp=" + montantTp + ", montantDiff=" + montantDiff + ", nbreVente=" + nbreVente + ", fondCaisse=" + fondCaisse + ", montantRegDiff=" + montantRegDiff + ", montantRegleTp=" + montantRegleTp + ", montantEntre=" + montantEntre + ", montantAchat=" + montantAchat + ", montantSortie=" + montantSortie + ", marge=" + marge + ", ratioVA=" + ratioVA + ", rationAV=" + rationAV + '}';
    }

    
    
}
