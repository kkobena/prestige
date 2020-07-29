/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asc.business.caissier;

import dal.TUser;
import java.util.List;

/**
 *
 * @author JZAGO
 */
public interface CaissierService {
    List<TUser> getAllCaissiers();
}
