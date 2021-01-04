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
    private long montantTTC = 0, montantHT = 0, montantAchat = 0, montantMarge = 0, montantCumulTTC = 0, montantCumulHT = 0, montantCumulAchat = 0;
    private long valeurPeriode = 0, pourcentageMage = 0, pourcentageTH = 0, pourcentageCumulMage = 0, pourcentageCumulTH = 0;
    private long montantTva = 0, montantCumulTva = 0, montantRemise = 0, montantCumulMarge;

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

    public long getMontantCumulMarge() {
        return montantCumulMarge;
    }

    public void setMontantCumulMarge(long montantCumulMarge) {
        this.montantCumulMarge = montantCumulMarge;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPourcentageCumulMage() {
        return pourcentageCumulMage;
    }

    public void setPourcentageCumulMage(long pourcentageCumulMage) {
        this.pourcentageCumulMage = pourcentageCumulMage;
    }

    public long getPourcentageCumulTH() {
        return pourcentageCumulTH;
    }

    public void setPourcentageCumulTH(long pourcentageCumulTH) {
        this.pourcentageCumulTH = pourcentageCumulTH;
    }

    public void setFamilleId(String familleId) {
        this.familleId = familleId;
    }

    public long getMontantTva() {
        return montantTva;
    }

    public void setMontantTva(long montantTva) {
        this.montantTva = montantTva;
    }

    public long getMontantCumulTva() {
        return montantCumulTva;
    }

    public void setMontantCumulTva(long montantCumulTva) {
        this.montantCumulTva = montantCumulTva;
    }

    public long getMontantRemise() {
        return montantRemise;
    }

    public void setMontantRemise(long montantRemise) {
        this.montantRemise = montantRemise;
    }

    public FamilleArticleStatDTO(String code, String libelle, long montantTTC, long montantAchat, long montantTva, long montantRemise, String familleId) {
        this.code = code;
        this.libelle = libelle;
        this.montantTTC = (montantTTC - montantRemise);
        this.montantHT =  (montantTTC - montantRemise - montantTva);
        this.montantAchat =  montantAchat;
        this.montantMarge =(this.montantHT - montantAchat);
        Double p = new BigDecimal(Double.valueOf(this.montantMarge) / this.montantHT).setScale(2, RoundingMode.HALF_UP).doubleValue() * 100;
        this.pourcentageMage = p.intValue();
        this.montantTva =  montantTva;
        this.montantRemise =  montantRemise;
        this.familleId = familleId;
    }

    public FamilleArticleStatDTO(long montantTTC, long montantAchat, long montantTva, long montantRemise) {
        this.montantCumulTTC =  (montantTTC - montantRemise);
        this.montantCumulHT = (montantTTC - montantRemise - montantTva);
        this.montantCumulAchat =  montantAchat;
        this.montantCumulTva =  montantTva;

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
    public long getMontantTTC() {
        return montantTTC;
    }

    public void setMontantTTC(long montantTTC) {
        this.montantTTC = montantTTC;
    }

    @JSONPropertyName("MONTANT NET HT")
    public long getMontantHT() {
        return montantHT;
    }

    public void setMontantHT(long montantHT) {
        this.montantHT = montantHT;
    }

    @JSONPropertyName("VALEUR ACHAT")
    public long getMontantAchat() {
        return montantAchat;
    }

    public void setMontantAchat(long montantAchat) {
        this.montantAchat = montantAchat;
    }

    @JSONPropertyName("MARGE NET")
    public long getMontantMarge() {
        return montantMarge;
    }

    public void setMontantMarge(long montantMarge) {
        this.montantMarge = montantMarge;
    }

    public long getMontantCumulTTC() {
        return montantCumulTTC;
    }

    public void setMontantCumulTTC(long montantCumulTTC) {
        this.montantCumulTTC = montantCumulTTC;
    }

    public long getMontantCumulHT() {
        return montantCumulHT;
    }

    public void setMontantCumulHT(long montantCumulHT) {
        this.montantCumulHT = montantCumulHT;
    }

    public long getMontantCumulAchat() {
        return montantCumulAchat;
    }

    public void setMontantCumulAchat(long montantCumulAchat) {
        this.montantCumulAchat = montantCumulAchat;
    }

    public long getValeurPeriode() {
        return valeurPeriode;
    }

    public void setValeurPeriode(long valeurPeriode) {
        this.valeurPeriode = valeurPeriode;
    }

    @JSONPropertyName("MARGE POURCENTAGE")
    public long getPourcentageMage() {
        return pourcentageMage;
    }

    public void setPourcentageMage(long pourcentageMage) {
        this.pourcentageMage = pourcentageMage;
    }

    @JSONPropertyName("POURCENTAGE TOTAL")
    public long getPourcentageTH() {
        return pourcentageTH;
    }

    public void setPourcentageTH(long pourcentageTH) {
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

     public FamilleArticleStatDTO(String code, String libelle, long montantTTC, long montantAchat, long montantTva, long montantRemise, Integer prixAchat, Integer prixVente, long quantite){
        this.code = code;
        this.libelle = libelle;
        this.montantCumulTTC = (montantTTC - montantRemise);
        this.montantCumulHT = (montantTTC - montantRemise - montantTva);
        this.montantCumulAchat =  montantAchat;
        this.montantCumulMarge =  (this.montantCumulHT - montantAchat);
        Double p = new BigDecimal(Double.valueOf(this.montantCumulMarge) / this.montantCumulHT).setScale(2, RoundingMode.HALF_UP).doubleValue() * 100;
        this.pourcentageCumulMage = p.intValue();
        this.montantTva = prixAchat;
        this.montantRemise = prixVente;
        this.montantCumulTva =  quantite;

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
            long nombreSortie, long montantVente,int seuil) {
        this.code = code;
        this.libelle = libelle;
        this.familleId = familleId;
        this.description = description;
        this.montantCumulTTC =  quantiteVNO;
        this.montantCumulHT = quantiteVO;
        this.montantCumulAchat =  quantiteVendue;
        this.montantCumulMarge = nombreSortie;
        Double p = new BigDecimal(Double.valueOf(quantiteVendue) / nombreSortie).setScale(1, RoundingMode.HALF_UP).doubleValue();
        this.pourcentageCumulMage = p.intValue();
        this.montantTva =  montantVente;
        this.valeurPeriode= Long.valueOf(seuil) ;
        this.id=id;

    }
  public FamilleArticleStatDTO(String code, String libelle,String familleId, String description, long montantTTC, long montantAchat, long montantTva, long montantRemise, long quantite) {
        this.code = code;
        this.libelle = libelle;
        this.montantCumulTTC =  (montantTTC - montantRemise);
        this.montantCumulHT =  (montantTTC - montantRemise - montantTva);
        this.montantCumulAchat =  montantAchat;
        this.montantCumulMarge =  (this.montantCumulHT - montantAchat);
        Double p = new BigDecimal(Double.valueOf(this.montantCumulMarge) / this.montantCumulHT).setScale(2, RoundingMode.HALF_UP).doubleValue() * 100;
        this.pourcentageCumulMage = p.intValue();
        this.montantCumulTva =  quantite;
         this.familleId = familleId;
        this.description = description;

    }
}
