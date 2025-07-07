
package rest.service.dto.analyse;

/**
 *
 * @author airman
 */
public class DetailProduitDTO {
    private String nom, emplacement;
    private int qteInitiale, qteSaisie, ecartQuantite;
    private long ecartValeurAchat;
    private int prixAchat, prixVente;
    // Getters et Setters

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public int getQteInitiale() {
        return qteInitiale;
    }

    public void setQteInitiale(int qteInitiale) {
        this.qteInitiale = qteInitiale;
    }

    public int getQteSaisie() {
        return qteSaisie;
    }

    public void setQteSaisie(int qteSaisie) {
        this.qteSaisie = qteSaisie;
    }

    public int getEcartQuantite() {
        return ecartQuantite;
    }

    public void setEcartQuantite(int ecartQuantite) {
        this.ecartQuantite = ecartQuantite;
    }

    public long getEcartValeurAchat() {
        return ecartValeurAchat;
    }

    public void setEcartValeurAchat(long ecartValeurAchat) {
        this.ecartValeurAchat = ecartValeurAchat;
    }

    public int getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(int prixAchat) {
        this.prixAchat = prixAchat;
    }

    public int getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(int prixVente) {
        this.prixVente = prixVente;
    }

}
