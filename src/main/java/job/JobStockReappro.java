/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package job;

import dal.dataManager;
import javax.persistence.StoredProcedureQuery;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author KKOFFI
 */
public class JobStockReappro implements Job{
    
    dataManager OdaManager=null;
    public JobStockReappro() {
         OdaManager=new dataManager();
        OdaManager.initEntityManager();
    }
    @Override 
 public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
          StoredProcedureQuery q = OdaManager.getEm().createStoredProcedureQuery("UpdateStockReapro");
            q.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
  
       
      } 
}
