/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import commonTasks.dto.NotificationDTO;
import dal.Notification;
import dal.TClient;
import dal.TUser;
import dal.enumeration.Canal;
import dal.enumeration.Statut;
import dal.enumeration.TypeNotification;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Local;
import org.json.JSONObject;

/**
 *
 * @author koben
 */
@Local
public interface NotificationService {

    JSONObject findAll(int criteria, int canal, Statut statut, LocalDate dtStart, LocalDate dtEnd);

    List<NotificationDTO> findAllDto(int criteria, int canal, Statut statut, LocalDate dtStart, LocalDate dtEnd);

    void save(Notification notification) ;

   
}
