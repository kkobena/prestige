package rest.service.calculation;

import dal.PrixReferenceType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import rest.service.calculation.dto.CalculatedShare;
import rest.service.calculation.dto.CalculationInput;
import rest.service.calculation.dto.CalculationResult;
import rest.service.calculation.dto.NatureVente;
import dal.Rate;
import javax.ejb.Stateless;
import rest.service.calculation.dto.SaleItemInput;
import rest.service.calculation.dto.TiersPayantInput;
import rest.service.calculation.dto.TiersPayantLineOutput;
import rest.service.calculation.dto.TiersPayantPrixInput;

@Stateless
public class TiersPayantCalculationService {

    public CalculationResult calculate(CalculationInput input) {
        CalculationResult calculationResult = new CalculationResult();
        BigDecimal totalAmountAssurance = BigDecimal.ZERO;
        BigDecimal itemPartAssure = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        Map<String, BigDecimal> tiersPayants = new HashMap<>();
        if (CollectionUtils.isEmpty(input.getSaleItems())) {
            return null;
        }
        for (SaleItemInput saleItemInput : input.getSaleItems()) {
            CalculatedShare itemShare = calculateSaleItem(saleItemInput, input.getTiersPayants(),
                    input.getNatureVente());
            totalAmountAssurance = totalAmountAssurance.add(itemShare.getTotalReimbursedAmount());
            discountAmount = discountAmount.add(saleItemInput.getDiscountAmount());
            itemPartAssure = itemPartAssure.add(itemShare.getPatientShare());
            itemShare.getTiersPayants().forEach((clientTiersPayantId, montant) -> tiersPayants
                    .merge(clientTiersPayantId, montant, BigDecimal::add));
            calculationResult.getItemShares().add(itemShare);
        }
        calculationResult.setDiscountAmount(discountAmount.setScale(0, RoundingMode.HALF_UP));
        calculationResult.setTotalSaleAmount(input.getTotalSalesAmount().setScale(0, RoundingMode.HALF_UP));
        List<TiersPayantLineOutput> lineOutputs = new ArrayList<>();
        StringBuilder warnings = new StringBuilder();
        for (TiersPayantInput tpInput : input.getTiersPayants()) {
            BigDecimal remainingAmountForTps = tiersPayants.getOrDefault(tpInput.getClientTiersPayantId(),
                    BigDecimal.ZERO);
            remainingAmountForTps = remainingAmountForTps.setScale(0, RoundingMode.HALF_UP);
            BigDecimal actualShare = applyCeilings(remainingAmountForTps, tpInput, warnings);
            totalAmountAssurance = totalAmountAssurance.add(actualShare).subtract(remainingAmountForTps);
            totalAmountAssurance = totalAmountAssurance.setScale(0, RoundingMode.HALF_UP);
            TiersPayantLineOutput lineOutput = new TiersPayantLineOutput();
            lineOutput.setClientTiersPayantId(tpInput.getClientTiersPayantId());
            lineOutput.setMontant(actualShare);
            lineOutput.setFinalTaux(calculateFinalTaux(actualShare, calculationResult.getTotalSaleAmount()));
            lineOutputs.add(lineOutput);
        }
        calculationResult.setTotalTiersPayant(totalAmountAssurance);
        BigDecimal partAssure = calculatePatientShare(calculationResult, input.getNatureVente());
        calculationResult.setTotalPatientShare(partAssure);
        calculationResult.setTiersPayantLines(lineOutputs);
        calculationResult.setWarningMessage(warnings.toString());
        return calculationResult;
    }

    private BigDecimal applyCeilings(BigDecimal partTiersPayantNet, TiersPayantInput tp, StringBuilder warnings) {
        BigDecimal finalAmount = computeThirdPartyPart(tp, partTiersPayantNet);
        if (finalAmount.compareTo(partTiersPayantNet) != 0) {
            warnings.append("Le montant remboursé pour le tiers payant ")
                    .append(" <span style='font-weight:900;color:blue;text-decoration: underline;'> ")
                    .append(tp.getTiersPayantFullName()).append("</span> a été plafonné à ")
                    .append(" <span style='font-weight:900;color:blue;text-decoration: underline;'> ")
                    .append(finalAmount).append("</span>.\n");
        }
        return finalAmount;
    }

    private BigDecimal computeThirdPartyPart(TiersPayantInput tp, BigDecimal partTiersPayantNet) {
        BigDecimal totalNetAmount = computePlafond(tp.getPlafondConso(), tp.getConsoMensuelle(), partTiersPayantNet);// plafon
                                                                                                                     // tiers
                                                                                                                     // payant
                                                                                                                     // mensuel
                                                                                                                     // consommation
        return computePlafondClient(tp, totalNetAmount);
    }

    private BigDecimal computePlafondClient(TiersPayantInput tp, BigDecimal partTiersPayantNet) {
        BigDecimal totalNetAmount = computePlafond(tp.getPlafondConso(), tp.getConsoMensuelle(), partTiersPayantNet);// plafon
                                                                                                                     // mensuel
                                                                                                                     // consommation
        return computePlafondVente(tp.getPlafondJournalierClient(), totalNetAmount);
    }

    private BigDecimal computePlafondVente(BigDecimal plafondVente, BigDecimal totalNetAmount) {
        if (plafondVente == null) {
            return totalNetAmount;
        }
        return totalNetAmount.min(plafondVente);
    }

