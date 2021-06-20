/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author koben
 */
@Entity
@Table(name = "stock_daily_value")
public class StockDailyValue implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private int id;
    @Column(name = "valeur_achat", nullable = false)
    private long valeurAchat;
    @Column(name = "valeur_vente", nullable = false)
    private long valeurVente;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

}
