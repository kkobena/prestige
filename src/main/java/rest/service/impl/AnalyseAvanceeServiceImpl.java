
package rest.service.impl;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import rest.service.AnalyseAvanceeService;
import rest.service.AnalyseInvDTOService;
import rest.service.dto.AnalyseInvDTO;

/**
 *
 * @author airman
 */
@Stateless
public class AnalyseAvanceeServiceImpl implements AnalyseAvanceeService {

    @EJB
    private AnalyseInvDTOService analyseInvDTOService;

    @Override
    public AnalyseAvanceeDTO getAnalyseAvancee(String inventaireId, String inventaireName) {
        List<AnalyseInvDTO> rawData = analyseInvDTOService.listAnalyseInv(inventaireId);

        AnalyseAvanceeDTO response = new AnalyseAvanceeDTO();
        response.setSynthese(buildSynthese(rawData));
        response.setAnalyseABC(buildAnalyseABC(rawData));
        response.setDetailProduits(buildDetailProduits(rawData));
        response.setAnalysisText(generateTextualAnalysis(response, inventaireName));

        return response;
    }

    @Override
    public String generateTextualAnalysis(AnalyseAvanceeDTO data, String inventaireName) {
        StringBuilder analysis = new StringBuilder();
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.FRANCE);

        long totalEcartNet = data.getSynthese().stream().mapToLong(SyntheseEmplacementDTO::getEcartValeurAchat).sum();
        long totalValeurMachine = data.getSynthese().stream().mapToLong(SyntheseEmplacementDTO::getValeurAchatMachine)
                .sum();
        double tauxDemarqueGlobal = (totalValeurMachine != 0) ? ((double) totalEcartNet / totalValeurMachine) * 100.0
                : 0.0;
        int produitsModifies = (int) data.getDetailProduits().stream().filter(p -> p.getEcartQuantite() != 0).count();
        int totalProduits = data.getDetailProduits().size();

