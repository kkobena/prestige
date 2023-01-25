/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

import dal.TUser;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import toolkits.parameters.commonparameter;

/**
 *
 * @author koben
 */
public class Utils {
private final HttpServletRequest servletRequest;

    public Utils(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }
    public boolean hasConnectedUser(){
         HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        return Objects.nonNull(tu);
    }
}
