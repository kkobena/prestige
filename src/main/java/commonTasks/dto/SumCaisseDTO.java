/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Kobena
 */
public class SumCaisseDTO implements Serializable {

    private long amount;
    private String modeReglement;
    private List<VisualisationCaisseDTO> caisses;
    private List<SumCaisseDTO> summary;

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public List<SumCaisseDTO> getSummary() {
        return summary;
    }

    public void setSummary(List<SumCaisseDTO> summary) {
        this.summary = summary;
    }

    public String getModeReglement() {
        return modeReglement;
    }

    public void setModeReglement(String modeReglement) {
        this.modeReglement = modeReglement;
    }

    public SumCaisseDTO(long amount, String modeReglement) {
        this.amount = amount;
        this.modeReglement = modeReglement;
    }

    public SumCaisseDTO( String modeReglement,Long amount) {
        this.amount = amount;
        this.modeReglement = modeReglement;
    }

    public SumCaisseDTO() {
    }

    public List<VisualisationCaisseDTO> getCaisses() {
        return caisses;
    }

    public void setCaisses(List<VisualisationCaisseDTO> caisses) {
        this.caisses = caisses;
    }

    @Override
    public String toString() {
        return "SumCaisseDTO{" + "amount=" + amount + ", modeReglement=" + modeReglement + '}';
    }

}
