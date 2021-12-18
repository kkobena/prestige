/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import dal.ReglementCarnet;
import dal.TTiersPayant;
import dal.TUser;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;
import util.DateConverter;

/**
 *
 * @author koben
 */
public class ReglementCarnetDTO {

    private Integer id;

    private String description;

    private Integer montantPaye;

    private Integer montantPayer;
    private LocalDateTime created;
    private Integer montantRestant;
    private String userId;
    private String user;
    private String tiersPayantId;
    private String tiersPayant;
    private String reference;
    private String createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMontantPaye() {
        return montantPaye;
    }

    public void setMontantPaye(Integer montantPaye) {
        this.montantPaye = montantPaye;
    }

    public Integer getMontantPayer() {
        return montantPayer;
    }

    public void setMontantPayer(Integer montantPayer) {
        this.montantPayer = montantPayer;
    }

    public Integer getMontantRestant() {
        return montantRestant;
    }

    public void setMontantRestant(Integer montantRestant) {
        this.montantRestant = montantRestant;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTiersPayantId() {
        return tiersPayantId;
    }

    public ReglementCarnetDTO setTiersPayantId(String tiersPayantId) {
        this.tiersPayantId = tiersPayantId;
        return this;
    }

    public String getTiersPayant() {
        return tiersPayant;
    }

    public void setTiersPayant(String tiersPayant) {
        this.tiersPayant = tiersPayant;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public ReglementCarnetDTO() {
    }

    public ReglementCarnetDTO(ReglementCarnet carnet) {
        this.id = carnet.getId();
        this.description = carnet.getDescription();
        this.montantPaye = carnet.getMontantPaye();
        this.montantPayer = carnet.getMontantPayer();
        this.montantRestant = carnet.getMontantRestant();
        TUser us = carnet.getUser();
        this.userId = us.getLgUSERID();
        this.user = us.getStrFIRSTNAME().concat(" ").concat(us.getStrLASTNAME());
        TTiersPayant payant = carnet.getTiersPayant();
        this.tiersPayantId = payant.getLgTIERSPAYANTID();
        this.tiersPayant = payant.getStrFULLNAME();
        this.createdAt = carnet.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        this.description = carnet.getDescription();
        this.reference = StringUtils.leftPad(carnet.getReference().toString(), 5, '0');
        this.created = carnet.getCreatedAt();
    }

    public ReglementCarnetDTO(Long montantPaye, Long montantPayer) {
        this.montantPaye = montantPaye.intValue();
        this.montantPayer = montantPayer.intValue();

    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

}
