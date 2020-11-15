/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.NotificationDTO;
import dal.Notification;
import dal.enumeration.Statut;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;
import rest.service.NotificationService;

/**
 *
 * @author koben
 */
@Stateless
public class NotificationImpl implements NotificationService {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public JSONObject findAll(int criteria, int canal, Statut statut, LocalDate dtStart, LocalDate dtEnd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<NotificationDTO> findAllDto(int criteria, int canal, Statut statut, LocalDate dtStart, LocalDate dtEnd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save(Notification notification) {
        getEntityManager().merge(notification);
    }

  

}
