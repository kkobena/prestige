/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import org.json.JSONPropertyName;

/**
 *
 * @author DICI
 */
public class FamilleArticleStatDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code, libelle, familleId, description,id;
    private Integer montantTTC = 0, montantHT = 0, montantAchat = 0, montantMarge = 0, montantCumulTTC = 0, montantCumulHT = 0, montantCumulAchat = 0;
    private Integer valeurPeriode = 0, pourcentageMage = 0, pourcentageTH = 0, pourcentageCumulMage = 0, pourcentageCumulTH = 0;
    private Integer montantTva = 0, montantCumulTva = 0, montantRemise = 0, montantCumulMarge;

    public FamilleArticleStatDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFamilleId() {
        return familleId;
    }

    public Integer getMontantCumulMarge() {
        return montantCumulMarge;
    }

    public void setMontantCumulMarge(Integer montantCumulMarge) {
        this.montantCumulMarge = montantCumulMarge;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPourcentageCumulMage() {
        return pourcentageCumulMage;
    }

    public void setPourcentageCumulMage(Integer pourcentageCumulMage) {
        this.pourcentageCumulMage = pourcentageCumulMage;
    }

    public Integer getPourcentageCumulTH() {
        return pourcentageCumulTH;
    }

    public void setPourcentageCumulTH(Integer pourcentageCumulTH) {
        this.pourcentageCumulTH = pourcentageCumulTH;
    }

    public void setFamilleId(String familleId) {
        this.familleId = familleId;
    }

    public Integer getMontantTva() {
        return montantTva;
    }

    public void setMontantTva(Integer montantTva) {
        this.montantTva = montantTva;
    }

    public Integer getMontantCumulTva() {
        return montantCumulTva;
    }

    public void setMontantCumulTva(Integer montantCumulTva) {
        this.montantCumulTva = montantCumulTva;
    }

    public Integer getMontantRemise() {
        return montantRemise;
    }

    public void setMontantRemise(Integer montantRemise) {
        this.montantRemise = montantRemise;
    }

    public FamilleArticleStatDTO(String code, String libelle, long montantTTC, long montantAchat, long montantTva, long montantRemise, String familleId) {
        this.code = code;
        this.libelle = libelle;
        this.montantTTC = (int) (montantTTC - montantRemise);
        this.montantHT = (int) (montantTTC - montantRemise - montantTva);
        this.montantAchat = (int) montantAchat;
        this.montantMarge = (int) (this.montantHT - montantAchat);
        Double p = new BigDecimal(Double.valueOf(this.montantMarge) / this.montantHT).setScale(2, RoundingMode.HALF_UP).doubleValue() * 100;
        this.pourcentageMage = p.intValue();
        this.montantTva = (int) montantTva;
        this.montantRemise = (int) montantRemise;
        this.familleId = familleId;
    }

    public FamilleArticleStatDTO(long montantTTC, long montantAchat, long montantTva, long montantRemise) {
        this.montantCumulTTC = (int) (montantTTC - montantRemise);
        this.montantCumulHT = (int) (montantTTC - montantRemise - montantTva);
        this.montantCumulAchat = (int) montantAchat;
        this.montantCumulTva = (int) montantTva;

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @JSONPropertyName("MONTANT NET TTC")
    public Integer getMontantTTC() {
        return montantTTC;
    }

    public void setMontantTTC(Integer montantTTC) {
        this.montantTTC = montantTTC;
    }

    @JSONPropertyName("MONTANT NET HT")
    public Integer getMontantHT() {
        return montantHT;
    }

    public void setMontantHT(Integer montantHT) {
        this.montantHT = montantHT;
    }

    @JSONPropertyName("VALEUR ACHAT")
    public Integer getMontantAchat() {
        return montantAchat;
    }

    public void setMontantAchat(Integer montantAchat) {
        this.montantAchat = montantAchat;
    }

    @JSONPropertyName("MARGE NET")
    public Integer getMontantMarge() {
        return montantMarge;
    }

    public void setMontantMarge(Integer montantMarge) {
        this.montantMarge = montantMarge;
    }

    public Integer getMontantCumulTTC() {
        return montantCumulTTC;
    }

    public void setMontantCumulTTC(Integer montantCumulTTC) {
        this.montantCumulTTC = montantCumulTTC;
    }

    public Integer getMontantCumulHT() {
        return montantCumulHT;
    }

    public void setMontantCumulHT(Integer montantCumulHT) {
        this.montantCumulHT = montantCumulHT;
    }

    public Integer getMontantCumulAchat() {
        return montantCumulAchat;
    }

    public void setMontantCumulAchat(Integer montantCumulAchat) {
        this.montantCumulAchat = montantCumulAchat;
    }

    public Integer getValeurPeriode() {
        return valeurPeriode;
    }

    public void setValeurPeriode(Integer valeurPeriode) {
        this.valeurPeriode = valeurPeriode;
    }

    @JSONPropertyName("MARGE POURCENTAGE")
    public Integer getPourcentageMage() {
        return pourcentageMage;
    }

    public void setPourcentageMage(Integer pourcentageMage) {
        this.pourcentageMage = pourcentageMage;
    }

    @JSONPropertyName("POURCENTAGE TOTAL")
    public Integer getPourcentageTH() {
        return pourcentageTH;
    }

    public void setPourcentageTH(Integer pourcentageTH) {
        this.pourcentageTH = pourcentageTH;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.code);
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
        final FamilleArticleStatDTO other = (FamilleArticleStatDTO) obj;
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        return true;
    }

    public FamilleArticleStatDTO(String code, String libelle, long montantTTC, long montantAchat, long montantTva, long montantRemise, Integer prixAchat, Integer prixVente, long quantite) {
        this.code = code;
        this.libelle = libelle;
        this.montantCumulTTC = (int) (montantTTC - montantRemise);
        this.montantCumulHT = (int) (montantTTC - montantRemise - montantTva);
        this.montantCumulAchat = (int) montantAchat;
        this.montantCumulMarge = (int) (this.montantCumulHT - montantAchat);
        Double p = new BigDecimal(Double.valueOf(this.montantCumulMarge) / this.montantCumulHT).setScale(2, RoundingMode.HALF_UP).doubleValue() * 100;
        this.pourcentageCumulMage = p.intValue();
        this.montantTva = prixAchat;
        this.montantRemise = prixVente;
        this.montantCumulTva = (int) quantite;

    }
/**
 *  constructeur statistique des unites vendues
     * @param id
 * @param code
 * @param libelle
 * @param familleId
 * @param description
 * @param quantiteVNO
 * @param quantiteVO
 * @param quantiteVendue
 * @param nombreSortie
 * @param montantVente
     * @param seuil
 */
  
    public FamilleArticleStatDTO(String id,String code, String libelle, String familleId, String description, long quantiteVNO, long quantiteVO, long quantiteVendue,
            long nombreSortie, long montantVente,Integer seuil) {
        this.code = code;
        this.libelle = libelle;
        this.familleId = familleId;
        this.description = description;
        this.montantCumulTTC = (int) quantiteVNO;
        this.montantCumulHT = (int) quantiteVO;
        this.montantCumulAchat = (int) quantiteVendue;
        this.montantCumulMarge = (int) nombreSortie;
        Double p = new BigDecimal(Double.valueOf(quantiteVendue) / nombreSortie).setScale(1, RoundingMode.HALF_UP).doubleValue();
        this.pourcentageCumulMage = p.intValue();
        this.montantTva = (int) montantVente;
        this.valeurPeriode=seuil;
        this.id=id;

    }
  public FamilleArticleStatDTO(String code, String libelle,String familleId, String description, long montantTTC, long montantAchat, long montantTva, long montantRemise, long quantite) {
        this.code = code;
        this.libelle = libelle;
        this.montantCumulTTC = (int) (montantTTC - montantRemise);
        this.montantCumulHT = (int) (montantTTC - montantRemise - montantTva);
        this.montantCumulAchat = (int) montantAchat;
        this.montantCumulMarge = (int) (this.montantCumulHT - montantAchat);
        Double p = new BigDecimal(Double.valueOf(this.montantCumulMarge) / this.montantCumulHT).setScale(2, RoundingMode.HALF_UP).doubleValue() * 100;
        this.pourcentageCumulMage = p.intValue();
        this.montantCumulTva = (int) quantite;
         this.familleId = familleId;
        this.description = description;

    }
}
