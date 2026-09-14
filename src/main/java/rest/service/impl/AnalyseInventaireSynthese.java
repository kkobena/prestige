package rest.service.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import rest.service.dto.AnalyseInvDTO;
import rest.service.dto.BalanceEditionLigneDTO;

/**
 * Synthese d'un inventaire : les chiffres, les emplacements et les articles a regarder, et les recommandations qui en
 * decoulent (retours du 13/09).
 *
 * <p>
 * Tout est calcule ici, une fois, a partir des lignes de l'inventaire : l'ecran, le PDF et Excel montrent donc les
 * memes valeurs. La classe ne connait ni JasperReports ni HTTP : elle se teste sans conteneur.
 * </p>
 *
 * <p>
 * Conventions : la valeur « machine » est celle du stock avant l'inventaire (quantite initiale), la valeur « inventaire
 * » celle qui a ete comptee. L'ecart est donc constate moins attendu : negatif = demarque.
 * </p>
 */
public final class AnalyseInventaireSynthese {

    /** Un emplacement, avec ce qu'il pese dans l'ecart global. */
    public static final class Emplacement {

        private final String nom;
        private final long articles;
        private final long articlesEcart;
        private final double valeurAchatMachine;
        private final double valeurAchatInventaire;
        private final double valeurVenteMachine;
        private final double valeurVenteInventaire;
        private double contributionPct;

        Emplacement(String nom) {
            this.nom = nom;
            this.articles = 0;
            this.articlesEcart = 0;
            this.valeurAchatMachine = 0;
            this.valeurAchatInventaire = 0;
            this.valeurVenteMachine = 0;
            this.valeurVenteInventaire = 0;
        }

        private Emplacement(String nom, long articles, long articlesEcart, double vam, double vai, double vvm,
                double vvi) {
            this.nom = nom;
            this.articles = articles;
            this.articlesEcart = articlesEcart;
            this.valeurAchatMachine = vam;
            this.valeurAchatInventaire = vai;
            this.valeurVenteMachine = vvm;
            this.valeurVenteInventaire = vvi;
        }

        Emplacement cumule(long deArticles, long deEcarts, double vam, double vai, double vvm, double vvi) {
            return new Emplacement(nom, articles + deArticles, articlesEcart + deEcarts, valeurAchatMachine + vam,
                    valeurAchatInventaire + vai, valeurVenteMachine + vvm, valeurVenteInventaire + vvi);
        }

        public String getNom() {
            return nom;
        }

        public long getArticles() {
            return articles;
        }

        public long getArticlesEcart() {
            return articlesEcart;
        }

        public double getValeurAchatMachine() {
            return valeurAchatMachine;
        }

        public double getValeurAchatInventaire() {
            return valeurAchatInventaire;
        }

        public double getValeurVenteMachine() {
            return valeurVenteMachine;
        }

        public double getValeurVenteInventaire() {
            return valeurVenteInventaire;
        }

        public double getEcartAchat() {
            return valeurAchatInventaire - valeurAchatMachine;
        }

        public double getEcartVente() {
            return valeurVenteInventaire - valeurVenteMachine;
        }

        /** Part de cet emplacement dans la somme des ecarts d'achat en valeur absolue. */
        public double getContributionPct() {
            return contributionPct;
        }

        public double getRatioVA() {
            return valeurAchatInventaire == 0 ? 0 : valeurVenteInventaire / valeurAchatInventaire;
        }
    }

    /** Un article, avec son ecart en quantite et en valeur d'achat. */
    public static final class Article {

        private final String codeCip;
        private final String nom;
        private final String emplacement;
        private final long ecartQte;
        private final double ecartValeurAchat;
        private final double ecartValeurVente;

        Article(String codeCip, String nom, String emplacement, long ecartQte, double ecartValeurAchat,
                double ecartValeurVente) {
            this.codeCip = codeCip;
            this.nom = nom;
            this.emplacement = emplacement;
            this.ecartQte = ecartQte;
            this.ecartValeurAchat = ecartValeurAchat;
            this.ecartValeurVente = ecartValeurVente;
        }

        public String getCodeCip() {
            return codeCip;
        }

        public String getNom() {
            return nom;
        }

        public String getEmplacement() {
            return emplacement;
        }

        public long getEcartQte() {
            return ecartQte;
        }

