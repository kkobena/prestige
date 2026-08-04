package dal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Modele de facture dynamique cree par l'utilisateur (createur de modeles) : colonnes a afficher, libelles, ordre et
 * tri. Independant des modeles Jasper historiques (t_model_facture), qui restent inchanges.
 */
@Entity
@Table(name = "model_facture_dynamique")
public class ModelFactureDynamique implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Le tri suit le champ tri de la fiche du tiers payant. */
    public static final String TRI_SELON_TIERS_PAYANT = "TIERS_PAYANT";
    public static final String TRI_ALPHABETIQUE = "ALPHABETIQUE";
    public static final String TRI_DATE_BON = "DATE_BON";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "str_NOM", nullable = false, length = 100)
    private String nom;

    @Column(name = "str_DESCRIPTION", length = 255)
    private String description;

    @Column(name = "str_MODE_TRI", length = 30)
    private String modeTri = TRI_SELON_TIERS_PAYANT;

    @Column(name = "str_STATUT", length = 20)
    private String statut = "enable";

    @Column(name = "dt_CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtCreated;

    @Column(name = "dt_UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtUpdated;

    @OneToMany(mappedBy = "modele", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<ModelFactureDynamiqueColonne> colonnes = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModeTri() {
        return modeTri;
    }

    public void setModeTri(String modeTri) {
        this.modeTri = modeTri;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Date getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(Date dtCreated) {
        this.dtCreated = dtCreated;
    }

    public Date getDtUpdated() {
        return dtUpdated;
    }

    public void setDtUpdated(Date dtUpdated) {
        this.dtUpdated = dtUpdated;
    }

    public List<ModelFactureDynamiqueColonne> getColonnes() {
        return colonnes;
    }

    public void setColonnes(List<ModelFactureDynamiqueColonne> colonnes) {
        this.colonnes = colonnes;
    }
}
