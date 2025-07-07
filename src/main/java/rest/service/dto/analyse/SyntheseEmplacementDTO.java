
package rest.service.dto.analyse;

/**
 *
 * @author airman
 */
public class SyntheseEmplacementDTO {
    private String emplacement;
    private long valeurAchatMachine;
    private long ecartValeurAchat;
    private double tauxDemarque;
    private double contributionEcart;
    // Getters et Setters

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public long getValeurAchatMachine() {
        return valeurAchatMachine;
    }

    public void setValeurAchatMachine(long valeurAchatMachine) {
        this.valeurAchatMachine = valeurAchatMachine;
    }

    public long getEcartValeurAchat() {
        return ecartValeurAchat;
    }

    public void setEcartValeurAchat(long ecartValeurAchat) {
        this.ecartValeurAchat = ecartValeurAchat;
    }

    public double getTauxDemarque() {
        return tauxDemarque;
    }

    public void setTauxDemarque(double tauxDemarque) {
        this.tauxDemarque = tauxDemarque;
    }

    public double getContributionEcart() {
        return contributionEcart;
    }

    public void setContributionEcart(double contributionEcart) {
        this.contributionEcart = contributionEcart;
    }

}
