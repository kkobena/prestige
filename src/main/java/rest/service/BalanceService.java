
package rest.service;

import commonTasks.dto.BalanceDTO;
import dal.TPreenregistrementDetail;
import java.util.List;
import javax.ejb.Local;
import rest.service.dto.BalanceParamsDTO;

/**
 *
 * @author koben
 */
@Local
public interface BalanceService {
    
    List<TPreenregistrementDetail> listPreenregistrements(BalanceParamsDTO balanceParams);
    
    List<BalanceDTO> buildBalanceFromPreenregistrement(BalanceParamsDTO balanceParams);
    
    
}
