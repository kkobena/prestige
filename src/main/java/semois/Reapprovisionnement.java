/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semois;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author kkoffi
 */
@Singleton
@Startup
public class Reapprovisionnement {

    @EJB
    private SemoisService semoisService;

    @PostConstruct
    public void init() {
        semoisService.execute();
    }

}
