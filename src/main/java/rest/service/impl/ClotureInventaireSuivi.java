package rest.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;

/**
 * Avancement d'une cloture d'inventaire, lu par l'ecran pendant le traitement (retour du 13/09).
 *
 * <p>
 * La cloture est une suite d'ordres SQL ensemblistes : l'avancement compte les ETAPES reellement franchies et les
 * lignes touchees par chacune, rien de simule. L'etat vit en memoire le temps de la requete et reste lisible quelques
 * minutes apres, le temps que l'ecran affiche le recapitulatif.
 * </p>
 */
public final class ClotureInventaireSuivi {

    private static final Map<String, ClotureInventaireSuivi> EN_COURS = new ConcurrentHashMap<>();
    private static final long RETENTION_MS = 10 * 60 * 1000L;

    public static final List<String> ETAPES_RAYON = Collections.unmodifiableList(
            List.of("Stock rayon", "Stock par type", "Date du dernier inventaire", "Historique des mouvements",
                    "Mouvements du jour", "Instantanés du jour", "Lignes de l'inventaire", "En-tête de l'inventaire"));
    public static final List<String> ETAPES_RESERVE = Collections
            .unmodifiableList(List.of("Stock réserve", "Lignes de l'inventaire", "En-tête de l'inventaire"));

    private final String inventaireId;
    private final List<String> etapes;
    private final long debutMs = System.currentTimeMillis();
    private volatile int etapeEnCours = 0; // 1..n pendant le traitement
    private volatile long lignesTraitees = 0;
    private volatile boolean termine = false;
    private volatile boolean succes = false;
    private volatile long finMs = 0;
    private final List<String> journal = new ArrayList<>();

    private ClotureInventaireSuivi(String inventaireId, List<String> etapes) {
        this.inventaireId = inventaireId;
        this.etapes = etapes;
    }

    /** Ouvre le suivi d'une cloture ; les suivis anciens sont oublies. */
    public static ClotureInventaireSuivi demarrer(String inventaireId, boolean reserve) {
        long maintenant = System.currentTimeMillis();
        EN_COURS.entrySet().removeIf(e -> e.getValue().termine && maintenant - e.getValue().finMs > RETENTION_MS);
        ClotureInventaireSuivi suivi = new ClotureInventaireSuivi(inventaireId,
                reserve ? ETAPES_RESERVE : ETAPES_RAYON);
        EN_COURS.put(inventaireId, suivi);
        return suivi;
    }

    public static ClotureInventaireSuivi lire(String inventaireId) {
        return EN_COURS.get(inventaireId);
    }

    /** L'etape numero {@code numero} (1..n) commence. */
    public void etape(int numero) {
        this.etapeEnCours = numero;
    }

    /** L'etape en cours est finie ; {@code lignes} lignes touchees. */
    public void etapeFinie(long lignes) {
        this.lignesTraitees += Math.max(0, lignes);
        synchronized (journal) {
            journal.add(libelle(etapeEnCours) + " : " + lignes + " ligne(s)");
        }
    }

    public void terminer(boolean succes) {
        this.termine = true;
        this.succes = succes;
        this.finMs = System.currentTimeMillis();
        if (succes) {
            this.etapeEnCours = etapes.size();
        }
    }

    private String libelle(int numero) {
        return numero >= 1 && numero <= etapes.size() ? etapes.get(numero - 1) : "";
    }

    public long dureeMs() {
        return (finMs > 0 ? finMs : System.currentTimeMillis()) - debutMs;
    }

    public JSONObject etat() {
        int total = etapes.size();
        int faites = termine && succes ? total : Math.max(0, etapeEnCours - 1);
        JSONObject json = new JSONObject();
        json.put("inventaireId", inventaireId);
        json.put("etape", etapeEnCours);
        json.put("totalEtapes", total);
        json.put("libelle", termine ? (succes ? "Terminé" : "Interrompu") : libelle(etapeEnCours));
        json.put("pourcentage", total == 0 ? 0 : (int) Math.round(faites * 100.0 / total));
        json.put("lignesTraitees", lignesTraitees);
        json.put("termine", termine);
        json.put("succes", succes);
        json.put("dureeMs", dureeMs());
        synchronized (journal) {
            json.put("journal", new ArrayList<>(journal));
        }
        return json;
    }
}
