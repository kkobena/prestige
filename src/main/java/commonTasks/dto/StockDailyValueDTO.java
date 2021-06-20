/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import dal.StockDailyValue;
import java.io.Serializable;

/**
 *
 * @author koben
 */
public class StockDailyValueDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private long valeurAchat;
    private long valeurVente;

    public long getValeurAchat() {
        return valeurAchat;
    }

    public void setValeurAchat(long valeurAchat) {
        this.valeurAchat = valeurAchat;
    }

    public long getValeurVente() {
        return valeurVente;
    }

    public void setValeurVente(long valeurVente) {
        this.valeurVente = valeurVente;
    }

    public StockDailyValueDTO(StockDailyValue sdv) {
        this.valeurAchat = sdv.getValeurAchat();
        this.valeurVente = sdv.getValeurVente();
    }

    public StockDailyValueDTO() {
    }

}
