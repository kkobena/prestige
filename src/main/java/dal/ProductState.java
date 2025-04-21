package dal;

import dal.enumeration.ProductStateEnum;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author koben
 */
@Entity
@Table(name = "product_state", indexes = { @Index(columnList = "produit_state", name = "produit_state_index") })
public class ProductState implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime updated = LocalDateTime.now();
    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(name = "produit_id", referencedColumnName = "lg_FAMILLE_ID")
    private TFamille produit;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "produit_state", nullable = false, length = 25)
    private ProductStateEnum produitStateEnum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public TFamille getProduit() {
        return produit;
    }

    public void setProduit(TFamille produit) {
        this.produit = produit;
    }

    public ProductStateEnum getProduitStateEnum() {
        return produitStateEnum;
    }

    public void setProduitStateEnum(ProductStateEnum produitStateEnum) {
        this.produitStateEnum = produitStateEnum;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.id);
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
        final ProductState other = (ProductState) obj;
        return Objects.equals(this.id, other.id);
    }

}