        analysis.append("Synthèse & Recommandations\n\n");
        analysis.append(String.format("Date du Rapport : %s\n",
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM uuuu", Locale.FRENCH))));
        analysis.append(String.format("Inventaire : %s\n\n", inventaireName));
        analysis.append("1. Synthèse Globale\n");
        analysis.append(String.format(
                "L'inventaire présente un écart global net de %s FCFA, ce qui représente une démarque de %.2f%%.\n",
                nf.format(totalEcartNet), tauxDemarqueGlobal));
        analysis.append(String.format("Sur un total de %d produits, %d lignes ont été modifiées.\n\n", totalProduits,
                produitsModifies));

        if (!data.getAnalyseABC().isEmpty()) {
            analysis.append("2. Points de Vigilance (Analyse ABC)\n");
            long countA = data.getAnalyseABC().stream().filter(p -> "A".equals(p.getCategorie())).count();
            analysis.append(String.format(
                    "L'analyse révèle que les %d produits de catégorie A sont les plus critiques et doivent être examinés en priorité.\n\n",
                    countA));
        }

        analysis.append("3. Performance par Emplacement\n");
        data.getSynthese().stream().max(Comparator.comparingDouble(s -> Math.abs(s.getEcartValeurAchat())))
                .ifPresent(e -> analysis.append(String.format(
                        "- Emplacement Critique : L'emplacement '%s' est le plus problématique, contribuant à %.2f%% de l'écart total.\n",
                        e.getEmplacement(), e.getContributionEcart())));

        data.getSynthese().stream().filter(e -> e.getRatioVA() > 0 && e.getRatioVA() < 1.51)
                .min(Comparator.comparingDouble(SyntheseEmplacementDTO::getRatioVA))
                .ifPresent(e -> analysis.append(String.format(
                        "- Conformité des Marges : L'emplacement '%s' présente le ratio V/A le plus faible (%.2f), indiquant une vigilance sur les marges.\n\n",
                        e.getEmplacement(), e.getRatioVA())));

        analysis.append("4. Recommandations\n");
        analysis.append(
                "1. Action Immédiate : Lancer un contrôle ciblé sur les produits de catégorie A pour identifier la source des écarts.\n");
        analysis.append(
                "2. Audit d'Emplacement : Mener un audit des procédures sur les emplacements à forte contribution d'écart.\n");
        analysis.append("3. Revue des Prix : Analyser la politique de prix des emplacements à faible ratio V/A.\n");

        return analysis.toString();
    }

    private List<DetailProduitDTO> buildDetailProduits(List<AnalyseInvDTO> rawData) {
        return rawData.stream().map(item -> {
            DetailProduitDTO dto = new DetailProduitDTO();
            dto.setNom(item.getNom());
            dto.setEmplacement(item.getEmplacement());

            int qteInitiale = item.getQteInitiale() != null ? item.getQteInitiale() : 0;
            int qteSaisie = item.getQteSaisie() != null ? item.getQteSaisie() : 0;
            int prixAchat = item.getPrixAchat() != null ? item.getPrixAchat() : 0;
            int prixVente = item.getPrixVente() != null ? item.getPrixVente() : 0;

            dto.setQteInitiale(qteInitiale);
            dto.setQteSaisie(qteSaisie);
            int ecartQte = qteSaisie - qteInitiale;
            dto.setEcartQuantite(ecartQte);
            dto.setEcartValeurAchat((long) ecartQte * prixAchat);
            dto.setPrixAchat(prixAchat);
            dto.setPrixVente(prixVente);
            return dto;
        }).collect(Collectors.toList());
    }

    private List<SyntheseEmplacementDTO> buildSynthese(List<AnalyseInvDTO> rawData) {
        long totalEcartAbsoluInventaire = rawData.stream().mapToLong(item -> {
            int qteInitiale = item.getQteInitiale() != null ? item.getQteInitiale() : 0;
            int qteSaisie = item.getQteSaisie() != null ? item.getQteSaisie() : 0;
            int prixAchat = item.getPrixAchat() != null ? item.getPrixAchat() : 0;
            return Math.abs((long) (qteSaisie - qteInitiale) * prixAchat);
        }).sum();

        Map<String, List<AnalyseInvDTO>> groupedByEmplacement = rawData.stream()
                .collect(Collectors.groupingBy(AnalyseInvDTO::getEmplacement));

        return groupedByEmplacement.entrySet().stream().map(entry -> {
            String emplacement = entry.getKey();
            List<AnalyseInvDTO> items = entry.getValue();

            long valeurAchatMachine = items.stream()
                    .mapToLong(i -> (long) (i.getQteInitiale() != null ? i.getQteInitiale() : 0)
                            * (i.getPrixAchat() != null ? i.getPrixAchat() : 0))
                    .sum();
            long valeurAchatRayon = items.stream()
                    .mapToLong(i -> (long) (i.getQteSaisie() != null ? i.getQteSaisie() : 0)
                            * (i.getPrixAchat() != null ? i.getPrixAchat() : 0))
                    .sum();
            long valeurVenteMachine = items.stream()
                    .mapToLong(i -> (long) (i.getQteInitiale() != null ? i.getQteInitiale() : 0)
                            * (i.getPrixVente() != null ? i.getPrixVente() : 0))
                    .sum();
            long valeurVenteRayon = items.stream()
                    .mapToLong(i -> (long) (i.getQteSaisie() != null ? i.getQteSaisie() : 0)
                            * (i.getPrixVente() != null ? i.getPrixVente() : 0))
                    .sum();
            long ecartEmplacement = valeurAchatRayon - valeurAchatMachine;

            // --- CORRECTION DU CALCUL DU RATIO ---
            double sumOfRatios = 0;
            int countOfItemsWithPrice = 0;
            for (AnalyseInvDTO item : items) {
                Integer pa = item.getPrixAchat();
                Integer pv = item.getPrixVente();
                if (pa != null && pa > 0 && pv != null) {
                    sumOfRatios += (double) pv / pa;
                    countOfItemsWithPrice++;
                }
            }
            double averageRatio = (countOfItemsWithPrice > 0) ? sumOfRatios / countOfItemsWithPrice : 0.0;
            // --- FIN DE LA CORRECTION ---

            SyntheseEmplacementDTO dto = new SyntheseEmplacementDTO();
            dto.setEmplacement(emplacement);
            dto.setValeurAchatMachine(valeurAchatMachine);
            dto.setValeurAchatRayon(valeurAchatRayon);
            dto.setValeurVenteMachine(valeurVenteMachine);
            dto.setValeurVenteRayon(valeurVenteRayon); // Ajout de la nouvelle valeur
            dto.setEcartValeurAchat(ecartEmplacement);
            dto.setTauxDemarque(
                    (valeurAchatMachine != 0) ? ((double) ecartEmplacement / valeurAchatMachine) * 100.0 : 0.0);
            dto.setContributionEcart((totalEcartAbsoluInventaire != 0)
                    ? (Math.abs(ecartEmplacement) / (double) totalEcartAbsoluInventaire) * 100.0 : 0.0);
            dto.setRatioVA(averageRatio);
            return dto;
        }).collect(Collectors.toList());
    }

    private List<AnalyseAbcDTO> buildAnalyseABC(List<AnalyseInvDTO> rawData) {

        class EcartItem {
            final AnalyseInvDTO item;
            final long ecartAbsoluEnValeur;

            EcartItem(AnalyseInvDTO item, long ecartAbsoluEnValeur) {
                this.item = item;
                this.ecartAbsoluEnValeur = ecartAbsoluEnValeur;
            }
        }

        List<EcartItem> itemsAvecEcart = new ArrayList<>();
        for (AnalyseInvDTO item : rawData) {
            Integer qteInitialeObj = item.getQteInitiale();
            Integer qteSaisieObj = item.getQteSaisie();

            if (qteInitialeObj != null && qteSaisieObj != null && !qteInitialeObj.equals(qteSaisieObj)) {
                int prixAchat = item.getPrixAchat() != null ? item.getPrixAchat() : 0;
                long ecartAbsolu = Math.abs((long) (qteSaisieObj - qteInitialeObj) * prixAchat);
                itemsAvecEcart.add(new EcartItem(item, ecartAbsolu));
            }
        }

        long totalEcartAbsolu = itemsAvecEcart.stream().mapToLong(e -> e.ecartAbsoluEnValeur).sum();

        itemsAvecEcart.sort(new Comparator<EcartItem>() {
            @Override
            public int compare(EcartItem o1, EcartItem o2) {
                return Long.compare(o2.ecartAbsoluEnValeur, o1.ecartAbsoluEnValeur);
            }
        });

        List<AnalyseAbcDTO> abcList = new ArrayList<>();
        double pourcentageCumule = 0.0;

        for (EcartItem ecartItem : itemsAvecEcart) {
            AnalyseInvDTO item = ecartItem.item;
            AnalyseAbcDTO dto = new AnalyseAbcDTO();

            int qteInitiale = item.getQteInitiale();
            int qteSaisie = item.getQteSaisie();
            int prixAchat = item.getPrixAchat() != null ? item.getPrixAchat() : 0;

            long ecartValeur = (long) (qteSaisie - qteInitiale) * prixAchat;
            double pourcentageEcart = (totalEcartAbsolu != 0)
                    ? (Math.abs(ecartValeur) / (double) totalEcartAbsolu) * 100.0 : 0.0;
            pourcentageCumule += pourcentageEcart;

            dto.setNom(item.getNom());
            dto.setEcartValeurAchat(ecartValeur);
            dto.setPourcentageEcartTotal(pourcentageEcart);
            dto.setPourcentageCumule(pourcentageCumule);

            if (pourcentageCumule <= 80) {
                dto.setCategorie("A");
            } else if (pourcentageCumule <= 95) {
                dto.setCategorie("B");
            } else {
                dto.setCategorie("C");
            }
            abcList.add(dto);
        }
        return abcList;
    }
}
