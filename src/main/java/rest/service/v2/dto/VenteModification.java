/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest.service.v2.dto;

import java.util.List;

/**
 *
 * @author koben
 */
public class VenteModification {

    private boolean empty;
    private String oldClient;
    private String finalClient;
    private String oldBon;
    private String finalBon;
    private List<String> oldTiersPayant;
    private List<String> finalTiersPayant;
    private String oldMontantClient;
    private String nouveauMontantClient;
    private String oldAyantDroit;
    private String finalAyantDroit;

    public String getOldClient() {
        return oldClient;
    }

    public String getOldAyantDroit() {
        return oldAyantDroit;
    }

    public void setOldAyantDroit(String oldAyantDroit) {
        this.oldAyantDroit = oldAyantDroit;
    }

    public String getFinalAyantDroit() {
        return finalAyantDroit;
    }

    public void setFinalAyantDroit(String finalAyantDroit) {
        this.finalAyantDroit = finalAyantDroit;
    }

    public String getOldMontantClient() {
        return oldMontantClient;
    }

    public void setOldMontantClient(String oldMontantClient) {
        this.oldMontantClient = oldMontantClient;
    }

    public String getNouveauMontantClient() {
        return nouveauMontantClient;
    }

    public void setNouveauMontantClient(String nouveauMontantClient) {
        this.nouveauMontantClient = nouveauMontantClient;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public void setOldClient(String oldClient) {
        this.oldClient = oldClient;
    }

    public String getFinalClient() {
        return finalClient;
    }

    public void setFinalClient(String finalClient) {
        this.finalClient = finalClient;
    }

    public String getOldBon() {
        return oldBon;
    }

    public void setOldBon(String oldBon) {
        this.oldBon = oldBon;
    }

    public String getFinalBon() {
        return finalBon;
    }

    public void setFinalBon(String finalBon) {
        this.finalBon = finalBon;
    }

    public List<String> getOldTiersPayant() {
        return oldTiersPayant;
    }

    public void setOldTiersPayant(List<String> oldTiersPayant) {
        this.oldTiersPayant = oldTiersPayant;
    }

    public List<String> getFinalTiersPayant() {
        return finalTiersPayant;
    }

    public void setFinalTiersPayant(List<String> finalTiersPayant) {
        this.finalTiersPayant = finalTiersPayant;
    }
}
