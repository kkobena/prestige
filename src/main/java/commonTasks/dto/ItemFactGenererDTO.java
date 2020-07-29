/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import java.io.Serializable;

/**
 *
 * @author kkoffi
 */
public class ItemFactGenererDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id, fullName;
    private Integer montant, nbDossier;

    public ItemFactGenererDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getMontant() {
        return montant;
    }

    public void setMontant(Integer montant) {
        this.montant = montant;
    }

    public Integer getNbDossier() {
        return nbDossier;
    }

    public void setNbDossier(Integer nbDossier) {
        this.nbDossier = nbDossier;
    }

    public ItemFactGenererDTO(String id, String fullName, Integer montant) {
        this.id = id;
        this.fullName = fullName;
        this.montant = montant;
    }

    public ItemFactGenererDTO(String id, String fullName, Number montant, Number nbDossier) {
        this.id = id;
        this.fullName = fullName;
        try {
            this.montant = montant.intValue();
            this.nbDossier = nbDossier.intValue();
        } catch (Exception e) {
        }

    }

}