    private BigDecimal computePlafond(BigDecimal plafond, BigDecimal conso, BigDecimal partTiersPayantNet) {
        if (plafond == null) {
            return partTiersPayantNet; // Pas de plafond → on rembourse tout
        }

        // Si déjà au plafond ou au-delà → rien
        if (conso.compareTo(plafond) >= 0) {
            return BigDecimal.ZERO;
        }
        // Reste disponible avant plafond
        BigDecimal reste = plafond.subtract(conso);

        // On rembourse au maximum la part demandée, sinon le reste disponible
        return partTiersPayantNet.min(reste);
    }

    private CalculatedShare calculateSaleItem(SaleItemInput saleItem, List<TiersPayantInput> tiersPayantInputs,
            NatureVente natureVente) {
        tiersPayantInputs.sort(Comparator.comparingInt(tp -> tp.getPriorite()));
        CalculatedShare itemShare = new CalculatedShare();
        itemShare.setPharmacyPrice(saleItem.getRegularUnitPrice());
        itemShare.setSaleLineId(saleItem.getSalesLineId());
        itemShare.setDiscountAmount(saleItem.getDiscountAmount());
        BigDecimal totalPartTiersPayant = BigDecimal.ZERO;
        int prixReference = saleItem.getPrixAssurances().stream()
                .filter(p -> p.getOptionPrixType() != PrixReferenceType.TAUX).mapToInt(TiersPayantPrixInput::getPrice)
                .min().orElse(0);
        boolean hasOptionPrix = !saleItem.getPrixAssurances().isEmpty();
        BigDecimal calculationBaseUni = prixReference > 0 ? BigDecimal.valueOf(prixReference)
                : itemShare.getPharmacyPrice();
        BigDecimal calculationBase = calculationBaseUni.multiply(BigDecimal.valueOf(saleItem.getQuantity()));
        itemShare.setCalculationBasePrice(calculationBaseUni.intValue());
        for (TiersPayantInput tiersPayantInput : tiersPayantInputs) {
            float rate = tiersPayantInput.getTaux();
            if (hasOptionPrix) {
                TiersPayantPrixInput tiersPayantPrixInput = saleItem.getPrixAssurances().stream()
                        .filter(p -> p.getCompteTiersPayantId().equals(tiersPayantInput.getClientTiersPayantId()))
                        .findFirst().orElse(null);

                if (tiersPayantPrixInput != null
                        && tiersPayantPrixInput.getOptionPrixType() != PrixReferenceType.PRIX_REFERENCE) {
                    rate = tiersPayantPrixInput.getRate() / 100.0f;
                    itemShare.getRates()
                            .add(new Rate(saleItem.getSalesLineId(), tiersPayantInput.getClientTiersPayantId(), rate));

                }
            }
            BigDecimal remainingAmountForTps = calculationBase.subtract(totalPartTiersPayant);
            if (remainingAmountForTps.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal actualShare = calculationBase.multiply(BigDecimal.valueOf(rate));
            if (rate == 1.0f && natureVente == NatureVente.ASSURANCE) { // formulle confort
                remainingAmountForTps = saleItem.getTotalSalesAmount().subtract(totalPartTiersPayant);
                actualShare = BigDecimal.ZERO.max(remainingAmountForTps);
            } else {
                actualShare = actualShare.min(remainingAmountForTps);
            }

            totalPartTiersPayant = totalPartTiersPayant.add(actualShare);
            itemShare.getTiersPayants().put(tiersPayantInput.getClientTiersPayantId(), actualShare);
        }
        itemShare.setTotalReimbursedAmount(totalPartTiersPayant);
        // calculatePatientShare(itemShare);

        return itemShare;
    }

    private void calculatePatientShare(CalculatedShare itemShare) {
        BigDecimal patientPart = itemShare.getTotalPrice().subtract(itemShare.getTotalReimbursedAmount())
                .subtract(itemShare.getDiscountAmount());
        itemShare.setPatientShare(patientPart.max(BigDecimal.ZERO));

    }

    private BigDecimal calculatePatientShare(CalculationResult calculationResult, NatureVente nature) {
        if (nature == NatureVente.ASSURANCE) {

            BigDecimal patientPart = calculationResult.getTotalSaleAmount()
                    .subtract(calculationResult.getTotalTiersPayant()).subtract(calculationResult.getDiscountAmount())
                    .setScale(0, RoundingMode.HALF_UP);
            return patientPart.max(BigDecimal.ZERO);
        }
        if (nature == NatureVente.CARNET) {
            BigDecimal partTiersPayant = calculationResult.getTotalTiersPayant()
                    .subtract(calculationResult.getDiscountAmount()).max(BigDecimal.ZERO)
                    .setScale(0, RoundingMode.HALF_UP);
            calculationResult.setTotalTiersPayant(partTiersPayant);
            BigDecimal netAmount = calculationResult.getTotalSaleAmount().subtract(partTiersPayant).max(BigDecimal.ZERO)
                    .setScale(0, RoundingMode.HALF_UP);
            return netAmount.max(BigDecimal.ZERO);
        }
        /*
         * else if (nature == NatureVente.CARNET) {
         *
         * }
         */
        return calculationResult.getTotalSaleAmount().setScale(0, RoundingMode.HALF_UP);
    }

    private int calculateFinalTaux(BigDecimal actualShare, BigDecimal totalAmount) {
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return actualShare.multiply(BigDecimal.valueOf(100)).divide(totalAmount, 0, RoundingMode.CEILING).intValue();
    }
}
