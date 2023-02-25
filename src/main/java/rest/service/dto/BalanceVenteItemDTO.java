package rest.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author koben
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class BalanceVenteItemDTO {

    private LocalDate mvtDate;
    private String typeVente;
    private String typeReglment;
    private BigDecimal montantVenteDetail;
    private BigDecimal montantAchat;
    private BigDecimal montantTva;
    private BigDecimal montantUG;
    private BigDecimal montantRemise;

}
