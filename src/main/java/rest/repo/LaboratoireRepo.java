/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.repo;

import dal.Laboratoire;
import dal.enumeration.Statut;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author koben
 */
@Stateless
public class LaboratoireRepo extends AbstractRepoImpl<Laboratoire> {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
  public LaboratoireRepo() {
     super(Laboratoire.class);
    }
  

    public Laboratoire saveOrUpdate(Laboratoire laboratoire) {
        try {

            if (StringUtils.isEmpty(laboratoire.getId())) {
                laboratoire.setId(UUID.randomUUID().toString());
                this.save(laboratoire);
                return laboratoire;
            }
            this.findById(laboratoire.getId()).ifPresent(x -> {
                laboratoire.setCreatedAt(x.getCreatedAt());
                laboratoire.setModifiedAt(LocalDateTime.now());
                laboratoire.setStatus(Statut.ENABLE);
                this.update(laboratoire);
            });
            return laboratoire;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }

    }
}