        public double getEcartValeurAchat() {
            return ecartValeurAchat;
        }

        public double getEcartValeurVente() {
            return ecartValeurVente;
        }
    }

    // Sections des editions : l'ordre est celui de lecture du document.
    public static final String S_GLOBAL = "RÉCAPITULATIF GLOBAL";
    public static final String S_VALORISATION = "VALORISATION DU STOCK";
    public static final String S_VIGILANCE_EMPL = "POINTS DE VIGILANCE PAR EMPLACEMENT";
    public static final String S_ECARTS_POSITIFS = "ÉCARTS POSITIFS À VÉRIFIER";
    public static final String S_ARTICLES = "ARTICLES LES PLUS CRITIQUES";
    public static final String S_EMPLACEMENTS = "DÉTAIL PAR EMPLACEMENT";
    public static final String S_RECO = "RECOMMANDATIONS";

    private final String nomInventaire;
    private final long totalArticles;
    private final long articlesEcart;
    private final double valeurAchatMachine;
    private final double valeurAchatInventaire;
    private final double valeurVenteMachine;
    private final double valeurVenteInventaire;
    private final double sommeEcartsAbsolus;
    private final List<Emplacement> emplacements;
    private final List<Article> articles;

    private AnalyseInventaireSynthese(String nomInventaire, long totalArticles, long articlesEcart, double vam,
            double vai, double vvm, double vvi, double sommeEcartsAbsolus, List<Emplacement> emplacements,
            List<Article> articles) {
        this.nomInventaire = nomInventaire;
        this.totalArticles = totalArticles;
        this.articlesEcart = articlesEcart;
        this.valeurAchatMachine = vam;
        this.valeurAchatInventaire = vai;
        this.valeurVenteMachine = vvm;
        this.valeurVenteInventaire = vvi;
        this.sommeEcartsAbsolus = sommeEcartsAbsolus;
        this.emplacements = emplacements;
        this.articles = articles;
    }

    /** Calcule la synthese a partir des lignes de l'inventaire. Une liste vide donne une synthese a zero. */
    public static AnalyseInventaireSynthese calculer(List<AnalyseInvDTO> lignes) {
        Map<String, Emplacement> parEmplacement = new LinkedHashMap<>();
        List<Article> articles = new ArrayList<>();
        long total = 0, enEcart = 0;
        double vam = 0, vai = 0, vvm = 0, vvi = 0;
        String nomInventaire = "";
        if (lignes != null) {
            for (AnalyseInvDTO l : lignes) {
                if (l == null) {
                    continue;
                }
                if (nomInventaire.isEmpty() && l.getInvName() != null) {
                    nomInventaire = l.getInvName();
                }
                int qteInit = l.getQteInitiale() == null ? 0 : l.getQteInitiale();
                int qteSaisie = l.getQteSaisie() == null ? 0 : l.getQteSaisie();
                double pa = l.getPrixAchat() == null ? 0 : l.getPrixAchat();
                double pv = l.getPrixVente() == null ? 0 : l.getPrixVente();
                boolean ecart = qteInit != qteSaisie;
                total++;
                if (ecart) {
                    enEcart++;
                    articles.add(new Article(l.getCodeCip(), l.getNom(), libelleEmplacement(l.getEmplacement()),
                            (long) qteSaisie - qteInit, (qteSaisie - qteInit) * pa, (qteSaisie - qteInit) * pv));
                }
                vam += qteInit * pa;
                vai += qteSaisie * pa;
                vvm += qteInit * pv;
                vvi += qteSaisie * pv;
                String nom = libelleEmplacement(l.getEmplacement());
                Emplacement e = parEmplacement.getOrDefault(nom, new Emplacement(nom));
                parEmplacement.put(nom,
                        e.cumule(1, ecart ? 1 : 0, qteInit * pa, qteSaisie * pa, qteInit * pv, qteSaisie * pv));
            }
        }
        List<Emplacement> liste = new ArrayList<>(parEmplacement.values());
        double sommeAbsolue = liste.stream().mapToDouble(e -> Math.abs(e.getEcartAchat())).sum();
        for (Emplacement e : liste) {
            e.contributionPct = sommeAbsolue == 0 ? 0 : Math.abs(e.getEcartAchat()) / sommeAbsolue * 100;
        }
        // du plus gros ecart au plus petit : c'est l'ordre dans lequel l'officine doit regarder
        liste.sort(Comparator.comparingDouble((Emplacement e) -> Math.abs(e.getEcartAchat())).reversed());
        articles.sort(Comparator.comparingDouble((Article a) -> Math.abs(a.getEcartValeurAchat())).reversed());
        return new AnalyseInventaireSynthese(nomInventaire, total, enEcart, vam, vai, vvm, vvi, sommeAbsolue, liste,
                articles);
    }

