package rest.service;

import commonTasks.dto.BalanceDTO;
import commonTasks.dto.GenericDTO;
import java.util.List;
import javax.ejb.Local;
import org.json.JSONObject;
import rest.service.dto.BalanceParamsDTO;

/**
 *
 * @author koben
 */
@Local
public interface BalanceService {

    List<BalanceDTO> buildBalanceFromPreenregistrement(BalanceParamsDTO balanceParams);

    GenericDTO getBalanceVenteCaisseData(BalanceParamsDTO balanceParams);

    JSONObject getBalanceVenteCaisseDataView(BalanceParamsDTO balanceParams);
}
