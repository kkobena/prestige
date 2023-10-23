package rest.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author koben
 */
@Getter
@Setter
@Builder
public class CoffreCaisseDTO {

    private String id;
    private String statut;
    private String userId;
    private String userFullName;
    private String createAt;
    private String updateAt;
    private int amount;
    private boolean display;
    private String createdByFullName;

}
