
package rest.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author airman
 */

@Getter
@Setter
@Builder
public class InventaireFamilleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String lgInventaireFamilleId;
    private String lgInventaireId;
    private String lgFamilleId;
    private String cip;
    private String name;
    private String designation;
    private String lgZoneGeoId;
    private String lgFamilleArticleId;
    private String lgGrossisteId;
    private Integer prixVente;
    private Integer prixReference;
    private Integer paf;
    private Integer stockRayon;
    private Integer stockMachine;
    private Integer ecart;

    public String getLgInventaireFamilleId() {
        return lgInventaireFamilleId;
    }

    public void setLgInventaireFamilleId(String lgInventaireFamilleId) {
        this.lgInventaireFamilleId = lgInventaireFamilleId;
    }

    public String getLgInventaireId() {
        return lgInventaireId;
    }

    public void setLgInventaireId(String lgInventaireId) {
        this.lgInventaireId = lgInventaireId;
    }

    public String getLgFamilleId() {
        return lgFamilleId;
    }

    public void setLgFamilleId(String lgFamilleId) {
        this.lgFamilleId = lgFamilleId;
    }

    public String getCip() {
        return cip;
    }

    public void setCip(String cip) {
        this.cip = cip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getLgZoneGeoId() {
        return lgZoneGeoId;
    }

    public void setLgZoneGeoId(String lgZoneGeoId) {
        this.lgZoneGeoId = lgZoneGeoId;
    }

    public String getLgFamilleArticleId() {
        return lgFamilleArticleId;
    }

    public void setLgFamilleArticleId(String lgFamilleArticleId) {
        this.lgFamilleArticleId = lgFamilleArticleId;
    }

    public String getLgGrossisteId() {
        return lgGrossisteId;
    }

    public void setLgGrossisteId(String lgGrossisteId) {
        this.lgGrossisteId = lgGrossisteId;
    }

    public Integer getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(Integer prixVente) {
        this.prixVente = prixVente;
    }

    public Integer getPrixReference() {
        return prixReference;
    }

    public void setPrixReference(Integer prixReference) {
        this.prixReference = prixReference;
    }

    public Integer getPaf() {
        return paf;
    }

    public void setPaf(Integer paf) {
        this.paf = paf;
    }

    public Integer getStockRayon() {
        return stockRayon;
    }

    public void setStockRayon(Integer stockRayon) {
        this.stockRayon = stockRayon;
    }

    public Integer getStockMachine() {
        return stockMachine;
    }

    public void setStockMachine(Integer stockMachine) {
        this.stockMachine = stockMachine;
    }

    public Integer getEcart() {
        return ecart;
    }

    public void setEcart(Integer ecart) {
        this.ecart = ecart;
    }

}