    private static String libelleEmplacement(String valeur) {
        return valeur == null || valeur.trim().isEmpty() ? "Non défini" : valeur;
    }

    // ------------------------------------------------------------------ chiffres

    public String getNomInventaire() {
        return nomInventaire;
    }

    public long getTotalArticles() {
        return totalArticles;
    }

    public long getArticlesEcart() {
        return articlesEcart;
    }

    public long getArticlesConformes() {
        return totalArticles - articlesEcart;
    }

    public double getTauxEcart() {
        return totalArticles == 0 ? 0 : (double) articlesEcart / totalArticles * 100;
    }

    public double getTauxConforme() {
        return totalArticles == 0 ? 0 : (double) getArticlesConformes() / totalArticles * 100;
    }

    public double getValeurAchatMachine() {
        return valeurAchatMachine;
    }

    public double getValeurAchatInventaire() {
        return valeurAchatInventaire;
    }

    public double getValeurVenteMachine() {
        return valeurVenteMachine;
    }

    public double getValeurVenteInventaire() {
        return valeurVenteInventaire;
    }

    public double getEcartAchat() {
        return valeurAchatInventaire - valeurAchatMachine;
    }

    public double getEcartVente() {
        return valeurVenteInventaire - valeurVenteMachine;
    }

    public double getTauxEvolutionAchat() {
        return valeurAchatMachine == 0 ? 0 : getEcartAchat() / valeurAchatMachine * 100;
    }

    public double getTauxEvolutionVente() {
        return valeurVenteMachine == 0 ? 0 : getEcartVente() / valeurVenteMachine * 100;
    }

    public double getValeurMoyenneAchat() {
        return totalArticles == 0 ? 0 : valeurAchatInventaire / totalArticles;
    }

    public double getValeurMoyenneVente() {
        return totalArticles == 0 ? 0 : valeurVenteInventaire / totalArticles;
    }

    public double getSommeEcartsAbsolus() {
        return sommeEcartsAbsolus;
    }

    /** Tous les emplacements, du plus gros ecart au plus petit. */
    public List<Emplacement> getEmplacements() {
        return Collections.unmodifiableList(emplacements);
    }

    /** Tous les articles en ecart, du plus gros au plus petit. */
    public List<Article> getArticlesEnEcart() {
        return Collections.unmodifiableList(articles);
    }

    public List<Emplacement> emplacementsNegatifs(int nombre) {
        return premiers(
                emplacements.stream().filter(e -> e.getEcartAchat() < 0).collect(java.util.stream.Collectors.toList()),
                nombre);
    }

    public List<Emplacement> emplacementsPositifs(int nombre) {
        return premiers(
                emplacements.stream().filter(e -> e.getEcartAchat() > 0).collect(java.util.stream.Collectors.toList()),
                nombre);
    }

    public List<Emplacement> emplacementsCritiques(int nombre) {
        return premiers(
                emplacements.stream().filter(e -> e.getEcartAchat() != 0).collect(java.util.stream.Collectors.toList()),
                nombre);
    }

    public List<Article> articlesCritiques(int nombre) {
        return premiers(articles, nombre);
    }

    private static <T> List<T> premiers(List<T> liste, int nombre) {
        return liste.subList(0, Math.min(nombre, liste.size()));
    }

    public Emplacement getEmplacementCritique() {
        return emplacements.stream().filter(e -> e.getEcartAchat() != 0).findFirst().orElse(null);
    }

    public Emplacement getEmplacementFaibleMarge() {
        return emplacements.stream().filter(e -> e.getRatioVA() > 0)
                .min(Comparator.comparingDouble(Emplacement::getRatioVA)).orElse(null);
    }

    // ------------------------------------------------------------------ mise en forme

    private static final DecimalFormatSymbols SYMBOLES = symboles();

    private static DecimalFormatSymbols symboles() {
        DecimalFormatSymbols s = new DecimalFormatSymbols(Locale.FRANCE);
        s.setGroupingSeparator(' ');
        s.setDecimalSeparator(',');
        return s;
    }

