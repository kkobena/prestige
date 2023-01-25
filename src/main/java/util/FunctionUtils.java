package util;

import dal.TOrderDetail;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author koben
 */
public final class FunctionUtils {

    private static final String TOTAL = "total";
    private static final String DATA = "data";
    public static final Predicate<TOrderDetail> ECART_PRIX_VENTE_30 = t -> {
        if (Objects.isNull(t.getPrixUnitaire())) {
            return Math.abs(t.getLgFAMILLEID().getIntPRICE() - t.getIntPRICEDETAIL()) != 30;
        }
        return Math.abs(t.getLgFAMILLEID().getIntPRICE() - t.getPrixUnitaire()) != 30;
    };

    public static JSONObject returnData(List<?> data, long total) {
        if (CollectionUtils.isEmpty(data)) {
            return new JSONObject().put(TOTAL, 0).put(DATA, new JSONArray(Collections.emptyList()));
        }
        return new JSONObject().put(TOTAL, total).put(DATA, new JSONArray(data));
    }

    public static JSONObject returnData(List<?> data) {
        if (CollectionUtils.isEmpty(data)) {
            return new JSONObject().put(TOTAL, 0).put(DATA, new JSONArray(Collections.emptyList()));
        }
        return new JSONObject().put(TOTAL, data.size()).put(DATA, new JSONArray(data));
    }

    private FunctionUtils() {

    }
}
