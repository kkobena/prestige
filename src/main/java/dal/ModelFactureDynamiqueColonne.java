package dal;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Colonne d'un modele de facture dynamique : champ de donnees, libelle affiche et ordre.
 *
 * Deux niveaux coexistent dans la meme table : les colonnes du BON (une ligne par dossier, comportement historique) et,
 * quand le modele detaille les produits, les colonnes du PRODUIT (lignes de vente affichees sous chaque bon).
 */
@Entity
@Table(name = "model_facture_dynamique_colonne")
public class ModelFactureDynamiqueColonne implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String NIVEAU_BON = "BON";
    public static final String NIVEAU_PRODUIT = "PRODUIT";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "model_id", referencedColumnName = "id")
    private ModelFactureDynamique modele;

    /** Code du champ de donnees (cf. registre des colonnes disponibles cote service). */
    @Column(name = "str_CHAMP", nullable = false, length = 50)
    private String champ;

    /** Libelle de l'en-tete de colonne sur la facture. */
    @Column(name = "str_LIBELLE", nullable = false, length = 100)
    private String libelle;

    @Column(name = "int_ORDRE", nullable = false)
    private Integer ordre = 0;

    /** Niveau d'affichage : NIVEAU_BON (ligne de dossier) ou NIVEAU_PRODUIT (ligne de vente sous le bon). */
    @Column(name = "str_NIVEAU", length = 10)
    private String niveau = NIVEAU_BON;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ModelFactureDynamique getModele() {
        return modele;
    }

    public void setModele(ModelFactureDynamique modele) {
        this.modele = modele;
    }

    public String getChamp() {
        return champ;
    }

    public void setChamp(String champ) {
        this.champ = champ;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    /** Colonnes creees avant l'option "detail des produits" : niveau BON par defaut. */
    public String getNiveau() {
        return niveau == null ? NIVEAU_BON : niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public boolean estProduit() {
        return NIVEAU_PRODUIT.equals(getNiveau());
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }
}