    /** « 108 323 109 CFA », signe compris. */
    public static String montant(double valeur) {
        return new DecimalFormat("#,##0", SYMBOLES).format(Math.round(valeur)) + " CFA";
    }

    public static String nombre(double valeur) {
        return new DecimalFormat("#,##0", SYMBOLES).format(Math.round(valeur));
    }

    /** « 32,97 % », signe compris. */
    public static String pourcentage(double valeur) {
        return new DecimalFormat("#,##0.00", SYMBOLES).format(valeur) + " %";
    }

    public static String ratio(double valeur) {
        return valeur == 0 ? "N/A" : new DecimalFormat("0.00", SYMBOLES).format(valeur);
    }

    /** « 1 article sur 3 » : la lecture parlante du taux d'ecart. */
    public String articleSur() {
        double taux = getTauxEcart();
        if (taux <= 0) {
            return "Aucun écart constaté";
        }
        long sur = Math.max(1, Math.round(100 / taux));
        return "Près de 1 article sur " + sur;
    }

    private String commentaireEcart() {
        double e = getEcartAchat();
        return e < 0 ? "Démarque nette" : (e > 0 ? "Surplus net" : "Aucun écart");
    }

    private String commentaireEvolution() {
        double t = getTauxEvolutionAchat();
        return t < 0 ? "Baisse du stock" : (t > 0 ? "Hausse du stock" : "Stock inchangé");
    }

    /** Le resume d'une ligne, rappele sous le titre des editions. */
    public String resumeEnTete() {
        return nombre(totalArticles) + " article(s) retenu(s), " + nombre(articlesEcart) + " en écart ("
                + pourcentage(getTauxEcart()) + ")   -   écart de valeur d'achat " + montant(getEcartAchat()) + " ("
                + pourcentage(getTauxEvolutionAchat()) + ")";
    }

    /** Le paragraphe de synthese, avec les chiffres de l'inventaire. */
    public String syntheseGlobaleTexte() {
        StringBuilder sb = new StringBuilder();
        sb.append("L'inventaire présente un écart net de ").append(montant(getEcartAchat()))
                .append(" en valeur d'achat, soit ").append(getEcartAchat() < 0 ? "une démarque de " : "un surplus de ")
                .append(pourcentage(getTauxEvolutionAchat())).append(". En valeur de vente, l'écart est de ")
                .append(montant(getEcartVente())).append(", soit ").append(pourcentage(getTauxEvolutionVente()))
                .append(". Le taux d'articles modifiés est de ").append(pourcentage(getTauxEcart())).append(" des ")
                .append(nombre(totalArticles)).append(" articles, ce qui signifie que ").append(nombre(articlesEcart))
                .append(" produit(s) présentent un écart.");
        return sb.toString();
    }

    /** Les recommandations, construites sur les chiffres de cet inventaire. */
    public List<String> recommandations() {
        List<String> r = new ArrayList<>();
        Emplacement critique = getEmplacementCritique();
        if (critique != null) {
            r.add("Audit ciblé sur " + critique.getNom() + " : plus forte contribution à l'écart ("
                    + pourcentage(critique.getContributionPct()) + ", " + montant(critique.getEcartAchat())
                    + " en achat). Vérifier les mouvements, les inventaires tournants et les sorties non"
                    + " enregistrées.");
        }
        List<Article> cites = articlesCritiques(5);
        if (!cites.isEmpty()) {
            StringBuilder sb = new StringBuilder("Contrôler en priorité les articles : ");
            for (int i = 0; i < cites.size(); i++) {
                sb.append(i > 0 ? ", " : "").append(cites.get(i).getNom());
            }
            r.add(sb.append('.').toString());
        }
        List<Emplacement> positifs = emplacementsPositifs(1);
        if (!positifs.isEmpty()) {
            r.add("Analyser les écarts positifs : " + positifs.get(0).getNom() + " ("
                    + montant(positifs.get(0).getEcartAchat())
                    + " en achat) peut indiquer une erreur de saisie, un double comptage ou une mauvaise"
                    + " affectation.");
        }
        Emplacement marge = getEmplacementFaibleMarge();
        if (marge != null) {
            r.add("Surveiller la marge de " + marge.getNom() + " : ratio V/A de " + ratio(marge.getRatioVA())
                    + ", le plus faible de l'inventaire.");
        }
        r.add("Renforcer les contrôles : réception, rangement, sortie de stock, formation du personnel et comptages"
                + " tournants réguliers.");
        r.add("Mettre en place un suivi par emplacement : le taux d'écart global est de " + pourcentage(getTauxEcart())
                + ", un suivi par catégorie permet de cibler les zones les plus à risque.");
        return r;
    }

