package rest.service.calculation.dto;

import java.math.BigDecimal;

public class TiersPayantInput {

    private String clientTiersPayantId;
    private String tiersPayantId;
    private String tiersPayantFullName;
    private float taux;
    private BigDecimal plafondConso;
    private BigDecimal consoMensuelle = BigDecimal.ZERO;
    private BigDecimal plafondJournalierClient;
    private int priorite;

    public String getClientTiersPayantId() {
        return clientTiersPayantId;
    }

    public void setClientTiersPayantId(String clientTiersPayantId) {
        this.clientTiersPayantId = clientTiersPayantId;
    }

    public String getTiersPayantFullName() {
        return tiersPayantFullName;
    }

    public void setTiersPayantFullName(String tiersPayantFullName) {
        this.tiersPayantFullName = tiersPayantFullName;
    }

    public float getTaux() {
        return taux;
    }

    public void setTaux(float taux) {
        this.taux = taux;
    }

    public BigDecimal getPlafondConso() {
        return plafondConso;
    }

    public void setPlafondConso(BigDecimal plafondConso) {
        this.plafondConso = plafondConso;
    }

    public BigDecimal getConsoMensuelle() {
        return consoMensuelle;
    }

    public void setConsoMensuelle(BigDecimal consoMensuelle) {
        this.consoMensuelle = consoMensuelle;
    }

    public BigDecimal getPlafondJournalierClient() {
        return plafondJournalierClient;
    }

    public void setPlafondJournalierClient(BigDecimal plafondJournalierClient) {
        this.plafondJournalierClient = plafondJournalierClient;
    }

    public String getTiersPayantId() {
        return tiersPayantId;
    }

    public void setTiersPayantId(String tiersPayantId) {
        this.tiersPayantId = tiersPayantId;
    }

    public int getPriorite() {
        return priorite;
    }

    public void setPriorite(int priorite) {
        this.priorite = priorite;
    }

}
