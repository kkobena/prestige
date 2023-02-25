
package rest.service.dto;


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
public class BalanceParamsDTO {
 private   String dtStart;
private String   dtEnd;
private boolean checked;
private String emplacementId;
private Boolean excludeSome;
}
