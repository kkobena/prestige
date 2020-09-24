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
 * @author Kobena
 */
public class MontantAPaye implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer remise = 0, marge = 0, montantTva = 0;
    private Integer montantNet = 0, montant = 0, montantTp = 0, montantAccount = 0;
    private List<TiersPayantParams> tierspayants = new ArrayList<>();
    private boolean restructuring;
    private String message;

    public boolean isRestructuring() {
        return restructuring;
    }

    public void setRestructuring(boolean restructuring) {
        this.restructuring = restructuring;
    }

    public Integer getMontantTva() {
        return montantTva;
    }

    public void setMontantTva(Integer montantTva) {
        this.montantTva = montantTva;
    }

    public Integer getRemise() {
        return remise;
    }

    public Integer getMarge() {
        return marge;
    }

    public void setMarge(Integer marge) {
        this.marge = marge;
    }

    public MontantAPaye() {
    }

    public MontantAPaye montantAccount(Integer montantAccount) {
        this.montantAccount = montantAccount;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Integer getMontantAccount() {
        return montantAccount;
    }

    public void setMontantAccount(Integer montantAccount) {
        this.montantAccount = montantAccount;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MontantAPaye(Integer montantNet, Integer montant, Integer montantTp,
            Integer remise, Integer marge, Integer montantTva) {
        this.montant = montant;
        this.montantNet = montantNet;
        this.montantTp = montantTp;
        this.remise = remise;
        this.marge = marge;
        this.montantTva = montantTva;
    }

    public List<TiersPayantParams> getTierspayants() {
        return tierspayants;
    }

    public void setTierspayants(List<TiersPayantParams> tierspayants) {
        this.tierspayants = tierspayants;
    }

    public void setRemise(Integer remise) {
        this.remise = remise;
    }

    public Integer getMontantNet() {
        return montantNet;
    }

    public void setMontantNet(Integer montantNet) {
        this.montantNet = montantNet;
    }

    public Integer getMontant() {
        return montant;
    }

    public void setMontant(Integer montant) {
        this.montant = montant;
    }

    public Integer getMontantTp() {
        return montantTp;
    }

    public void setMontantTp(Integer montantTp) {
        this.montantTp = montantTp;
    }

    @Override
    public String toString() {
        return "MontantAPaye{" + "remise=" + remise + ", montantNet=" + montantNet + ", montant=" + montant + ", montantTp=" + montantTp + '}';
    }

}
