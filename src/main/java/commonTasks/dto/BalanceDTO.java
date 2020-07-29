/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author DICI
 */
public class BalanceDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String balanceId = UUID.randomUUID().toString();
    private String typeVente;
    Integer montantTTC = 0, montantNet = 0, montantRemise = 0, pourcentage = 0, panierMoyen = 0,
            montantEsp = 0, montantCheque=0, MontantVirement = 0, montantCB = 0, montantTp = 0, montantDiff = 0,nbreVente=0;

    public String getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(String balanceId) {
        this.balanceId = balanceId;
    }

    public String getTypeVente() {
        return typeVente;
    }

    public void setTypeVente(String typeVente) {
        this.typeVente = typeVente;
    }

    public Integer getMontantTTC() {
        return montantTTC;
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

    public Integer getNbreVente() {
        return nbreVente;
    }

    public void setNbreVente(Integer nbreVente) {
        this.nbreVente = nbreVente;
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
        return MontantVirement;
    }

    public void setMontantVirement(Integer MontantVirement) {
        this.MontantVirement = MontantVirement;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.balanceId);
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
        final BalanceDTO other = (BalanceDTO) obj;
        if (!Objects.equals(this.balanceId, other.balanceId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BalanceDTO{" + "balanceId=" + balanceId + ", typeVente=" + typeVente + ", montantTTC=" + montantTTC + ", montantNet=" + montantNet + ", montantRemise=" + montantRemise + ", pourcentage=" + pourcentage + ", panierMoyen=" + panierMoyen + ", montantEsp=" + montantEsp + ", montantCheque=" + montantCheque + ", MontantVirement=" + MontantVirement + ", montantCB=" + montantCB + ", montantTp=" + montantTp + ", montantDiff=" + montantDiff + '}';
    }



}
