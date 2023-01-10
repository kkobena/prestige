package job.ejb;

import com.mchange.v2.c3p0.C3P0Registry;
import dal.TParameters;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import util.DateUtil;

/**
 *
 * @author koben
 */
@Singleton
@Startup
@TransactionManagement(value = TransactionManagementType.BEAN)
public class StockReaproService {

    private static final Logger LOG = Logger.getLogger(StockReaproService.class.getName());
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @Inject
    private UserTransaction userTransaction;
    @Resource(name = "concurrent/__defaultManagedScheduledExecutorService")
    private ManagedScheduledExecutorService scheduledExecutorService;
    private Integer dayStock;
    private Integer delayReappro;

    @PostConstruct
    public void init() {

        scheduledExecutorService.scheduleAtFixedRate(this::execute, 0, 2, TimeUnit.HOURS);

    }

    private void execute() {
        if (checkExecutePossible()) {
            try {

                LocalDate lastMonth = DateUtil.getLastMonthFromNow();
                LocalDate threeMonthAgo = DateUtil.getNthLastMonthFromNow(3);
                userTransaction.begin();
                StoredProcedureQuery q = em.createStoredProcedureQuery("UpdateStockReapro");
                userTransaction.commit();
                q.execute();
            } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
                LOG.log(Level.SEVERE, null, e);
            }
        }

    }

    private boolean checkExecutePossible() {

        TParameters p = getParameters("KEY_DAY_SEUIL_REAPPRO");//derniere date de mise a jour stock reappro
        if (p == null) {
            return false;
        }
        LocalDate date = LocalDate.parse(p.getStrVALUE());
        return (date.getMonthValue() != LocalDate.now().getMonthValue());

    }

    public Integer getDayStock() {
        if (dayStock == null) {
            TParameters p = getParameters("KEY_DAY_STOCK");
            if (p == null) {
                return 0;
            }
            dayStock = Integer.valueOf(p.getStrVALUE().trim());
        }
        return dayStock;
    }

    public Integer getDelayReappro() {
        if (delayReappro == null) {
            TParameters p = getParameters("KEY_DELAI_REAPPRO");
            if (p == null) {
                return 0;
            }
            delayReappro = Integer.valueOf(p.getStrVALUE().trim());
        }
        return delayReappro;
    }

    private TParameters getParameters(String key) {
        try {
            return em.getReference(TParameters.class, "key");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "paramettre " + key + " n'existe pas", e);
            return null;
        }
    }
    
    /*
    BEGIN 
DECLARE LGFAMILLEID VARCHAR(40);
DECLARE int_MONTH_LAST_SEUIL_UPDATE INT(11); 
DECLARE dt_LASTDAY_PREVIEW_MONTH VARCHAR(15); 
DECLARE dt_FIRSTDAY_3MONTH_AGO VARCHAR(15); 
DECLARE int_QTE_VENDUS DOUBLE; 
DECLARE int_QTE_JOUR DOUBLE; 
DECLARE intSEUILMINI DOUBLE; 
DECLARE intSEUILMAX DOUBLE; 
DECLARE intQUANTITEREAPPRO DOUBLE; 
DECLARE int_JOUR_STOCK DOUBLE; 
DECLARE int_DELAI_REAPPRO DOUBLE; 

DECLARE done INT DEFAULT 0;

DECLARE curbl CURSOR FOR 
SELECT t.lg_FAMILLE_ID FROM t_famille t WHERE t.str_STATUT = 'enable'
AND t.bool_DECONDITIONNE = 0;

DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=1;


SELECT MONTH(t.str_VALUE) into int_MONTH_LAST_SEUIL_UPDATE from t_parameters t where t.str_KEY ='KEY_DAY_SEUIL_REAPPRO';
IF (int_MONTH_LAST_SEUIL_UPDATE <> MONTH(CURRENT_DATE())) THEN
	SELECT DATE_SUB(CURDATE(), INTERVAL DAYOFMONTH(CURDATE()) DAY) into dt_LASTDAY_PREVIEW_MONTH;
	SELECT DATE_SUB(DATE_SUB(CURDATE(), INTERVAL DAYOFMONTH(CURDATE())-1 DAY), INTERVAL 3 MONTH) into dt_FIRSTDAY_3MONTH_AGO;
	SELECT t.str_VALUE into int_JOUR_STOCK from t_parameters t where t.str_KEY ='KEY_DAY_STOCK';
	SELECT t.str_VALUE into int_DELAI_REAPPRO from t_parameters t where t.str_KEY ='KEY_DELAI_REAPPRO';
	OPEN curbl;
	bl_loop:LOOP
	FETCH curbl INTO LGFAMILLEID;
	IF done=1 THEN 
		LEAVE bl_loop;
	END IF;
	
	SELECT (CASE WHEN SUM(t.int_QUANTITY) IS NOT NULL THEN SUM(t.int_QUANTITY) ELSE 0 END) into int_QTE_VENDUS FROM t_preenregistrement_detail t, t_preenregistrement p 
	WHERE p.lg_PREENREGISTREMENT_ID = t.lg_PREENREGISTREMENT_ID AND p.str_STATUT = 'is_Closed' AND p.int_PRICE >= 0 AND p.`b_IS_CANCEL` = 0 
	AND DATE(p.dt_UPDATED) >= dt_FIRSTDAY_3MONTH_AGO AND DATE(p.dt_UPDATED) <= dt_LASTDAY_PREVIEW_MONTH AND t.lg_FAMILLE_ID = LGFAMILLEID; 
	
	SET int_QTE_JOUR = int_QTE_VENDUS / 84;
	SET intSEUILMINI = ceil(int_QTE_JOUR * int_JOUR_STOCK);
	SET intSEUILMAX = intSEUILMINI * int_JOUR_STOCK;
	SET intQUANTITEREAPPRO = ceil(int_QTE_JOUR * int_DELAI_REAPPRO);
	
	UPDATE t_famille t SET t.int_SEUIL_MIN = intSEUILMINI, t.int_SEUIL_MAX = intSEUILMAX, t.int_QTE_REAPPROVISIONNEMENT = intQUANTITEREAPPRO,
	t.dt_UPDATED = NOW(), t.dt_LAST_UPDATE_SEUILREAPPRO = NOW() WHERE t.lg_FAMILLE_ID = LGFAMILLEID;
	  		
	END LOOP bl_loop;
	CLOSE curbl;
	
	UPDATE t_parameters t set t.str_VALUE = CURRENT_DATE(), t.dt_UPDATED = NOW() WHERE t.str_KEY ='KEY_DAY_SEUIL_REAPPRO';
END IF;
END
    */
}