    // ------------------------------------------------------------------ lignes des editions

    /** Les en-tetes de colonnes de chaque section des editions. */
    public static Map<String, String[]> entetes() {
        Map<String, String[]> e = new LinkedHashMap<>();
        e.put(S_GLOBAL, new String[] { "Achat", "Vente", "Commentaire", "", "", "" });
        e.put(S_VALORISATION, new String[] { "Achat", "Vente", "", "", "", "" });
        e.put(S_VIGILANCE_EMPL,
                new String[] { "Écart V.Achat", "Écart V.Vente", "% contrib.", "Ratio V/A", "Art. écart", "" });
        e.put(S_ECARTS_POSITIFS, new String[] { "Écart V.Achat", "Écart V.Vente", "% contrib.", "Ratio V/A", "", "" });
        e.put(S_ARTICLES, new String[] { "Écart V.Achat", "Écart qté", "Emplacement", "", "", "" });
        e.put(S_EMPLACEMENTS, new String[] { "V.Achat machine", "V.Achat inventaire", "Écart V.Achat", "% contrib.",
                "Ratio V/A", "Art. écart" });
        e.put(S_RECO, new String[] { "", "", "", "", "", "" });
        return e;
    }

    /** Les lignes de l'edition simple, tous les emplacements dans la section de detail. */
    public List<BalanceEditionLigneDTO> lignesEditionSimple(int nombreEmplacements, int nombreArticles) {
        return lignesEditionSimple(nombreEmplacements, nombreArticles, "all");
    }

    /**
     * Les lignes de l'edition simple : resume, valorisation, emplacements critiques, articles, detail.
     *
     * @param filtreDetail
     *            « with » ne garde que les emplacements en ecart dans la section de detail, « without » que ceux sans
     *            ecart, toute autre valeur les garde tous. Le resume et les points de vigilance ne sont jamais filtres
     *            : ils decrivent l'inventaire entier.
     */
    public List<BalanceEditionLigneDTO> lignesEditionSimple(int nombreEmplacements, int nombreArticles,
            String filtreDetail) {
        List<BalanceEditionLigneDTO> l = new ArrayList<>();
        l.add(ligne(S_GLOBAL, 1, "Nombre total d'articles", true, nombre(totalArticles), "", "Base globale"));
        l.add(ligne(S_GLOBAL, 1, "Articles en écart", false, nombre(articlesEcart), "",
                pourcentage(getTauxEcart()) + " du total"));
        l.add(ligne(S_GLOBAL, 1, "Articles conformes", false, nombre(getArticlesConformes()), "",
                pourcentage(getTauxConforme()) + " du total"));
        l.add(ligne(S_GLOBAL, 1, "Taux d'articles en écart", false, pourcentage(getTauxEcart()), "", articleSur()));

        l.add(ligne(S_VALORISATION, 2, "Valorisation avant inventaire", false, montant(valeurAchatMachine),
                montant(valeurVenteMachine), "Valeur attendue"));
        l.add(ligne(S_VALORISATION, 2, "Valorisation après inventaire", false, montant(valeurAchatInventaire),
                montant(valeurVenteInventaire), "Valeur constatée"));
        l.add(ligne(S_VALORISATION, 2, "Écart global", true, montant(getEcartAchat()), montant(getEcartVente()),
                commentaireEcart()));
        l.add(ligne(S_VALORISATION, 2, "Taux d'évolution du stock", false, pourcentage(getTauxEvolutionAchat()),
                pourcentage(getTauxEvolutionVente()), commentaireEvolution()));

        for (Emplacement e : emplacementsCritiques(nombreEmplacements)) {
            l.add(ligne(S_VIGILANCE_EMPL, 3, e.getNom(), false, montant(e.getEcartAchat()), montant(e.getEcartVente()),
                    pourcentage(e.getContributionPct()), ratio(e.getRatioVA()), nombre(e.getArticlesEcart())));
        }
        if (emplacementsCritiques(1).isEmpty()) {
            l.add(ligne(S_VIGILANCE_EMPL, 3, "Aucun emplacement en écart", false));
        }

        for (Article a : articlesCritiques(nombreArticles)) {
            l.add(ligne(S_ARTICLES, 4, nomArticle(a), false, montant(a.getEcartValeurAchat()), nombre(a.getEcartQte()),
                    a.getEmplacement()));
        }
        if (articles.isEmpty()) {
            l.add(ligne(S_ARTICLES, 4, "Aucun article en écart", false));
        }

        boolean avecEcart = "with".equalsIgnoreCase(filtreDetail);
        boolean sansEcart = "without".equalsIgnoreCase(filtreDetail);
        for (Emplacement e : emplacements) {
            if ((avecEcart && e.getEcartAchat() == 0) || (sansEcart && e.getEcartAchat() != 0)) {
                continue;
            }
            l.add(ligne(S_EMPLACEMENTS, 5, e.getNom(), false, montant(e.getValeurAchatMachine()),
                    montant(e.getValeurAchatInventaire()), montant(e.getEcartAchat()),
                    pourcentage(e.getContributionPct()), ratio(e.getRatioVA()), nombre(e.getArticlesEcart())));
        }
        if (!avecEcart && !sansEcart) {
            l.add(ligne(S_EMPLACEMENTS, 5, "TOTAL GÉNÉRAL", true, montant(valeurAchatMachine),
                    montant(valeurAchatInventaire), montant(getEcartAchat()), pourcentage(100), "",
                    nombre(articlesEcart)));
        }
        return l;
    }

