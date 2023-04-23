package rest.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author koben
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"userId"})
public class TicketZDTO {

    private long totalEsp;
    private long totalMobile;
    private long totalCredit;
    private long totalCheque;
    private long totalVirement;
    private long totalCB;
    private long differe;
    private String user;
     private String userId;
    private long totalEntreeEsp;
    private long totalEntreeMobile;
    private long totalEntreeCredit;
    private long totalEntreeCheque;
    private long totalEntreeVirement;
    private long totalEntreeCB;

    private long totalReglementEsp;
    private long totalReglementMobile;
    private long totalReglementCredit;
    private long totalReglementCheque;
    private long totalReglementVirement;
    private long totalReglementCB;

    private long totalSortieEsp;
    private long totalSortieMobile;
    private long totalSortieCredit;
    private long totalSortieCheque;
    private long totalSortieVirement;
    private long totalSortieCB;


}
