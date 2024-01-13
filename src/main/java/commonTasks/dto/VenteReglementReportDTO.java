package commonTasks.dto;

/**
 *
 * @author koben
 */
public class VenteReglementReportDTO {

    private String typeReglement;
    private String typeVente;
    private String libelle;
    private long montant;
    private long montantAttentu;
    private long flagedAmount;
    private long ugNetAmount;
    private long ugTtcAmount;

    public VenteReglementReportDTO() {
    }

    public String getTypeVente() {
        return typeVente;
    }

    public long getFlagedAmount() {
        return flagedAmount;
    }

    public void setFlagedAmount(long flagedAmount) {
        this.flagedAmount = flagedAmount;
    }

    public void setTypeVente(String typeVente) {
        this.typeVente = typeVente;
    }

    public String getTypeReglement() {
        return typeReglement;
    }

    public void setTypeReglement(String typeReglement) {
        this.typeReglement = typeReglement;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public long getMontant() {
        return montant;
    }

    public void setMontant(long montant) {
        this.montant = montant;
    }

    public long getMontantAttentu() {
        return montantAttentu;
    }

    public void setMontantAttentu(long montantAttentu) {
        this.montantAttentu = montantAttentu;
    }

    public long getUgNetAmount() {
        return ugNetAmount;
    }

    public void setUgNetAmount(long ugNetAmount) {
        this.ugNetAmount = ugNetAmount;
    }

    public long getUgTtcAmount() {
        return ugTtcAmount;
    }

    public void setUgTtcAmount(long ugTtcAmount) {
        this.ugTtcAmount = ugTtcAmount;
    }

}