    /** Les lignes de l'edition de synthese : recapitulatif, vigilance, ecarts positifs, articles, recommandations. */
    public List<BalanceEditionLigneDTO> lignesEditionSynthese() {
        List<BalanceEditionLigneDTO> l = new ArrayList<>();
        l.add(ligne(S_GLOBAL, 1, "Nombre total d'articles", false, nombre(totalArticles), nombre(totalArticles),
                "Base globale"));
        l.add(ligne(S_GLOBAL, 1, "Articles modifiés / en écart", false, nombre(articlesEcart), nombre(articlesEcart),
                pourcentage(getTauxEcart()) + " du total"));
        l.add(ligne(S_GLOBAL, 1, "Taux d'articles en écart", false, pourcentage(getTauxEcart()),
                pourcentage(getTauxEcart()), articleSur()));
        l.add(ligne(S_GLOBAL, 1, "Taux d'articles conformes", false, pourcentage(getTauxConforme()),
                pourcentage(getTauxConforme()), nombre(getArticlesConformes()) + " articles conformes"));
        l.add(ligne(S_GLOBAL, 1, "Valeur machine / théorique", false, montant(valeurAchatMachine),
                montant(valeurVenteMachine), "Valeur attendue"));
        l.add(ligne(S_GLOBAL, 1, "Valeur inventaire / finale", false, montant(valeurAchatInventaire),
                montant(valeurVenteInventaire), "Valeur constatée"));
        l.add(ligne(S_GLOBAL, 1, "Écart de valeur", true, montant(getEcartAchat()), montant(getEcartVente()),
                commentaireEcart()));
        l.add(ligne(S_GLOBAL, 1, "Taux d'évolution du stock", false, pourcentage(getTauxEvolutionAchat()),
                pourcentage(getTauxEvolutionVente()), commentaireEvolution()));
        l.add(ligne(S_GLOBAL, 1, "Valeur moyenne par article", false, montant(getValeurMoyenneAchat()),
                montant(getValeurMoyenneVente()), "Sur base inventaire"));

        for (Emplacement e : emplacementsNegatifs(7)) {
            l.add(ligne(S_VIGILANCE_EMPL, 2, e.getNom(), false, montant(e.getEcartAchat()), montant(e.getEcartVente()),
                    pourcentage(e.getContributionPct()), ratio(e.getRatioVA()), nombre(e.getArticlesEcart())));
        }
        if (emplacementsNegatifs(1).isEmpty()) {
            l.add(ligne(S_VIGILANCE_EMPL, 2, "Aucun écart négatif", false));
        }

        for (Emplacement e : emplacementsPositifs(5)) {
            l.add(ligne(S_ECARTS_POSITIFS, 3, e.getNom(), false, montant(e.getEcartAchat()), montant(e.getEcartVente()),
                    pourcentage(e.getContributionPct()), ratio(e.getRatioVA())));
        }
        if (emplacementsPositifs(1).isEmpty()) {
            l.add(ligne(S_ECARTS_POSITIFS, 3, "Aucun écart positif", false));
        }

        for (Article a : articlesCritiques(5)) {
            l.add(ligne(S_ARTICLES, 4, nomArticle(a), false, montant(a.getEcartValeurAchat()), nombre(a.getEcartQte()),
                    a.getEmplacement()));
        }
        if (articles.isEmpty()) {
            l.add(ligne(S_ARTICLES, 4, "Aucun article en écart", false));
        }

        int rang = 1;
        for (String reco : recommandations()) {
            l.add(ligne(S_RECO, 5, (rang++) + ". " + reco, false));
        }
        return l;
    }

