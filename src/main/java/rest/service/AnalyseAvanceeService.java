
package rest.service;

import rest.service.dto.analyse.AnalyseAvanceeDTO;
import java.util.List;

/**
 *
 * @author airman
 */

public interface AnalyseAvanceeService {

    AnalyseAvanceeDTO getAnalyseAvancee(String inventaireId, String inventaireName);

    String generateTextualAnalysis(AnalyseAvanceeDTO data, String inventaireName);

    // --- DTOs (Data Transfer Objects) ---

    public static class SyntheseEmplacementDTO {
        private String emplacement;
        private long valeurAchatMachine;
        private long valeurAchatRayon;
        private long valeurVenteMachine;
        private long valeurVenteRayon; // Ajout pour l'affichage
        private long ecartValeurAchat;
        private double tauxDemarque;
        private double contributionEcart;
        private double ratioVA;

        // Getters & Setters
        public String getEmplacement() {
            return emplacement;
        }

        public void setEmplacement(String e) {
            this.emplacement = e;
        }

        public long getValeurAchatMachine() {
            return valeurAchatMachine;
        }

        public void setValeurAchatMachine(long v) {
            this.valeurAchatMachine = v;
        }

        public long getValeurAchatRayon() {
            return valeurAchatRayon;
        }

        public void setValeurAchatRayon(long v) {
            this.valeurAchatRayon = v;
        }

        public long getValeurVenteMachine() {
            return valeurVenteMachine;
        }

        public void setValeurVenteMachine(long v) {
            this.valeurVenteMachine = v;
        }

        public long getValeurVenteRayon() {
            return valeurVenteRayon;
        }

        public void setValeurVenteRayon(long v) {
            this.valeurVenteRayon = v;
        }

        public long getEcartValeurAchat() {
            return ecartValeurAchat;
        }

        public void setEcartValeurAchat(long e) {
            this.ecartValeurAchat = e;
        }

        public double getTauxDemarque() {
            return tauxDemarque;
        }

        public void setTauxDemarque(double t) {
            this.tauxDemarque = t;
        }

        public double getContributionEcart() {
            return contributionEcart;
        }

        public void setContributionEcart(double c) {
            this.contributionEcart = c;
        }

        public double getRatioVA() {
            return ratioVA;
        }

        public void setRatioVA(double r) {
            this.ratioVA = r;
        }
    }

    public static class AnalyseAbcDTO {
        private String nom;
        private long ecartValeurAchat;
        private double pourcentageEcartTotal;
        private double pourcentageCumule;
        private String categorie;

        // Getters & Setters
        public String getNom() {
            return nom;
        }

        public void setNom(String n) {
            this.nom = n;
        }

        public long getEcartValeurAchat() {
            return ecartValeurAchat;
        }

        public void setEcartValeurAchat(long e) {
            this.ecartValeurAchat = e;
        }

        public double getPourcentageEcartTotal() {
            return pourcentageEcartTotal;
        }

        public void setPourcentageEcartTotal(double p) {
            this.pourcentageEcartTotal = p;
        }

        public double getPourcentageCumule() {
            return pourcentageCumule;
        }

        public void setPourcentageCumule(double p) {
            this.pourcentageCumule = p;
        }

        public String getCategorie() {
            return categorie;
        }

        public void setCategorie(String c) {
            this.categorie = c;
        }
    }

    public static class DetailProduitDTO {
        private String nom, emplacement;
        private int qteInitiale, qteSaisie, ecartQuantite;
        private long ecartValeurAchat;
        private int prixAchat, prixVente;

        // Getters & Setters
        public String getNom() {
            return nom;
        }

        public void setNom(String n) {
            this.nom = n;
        }

        public String getEmplacement() {
            return emplacement;
        }

        public void setEmplacement(String e) {
            this.emplacement = e;
        }

        public int getQteInitiale() {
            return qteInitiale;
        }

        public void setQteInitiale(int q) {
            this.qteInitiale = q;
        }

        public int getQteSaisie() {
            return qteSaisie;
        }

        public void setQteSaisie(int q) {
            this.qteSaisie = q;
        }

        public int getEcartQuantite() {
            return ecartQuantite;
        }

        public void setEcartQuantite(int e) {
            this.ecartQuantite = e;
        }

        public long getEcartValeurAchat() {
            return ecartValeurAchat;
        }

        public void setEcartValeurAchat(long e) {
            this.ecartValeurAchat = e;
        }

        public int getPrixAchat() {
            return prixAchat;
        }

        public void setPrixAchat(int p) {
            this.prixAchat = p;
        }

        public int getPrixVente() {
            return prixVente;
        }

        public void setPrixVente(int p) {
            this.prixVente = p;
        }
    }

    public static class AnalyseAvanceeDTO {
        private boolean success = true;
        private List<SyntheseEmplacementDTO> synthese;
        private List<AnalyseAbcDTO> analyseABC;
        private List<DetailProduitDTO> detailProduits;
        private String analysisText;

        // Getters & Setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean s) {
            this.success = s;
        }

        public List<SyntheseEmplacementDTO> getSynthese() {
            return synthese;
        }

        public void setSynthese(List<SyntheseEmplacementDTO> s) {
            this.synthese = s;
        }

        public List<AnalyseAbcDTO> getAnalyseABC() {
            return analyseABC;
        }

        public void setAnalyseABC(List<AnalyseAbcDTO> a) {
            this.analyseABC = a;
        }

        public List<DetailProduitDTO> getDetailProduits() {
            return detailProduits;
        }

        public void setDetailProduits(List<DetailProduitDTO> d) {
            this.detailProduits = d;
        }

        public String getAnalysisText() {
            return analysisText;
        }

        public void setAnalysisText(String text) {
            this.analysisText = text;
        }
    }
}