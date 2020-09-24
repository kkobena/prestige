/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 *
 * @author koben
 */
public class RuptureFournisseurDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat heureFormat = new SimpleDateFormat("HH:mm");
    private String id, details = " ", libelleGrossiste, reference, grossisteId;
}
