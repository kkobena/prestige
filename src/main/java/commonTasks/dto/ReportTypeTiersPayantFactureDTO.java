package commonTasks.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author koben
 */
public class ReportTypeTiersPayantFactureDTO {

    private List<ReportFactureDTO> tierspayants = new ArrayList<>();
    private String typeTiersPayantId;
    private String typeTiersPayantLibelle;
    private BigDecimal montantFacture;
    private BigDecimal montantRegle;
    private BigDecimal montantRestant;

    public List<ReportFactureDTO> getTierspayants() {
        return tierspayants;
    }

    public ReportTypeTiersPayantFactureDTO() {
    }

    public ReportTypeTiersPayantFactureDTO(String typeTiersPayantId, String typeTiersPayantLibelle) {
        this.typeTiersPayantId = typeTiersPayantId;
        this.typeTiersPayantLibelle = typeTiersPayantLibelle;
    }

    public void setTierspayants(List<ReportFactureDTO> tierspayants) {
        this.tierspayants = tierspayants;
    }

    public String getTypeTiersPayantId() {
        return typeTiersPayantId;
    }

    public void setTypeTiersPayantId(String typeTiersPayantId) {
        this.typeTiersPayantId = typeTiersPayantId;
    }

    public String getTypeTiersPayantLibelle() {
        return typeTiersPayantLibelle;
    }

    public void setTypeTiersPayantLibelle(String typeTiersPayantLibelle) {
        this.typeTiersPayantLibelle = typeTiersPayantLibelle;
    }

    public BigDecimal getMontantFacture() {
        return montantFacture;
    }

    public void setMontantFacture(BigDecimal montantFacture) {
        this.montantFacture = montantFacture;
    }

    public BigDecimal getMontantRegle() {
        return montantRegle;
    }

    public void setMontantRegle(BigDecimal montantRegle) {
        this.montantRegle = montantRegle;
    }

    public BigDecimal getMontantRestant() {
        return montantRestant;
    }

    public void setMontantRestant(BigDecimal montantRestant) {
        this.montantRestant = montantRestant;
    }

}
