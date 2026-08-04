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

/** Colonne d'un modele de facture dynamique : champ de donnees, libelle affiche et ordre. */
@Entity
@Table(name = "model_facture_dynamique_colonne")
public class ModelFactureDynamiqueColonne implements Serializable {

    private static final long serialVersionUID = 1L;

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

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }
}
