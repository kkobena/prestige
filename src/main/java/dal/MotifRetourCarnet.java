/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author koben
 */
@Entity
@Table(name = "motif_retour_carnet")
@NamedQueries({
    @NamedQuery(name = "MotifRetourCarnet.findAll", query = "SELECT o FROM MotifRetourCarnet o ORDER BY o.libelle")}
        )
public class MotifRetourCarnet implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
      @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id", nullable = false, length = 11)
    private Integer id;
    @NotNull
    @Column(name = "libelle", nullable = false,unique = true)
    private String libelle;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
    
    
    
}
