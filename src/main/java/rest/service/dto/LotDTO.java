/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest.service.dto;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Hermann N'ZI
 */
// public class LotDTO implements Serializable{

@Getter
@Setter
@Builder
public class LotDTO implements Serializable {

    private Integer lg_FAMILLE_ID;

    private String str_NAME;

    private Integer int_CIP;

    private Integer int_PRICE;

    private Integer int_PAF;

    private String int_NUM_LOT;

    private Integer int_NUMBER;

    private String dt_CREATED;

    private String dt_UPDATED;

    private String lg_GROSSISTE_ID;

    private String str_REF_LIVRAISON;

    private String dt_SORTIE_USINE;

    private String dt_PEREMPTION;

    private Integer int_NUMBER_GRATUIT;

    private Integer int_QTY_VENDUE;

    private String lg_USER_ID;

    public Integer getLg_FAMILLE_ID() {
        return lg_FAMILLE_ID;
    }

    public void setLg_FAMILLE_ID(Integer lg_FAMILLE_ID) {
        this.lg_FAMILLE_ID = lg_FAMILLE_ID;
    }

    public String getStr_NAME() {
        return str_NAME;
    }

    public void setStr_NAME(String str_NAME) {
        this.str_NAME = str_NAME;
    }

    public Integer getInt_CIP() {
        return int_CIP;
    }

    public void setInt_CIP(Integer int_CIP) {
        this.int_CIP = int_CIP;
    }

    public Integer getInt_PRICE() {
        return int_PRICE;
    }

    public void setInt_PRICE(Integer int_PRICE) {
        this.int_PRICE = int_PRICE;
    }

    public Integer getInt_PAF() {
        return int_PAF;
    }

    public void setInt_PAF(Integer int_PAF) {
        this.int_PAF = int_PAF;
    }

    public String getInt_NUM_LOT() {
        return int_NUM_LOT;
    }

    public void setInt_NUM_LOT(String int_NUM_LOT) {
        this.int_NUM_LOT = int_NUM_LOT;
    }

    public Integer getInt_NUMBER() {
        return int_NUMBER;
    }

    public void setInt_NUMBER(Integer int_NUMBER) {
        this.int_NUMBER = int_NUMBER;
    }

    public String getDt_CREATED() {
        return dt_CREATED;
    }

    public void setDt_CREATED(String dt_CREATED) {
        this.dt_CREATED = dt_CREATED;
    }

    public String getDt_UPDATED() {
        return dt_UPDATED;
    }

    public void setDt_UPDATED(String dt_UPDATED) {
        this.dt_UPDATED = dt_UPDATED;
    }

    public String getLg_GROSSISTE_ID() {
        return lg_GROSSISTE_ID;
    }

    public void setLg_GROSSISTE_ID(String lg_GROSSISTE_ID) {
        this.lg_GROSSISTE_ID = lg_GROSSISTE_ID;
    }

    public String getStr_REF_LIVRAISON() {
        return str_REF_LIVRAISON;
    }

    public void setStr_REF_LIVRAISON(String str_REF_LIVRAISON) {
        this.str_REF_LIVRAISON = str_REF_LIVRAISON;
    }

    public String getDt_SORTIE_USINE() {
        return dt_SORTIE_USINE;
    }

    public void setDt_SORTIE_USINE(String dt_SORTIE_USINE) {
        this.dt_SORTIE_USINE = dt_SORTIE_USINE;
    }

    public String getDt_PEREMPTION() {
        return dt_PEREMPTION;
    }

    public void setDt_PEREMPTION(String dt_PEREMPTION) {
        this.dt_PEREMPTION = dt_PEREMPTION;
    }

    public Integer getInt_NUMBER_GRATUIT() {
        return int_NUMBER_GRATUIT;
    }

    public void setInt_NUMBER_GRATUIT(Integer int_NUMBER_GRATUIT) {
        this.int_NUMBER_GRATUIT = int_NUMBER_GRATUIT;
    }

    public Integer getInt_QTY_VENDUE() {
        return int_QTY_VENDUE;
    }

    public void setInt_QTY_VENDUE(Integer int_QTY_VENDUE) {
        this.int_QTY_VENDUE = int_QTY_VENDUE;
    }

    public String getLg_USER_ID() {
        return lg_USER_ID;
    }

    public void setLg_USER_ID(String lg_USER_ID) {
        this.lg_USER_ID = lg_USER_ID;
    }

}
