/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import dal.Medecin;
import java.io.Serializable;

/**
 *
 * @author koben
 */
public class MedecinDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String numOrdre;
    private String nom;
    private String commentaire;
    private String id;

    public String getNumOrdre() {
        return numOrdre;
    }

    public void setNumOrdre(String numOrdre) {
        this.numOrdre = numOrdre;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getId() {
        return id;
    }

    public MedecinDTO() {
    }

    public MedecinDTO(String numOrdre, String nom, String commentaire) {
        this.numOrdre = numOrdre;
        this.nom = nom;
        this.commentaire = commentaire;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MedecinDTO(Medecin medecin) {
        this.numOrdre = medecin.getNumOrdre();
        this.nom = medecin.getNom();
        this.commentaire = medecin.getCommentaire();
        this.id = medecin.getId();
    }

}
