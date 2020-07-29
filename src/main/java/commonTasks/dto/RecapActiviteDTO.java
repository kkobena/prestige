/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DICI
 */
public class RecapActiviteDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer montantTTC = 0,
            montantHT = 0, montantTVA = 0, 
            pourcentageEsp = 0, pourcentageCredit = 0;
    private Integer montantNet = 0,marge=0;
    private Integer montantRemise = 0;
    private Integer montantEsp = 0;
    private Integer montantCredit = 0;
    private Integer montantTotalMvt = 0,montantTotalTTC=0,montantTotalHT=0,montantTotalTVA=0;
    private double ratio;
    private List<Params> reglements =  new ArrayList<>();
    private List<Params> mvtsCaisse =  new ArrayList<>();
    private List<AchatDTO> achats = new ArrayList<>();
   

    public Integer getMarge() {
        return marge;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

   

    public Integer getMontantTotalTTC() {
        return montantTotalTTC;
    }

    public void setMontantTotalTTC(Integer montantTotalTTC) {
        this.montantTotalTTC = montantTotalTTC;
    }

    public Integer getMontantTotalHT() {
        return montantTotalHT;
    }

    public void setMontantTotalHT(Integer montantTotalHT) {
        this.montantTotalHT = montantTotalHT;
    }

    public Integer getMontantTotalTVA() {
        return montantTotalTVA;
    }

    public void setMontantTotalTVA(Integer montantTotalTVA) {
        this.montantTotalTVA = montantTotalTVA;
    }

    public void setMarge(Integer marge) {
        this.marge = marge;
    }

    public Integer getMontantTTC() {
        return montantTTC;
    }

    public void setMontantTTC(Integer montantTTC) {
        this.montantTTC = montantTTC;
    }

    public Integer getMontantHT() {
        return montantHT;
    }

    public void setMontantHT(Integer montantHT) {
        this.montantHT = montantHT;
    }

    public Integer getMontantTVA() {
        return montantTVA;
    }

    public void setMontantTVA(Integer montantTVA) {
        this.montantTVA = montantTVA;
    }

    public Integer getPourcentageEsp() {
        return pourcentageEsp;
    }

    public void setPourcentageEsp(Integer pourcentageEsp) {
        this.pourcentageEsp = pourcentageEsp;
    }

    public Integer getPourcentageCredit() {
        return pourcentageCredit;
    }

    public void setPourcentageCredit(Integer pourcentageCredit) {
        this.pourcentageCredit = pourcentageCredit;
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

    public Integer getMontantTotalMvt() {
        return montantTotalMvt;
    }

    public void setMontantTotalMvt(Integer montantTotalMvt) {
        this.montantTotalMvt = montantTotalMvt;
    }

    public List<Params> getReglements() {
        return reglements;
    }

    public void setReglements(List<Params> reglements) {
        this.reglements = reglements;
    }

    public List<Params> getMvtsCaisse() {
        return mvtsCaisse;
    }

    public void setMvtsCaisse(List<Params> mvtsCaisse) {
        this.mvtsCaisse = mvtsCaisse;
    }

    public List<AchatDTO> getAchats() {
        return achats;
    }

    public void setAchats(List<AchatDTO> achats) {
        this.achats = achats;
    }

 
    public RecapActiviteDTO() {
    }

  

}
