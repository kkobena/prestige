/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import dal.enumeration.TypeTransaction;
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
    private String typeVente, reglement;
    long montantTTC = 0, montantNet = 0, montantRemise = 0, pourcentage = 0, panierMoyen = 0,
            montantEsp = 0, montantCheque = 0, marge = 0, MontantVirement = 0, montantTva = 0, montantCB = 0, montantTp = 0, montantDiff = 0, nbreVente = 0, montantMobilePayment = 0;
    private TypeTransaction typeTransaction;

    public String getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(String balanceId) {
        this.balanceId = balanceId;
    }

    public TypeTransaction getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(TypeTransaction typeTransaction) {
        this.typeTransaction = typeTransaction;
    }

    public String getTypeVente() {
        return typeVente;
    }

    public void setTypeVente(String typeVente) {
        this.typeVente = typeVente;
    }

    public long getMontantTTC() {
        return montantTTC;
    }

    public void setMontantTTC(long montantTTC) {
        this.montantTTC = montantTTC;
    }

    public long getMontantNet() {
        return montantNet;
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

    public long getPourcentage() {
        return pourcentage;
    }

    public void setPourcentage(long pourcentage) {
        this.pourcentage = pourcentage;
    }

    public long getPanierMoyen() {
        return panierMoyen;
    }

    public long getNbreVente() {
        return nbreVente;
    }

    public void setNbreVente(long nbreVente) {
        this.nbreVente = nbreVente;
    }

    public void setPanierMoyen(long panierMoyen) {
        this.panierMoyen = panierMoyen;
    }

    public long getMontantEsp() {
        return montantEsp;
    }

    public void setMontantEsp(long montantEsp) {
        this.montantEsp = montantEsp;
    }

    public long getMontantCheque() {
        return montantCheque;
    }

    public void setMontantCheque(long montantCheque) {
        this.montantCheque = montantCheque;
    }

    public long getMontantVirement() {
        return MontantVirement;
    }

    public void setMontantVirement(long MontantVirement) {
        this.MontantVirement = MontantVirement;
    }

    public long getMontantCB() {
        return montantCB;
    }

    public void setMontantCB(long montantCB) {
        this.montantCB = montantCB;
    }

    public long getMontantTp() {
        return montantTp;
    }

    public void setMontantTp(long montantTp) {
        this.montantTp = montantTp;
    }

    public long getMontantDiff() {
        return montantDiff;
    }

    public void setMontantDiff(long montantDiff) {
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

    public long getMontantMobilePayment() {
        return montantMobilePayment;
    }

    public void setMontantMobilePayment(long montantMobilePayment) {
        this.montantMobilePayment = montantMobilePayment;
    }

    public long getMontantTva() {
        return montantTva;
    }

    public void setMontantTva(long montantTva) {
        this.montantTva = montantTva;
    }

    @Override
    public String toString() {
        return "BalanceDTO{" + "balanceId=" + balanceId + ", typeVente=" + typeVente + ", montantTTC=" + montantTTC + ", montantNet=" + montantNet + ", montantRemise=" + montantRemise + ", pourcentage=" + pourcentage + ", panierMoyen=" + panierMoyen + ", montantEsp=" + montantEsp + ", montantCheque=" + montantCheque + ", MontantVirement=" + MontantVirement + ", montantCB=" + montantCB + ", montantTp=" + montantTp + ", montantDiff=" + montantDiff + '}';
    }
/*
    constructor vente
    */
    public BalanceDTO(long montantTTC, long montantNet,
            long montantRemise, long montantTva, long marge, long montantDiff,
            long montantTp, long nbreVente, long montantEsp, int typeTransaction, String reglement) {
        this.montantTTC = montantTTC;
        this.montantRemise = montantRemise;
        this.montantNet = montantNet;
        this.montantTva = montantTva;
        this.marge = marge;
        this.montantTp = montantTp;
        this.nbreVente = nbreVente;
        this.montantDiff = montantDiff;
        this.montantEsp = montantEsp;
        switch (typeTransaction) {
            case 0:
                this.typeTransaction = TypeTransaction.VENTE_COMPTANT;
                break;
            case 1:
                this.typeTransaction = TypeTransaction.VENTE_CREDIT;
                break;
            default:
                break;
        }
        this.reglement = reglement;

    }

    public String getReglement() {
        return reglement;
    }

    public void setReglement(String reglement) {
        this.reglement = reglement;
    }

    public long getMarge() {
        return marge;
    }

    public void setMarge(long marge) {
        this.marge = marge;
    }

    public BalanceDTO() {
    }

}