    private static String nomArticle(Article a) {
        return a.getCodeCip() == null || a.getCodeCip().trim().isEmpty() ? String.valueOf(a.getNom())
                : a.getCodeCip() + " - " + a.getNom();
    }

    private static BalanceEditionLigneDTO ligne(String section, int ordre, String libelle, boolean total,
            String... valeurs) {
        return new BalanceEditionLigneDTO(section, ordre, libelle, total, false, valeurs);
    }

    // ------------------------------------------------------------------ onglet de l'ecran

    /** Le contenu de l'onglet « Synthèse & recommandations ». */
    public String html() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"synthese-inventaire\">");
        sb.append("<h3>1. Récapitulatif global</h3>");
        sb.append("<table class=\"si-table\"><tr><th>Indicateur</th><th>Achat</th><th>Vente</th>"
                + "<th>Commentaire</th></tr>");
        ligneHtml(sb, "Nombre total d'articles", nombre(totalArticles), nombre(totalArticles), "Base globale", false);
        ligneHtml(sb, "Articles modifiés / en écart", nombre(articlesEcart), nombre(articlesEcart),
                pourcentage(getTauxEcart()) + " du total", false);
        ligneHtml(sb, "Taux d'articles en écart", pourcentage(getTauxEcart()), pourcentage(getTauxEcart()),
                articleSur(), false);
        ligneHtml(sb, "Taux d'articles conformes", pourcentage(getTauxConforme()), pourcentage(getTauxConforme()),
                nombre(getArticlesConformes()) + " articles conformes", false);
        ligneHtml(sb, "Valeur machine / théorique", montant(valeurAchatMachine), montant(valeurVenteMachine),
                "Valeur attendue", false);
        ligneHtml(sb, "Valeur inventaire / finale", montant(valeurAchatInventaire), montant(valeurVenteInventaire),
                "Valeur constatée", false);
        ligneHtml(sb, "Écart de valeur", montant(getEcartAchat()), montant(getEcartVente()), commentaireEcart(), true);
        ligneHtml(sb, "Taux d'évolution du stock", pourcentage(getTauxEvolutionAchat()),
                pourcentage(getTauxEvolutionVente()), commentaireEvolution(), false);
        ligneHtml(sb, "Valeur moyenne par article", montant(getValeurMoyenneAchat()), montant(getValeurMoyenneVente()),
                "Sur base inventaire", false);
        sb.append("</table>");
        sb.append("<p class=\"si-para\"><b>Synthèse globale :</b> ").append(echappe(syntheseGlobaleTexte()))
                .append("</p>");

        sb.append("<h3>2. Points de vigilance par emplacement</h3>");
        sb.append("<p class=\"si-para\">Les écarts négatifs les plus importants en valeur d'achat :</p>");
        sb.append("<table class=\"si-table\"><tr><th>Emplacement</th><th>Écart V.Achat</th><th>Écart V.Vente</th>"
                + "<th>% contribution</th><th>Ratio V/A</th><th>Articles en écart</th></tr>");
        for (Emplacement e : emplacementsNegatifs(7)) {
            emplacementHtml(sb, e);
        }
        if (emplacementsNegatifs(1).isEmpty()) {
            sb.append("<tr><td colspan=\"6\" class=\"si-vide\">Aucun écart négatif.</td></tr>");
        }
        sb.append("</table>");
        Emplacement critique = getEmplacementCritique();
        if (critique != null) {
            sb.append("<p class=\"si-para\"><b>Emplacement critique :</b> ").append(echappe(critique.getNom()))
                    .append(" concentre à lui seul ").append(pourcentage(critique.getContributionPct()))
                    .append(" de l'écart absolu d'achat, avec ").append(montant(critique.getEcartAchat()))
                    .append(" en achat et ").append(montant(critique.getEcartVente())).append(" en vente.</p>");
        }
        Emplacement marge = getEmplacementFaibleMarge();
        if (marge != null) {
            sb.append("<p class=\"si-para\"><b>Marge à surveiller :</b> ").append(echappe(marge.getNom()))
                    .append(" a le ratio V/A le plus faible (").append(ratio(marge.getRatioVA()))
                    .append("), donc une marge plus réduite que les autres catégories.</p>");
        }
        sb.append("<p class=\"si-para\">À l'inverse, certains emplacements présentent des écarts positifs, signe"
                + " possible d'erreurs de saisie, de double comptage ou de mauvaise affectation :</p>");
        sb.append("<table class=\"si-table\"><tr><th>Emplacement</th><th>Écart V.Achat</th><th>Écart V.Vente</th>"
                + "<th>% contribution</th><th>Ratio V/A</th><th>Articles en écart</th></tr>");
        for (Emplacement e : emplacementsPositifs(5)) {
            emplacementHtml(sb, e);
        }
        if (emplacementsPositifs(1).isEmpty()) {
            sb.append("<tr><td colspan=\"6\" class=\"si-vide\">Aucun écart positif.</td></tr>");
        }
        sb.append("</table>");

        sb.append("<h3>3. Articles les plus critiques</h3>");
        sb.append("<table class=\"si-table\"><tr><th>Article</th><th>Écart V.Achat</th><th>Écart qté</th>"
                + "<th>Emplacement</th></tr>");
        for (Article a : articlesCritiques(5)) {
            sb.append("<tr><td>").append(echappe(nomArticle(a))).append("</td><td class=\"si-n ")
                    .append(a.getEcartValeurAchat() < 0 ? "si-rouge" : "si-vert").append("\">")
                    .append(montant(a.getEcartValeurAchat())).append("</td><td class=\"si-n\">")
                    .append(nombre(a.getEcartQte())).append("</td><td>").append(echappe(a.getEmplacement()))
                    .append("</td></tr>");
        }
        if (articles.isEmpty()) {
            sb.append("<tr><td colspan=\"4\" class=\"si-vide\">Aucun article en écart.</td></tr>");
        }
        sb.append("</table>");
        sb.append("<p class=\"si-para\">Ces articles doivent être vérifiés en priorité : problème de stock, erreur de"
                + " saisie ou démarque réelle.</p>");

        sb.append("<h3>4. Recommandations</h3><ol class=\"si-reco\">");
        for (String reco : recommandations()) {
            sb.append("<li>").append(echappe(reco)).append("</li>");
        }
        sb.append("</ol></div>");
        return sb.toString();
    }

    private void emplacementHtml(StringBuilder sb, Emplacement e) {
        sb.append("<tr><td>").append(echappe(e.getNom())).append("</td><td class=\"si-n ")
                .append(e.getEcartAchat() < 0 ? "si-rouge" : "si-vert").append("\">").append(montant(e.getEcartAchat()))
                .append("</td><td class=\"si-n ").append(e.getEcartVente() < 0 ? "si-rouge" : "si-vert").append("\">")
                .append(montant(e.getEcartVente())).append("</td><td class=\"si-n\">")
                .append(pourcentage(e.getContributionPct())).append("</td><td class=\"si-n\">")
                .append(ratio(e.getRatioVA())).append("</td><td class=\"si-n\">").append(nombre(e.getArticlesEcart()))
                .append("</td></tr>");
    }

    private static void ligneHtml(StringBuilder sb, String libelle, String achat, String vente, String commentaire,
            boolean fort) {
        sb.append(fort ? "<tr class=\"si-fort\"><td>" : "<tr><td>").append(echappe(libelle))
                .append("</td><td class=\"si-n\">").append(achat).append("</td><td class=\"si-n\">").append(vente)
                .append("</td><td class=\"si-c\">").append(echappe(commentaire)).append("</td></tr>");
    }

    private static String echappe(String valeur) {
        if (valeur == null) {
            return "";
        }
        return valeur.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
