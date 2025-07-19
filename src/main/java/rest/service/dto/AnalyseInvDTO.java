
package rest.service.dto;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author airman
 */

@XmlRootElement
public class AnalyseInvDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codeCip;
    private String nom;
    private Integer prixAchat;
    private Integer prixVente;
    private String emplacement;
    private String inventaireId;
    private String invName;
    private Integer qteSaisie;
    private Integer qteInitiale;

    // Constructeur privé pour forcer l'utilisation du Builder
    private AnalyseInvDTO(Builder builder) {
        this.codeCip = builder.codeCip;
        this.nom = builder.nom;
        this.prixAchat = builder.prixAchat;
        this.prixVente = builder.prixVente;
        this.emplacement = builder.emplacement;
        this.inventaireId = builder.inventaireId;
        this.invName = builder.invName;
        this.qteSaisie = builder.qteSaisie;
        this.qteInitiale = builder.qteInitiale;
    }

    // Constructeur par défaut pour JAXB
    public AnalyseInvDTO() {
    }

    // Getters
    public String getCodeCip() {
        return codeCip;
    }

    public String getNom() {
        return nom;
    }

    public Integer getPrixAchat() {
        return prixAchat;
    }

    public Integer getPrixVente() {
        return prixVente;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public String getInventaireId() {
        return inventaireId;
    }

    public String getInvName() {
        return invName;
    }

    public Integer getQteSaisie() {
        return qteSaisie;
    }

    public Integer getQteInitiale() {
        return qteInitiale;
    }

    // Classe Builder statique interne
    public static class Builder {
        private String codeCip;
        private String nom;
        private Integer prixAchat;
        private Integer prixVente;
        private String emplacement;
        private String inventaireId;
        private String invName;
        private Integer qteSaisie;
        private Integer qteInitiale;

        public Builder codeCip(String codeCip) {
            this.codeCip = codeCip;
            return this;
        }

        public Builder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public Builder prixAchat(Number prixAchat) {
            this.prixAchat = (prixAchat != null) ? prixAchat.intValue() : null;
            return this;
        }

        public Builder prixVente(Number prixVente) {
            this.prixVente = (prixVente != null) ? prixVente.intValue() : null;
            return this;
        }

        public Builder emplacement(String emplacement) {
            this.emplacement = emplacement;
            return this;
        }

        public Builder inventaireId(String inventaireId) {
            this.inventaireId = inventaireId;
            return this;
        }

        public Builder invName(String invName) {
            this.invName = invName;
            return this;
        }

        public Builder qteSaisie(Number qteSaisie) {
            this.qteSaisie = (qteSaisie != null) ? qteSaisie.intValue() : null;
            return this;
        }

        public Builder qteInitiale(Number qteInitiale) {
            this.qteInitiale = (qteInitiale != null) ? qteInitiale.intValue() : null;
            return this;
        }

        public AnalyseInvDTO build() {
            return new AnalyseInvDTO(this);
        }
    }
}
