/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import java.io.Serializable;

/**
 *
 * @author DICI
 */
public class AchatDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer montantTTC = 0, montantHT = 0, montantTVA = 0;
    private String libelleGroupeGrossiste;

    public Integer getMontantTTC() {
        return montantTTC;
    }

    public void setMontantTTC(Integer montantTTC) {
        this.montantTTC = montantTTC;
    }

    public Integer getMontantHT() {
        return montantHT;
    }

    public void setMontantHT(Integer montantHT) {
        this.montantHT = montantHT;
    }

    public Integer getMontantTVA() {
        return montantTVA;
    }

    public void setMontantTVA(Integer montantTVA) {
        this.montantTVA = montantTVA;
    }

    public String getLibelleGroupeGrossiste() {
        return libelleGroupeGrossiste;
    }

    public void setLibelleGroupeGrossiste(String libelleGroupeGrossiste) {
        this.libelleGroupeGrossiste = libelleGroupeGrossiste;
    }

    public AchatDTO() {
    }
    
    
}
