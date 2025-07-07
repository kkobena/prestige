
package rest.service.dto.analyse;

/**
 *
 * @author airman
 */
public class AnalyseAbcDTO {
    private String nom;
    private long ecartValeurAchat;
    private double pourcentageEcartTotal;
    private double pourcentageCumule;
    private String categorie;
    // Getters et Setters

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public long getEcartValeurAchat() {
        return ecartValeurAchat;
    }

    public void setEcartValeurAchat(long ecartValeurAchat) {
        this.ecartValeurAchat = ecartValeurAchat;
    }

    public double getPourcentageEcartTotal() {
        return pourcentageEcartTotal;
    }

    public void setPourcentageEcartTotal(double pourcentageEcartTotal) {
        this.pourcentageEcartTotal = pourcentageEcartTotal;
    }

    public double getPourcentageCumule() {
        return pourcentageCumule;
    }

    public void setPourcentageCumule(double pourcentageCumule) {
        this.pourcentageCumule = pourcentageCumule;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

}
