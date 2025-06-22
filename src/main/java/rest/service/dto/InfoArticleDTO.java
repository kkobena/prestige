
package rest.service.dto;

import java.math.BigDecimal;

/**
 *
 * @author airman
 */
import java.io.Serializable;

public class InfoArticleDTO implements Serializable {

    private String grossiste;
    private String emplacement;
    private String produitId;
    private String codeCip;
    private String libelle;
    private Integer prixVente;
    private Integer prixAchat;
    private Integer stock;
    private Integer quantiteVendue;
    private Double moyenne;
    private String quantiteMois;

    // Getters and Setters
    public String getGrossiste() {
        return grossiste;
    }

    public void setGrossiste(String grossiste) {
        this.grossiste = grossiste;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public String getProduitId() {
        return produitId;
    }

    public void setProduitId(String produitId) {
        this.produitId = produitId;
    }

    public String getCodeCip() {
        return codeCip;
    }

    public void setCodeCip(String codeCip) {
        this.codeCip = codeCip;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(Integer prixVente) {
        this.prixVente = prixVente;
    }

    public Integer getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(Integer prixAchat) {
        this.prixAchat = prixAchat;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getQuantiteVendue() {
        return quantiteVendue;
    }

    public void setQuantiteVendue(Integer quantiteVendue) {
        this.quantiteVendue = quantiteVendue;
    }

    public Double getMoyenne() {
        return moyenne;
    }

    public void setMoyenne(Double moyenne) {
        this.moyenne = moyenne;
    }

    public String getQuantiteMois() {
        return quantiteMois;
    }

    public void setQuantiteMois(String quantiteMois) {
        this.quantiteMois = quantiteMois;
    }

}
