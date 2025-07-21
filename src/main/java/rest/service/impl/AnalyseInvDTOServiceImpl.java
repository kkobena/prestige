/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import rest.service.AnalyseInvDTOService;
import rest.service.dto.AnalyseInvDTO;

/**
 *
 * @author airman
 */
@Stateless
public class AnalyseInvDTOServiceImpl implements AnalyseInvDTOService {

    private static final Logger LOG = Logger.getLogger(AnalyseInvDTOServiceImpl.class.getName());

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public List<AnalyseInvDTO> listAnalyseInv(String inventaireId) {
        try {
            // La requête SQL native avec des alias clairs pour chaque colonne.
            // Ces alias seront utilisés pour récupérer les valeurs du Tuple.
            String queryString = "SELECT f.int_CIP AS codeCip, f.str_NAME AS nom, f.int_PAF AS prixAchat, f.int_PRICE AS prixVente, z.str_LIBELLEE AS emplacement, i.lg_INVENTAIRE_ID AS inventaireId, iv.str_NAME AS invName, i.int_NUMBER AS qteSaisie, i.int_NUMBER_INIT AS qteInitiale "
                    + "FROM t_inventaire_famille i, t_famille f, t_zone_geographique z, t_inventaire iv "
                    + "WHERE i.lg_FAMILLE_ID=f.lg_FAMILLE_ID AND f.lg_ZONE_GEO_ID = z.lg_ZONE_GEO_ID AND i.lg_INVENTAIRE_ID=iv.lg_INVENTAIRE_ID "
                    + "AND i.lg_INVENTAIRE_ID = :inventaireId " + "ORDER BY z.str_LIBELLEE, f.str_NAME ASC";

            Query query = em.createNativeQuery(queryString, Tuple.class);
            query.setParameter("inventaireId", inventaireId);

            // 1. Exécuter la requête pour obtenir une liste de Tuples
            @SuppressWarnings("unchecked")
            List<Tuple> tuples = query.getResultList();

            // 2. Utiliser un stream pour mapper chaque Tuple à un DTO via la méthode build
            return tuples.stream().map(this::build).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.log(Level.SEVERE,
                    "Erreur lors de la récupération de l'analyse d'inventaire pour l'ID : " + inventaireId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Méthode privée pour construire un objet AnalyseInvDTO à partir d'un Tuple. Centralise la logique de mapping et
     * améliore la lisibilité.
     *
     * @param t
     *            Le Tuple retourné par la requête JPA.
     *
     * @return Un objet AnalyseInvDTO entièrement construit.
     */
    private AnalyseInvDTO build(Tuple t) {
        return new AnalyseInvDTO.Builder().codeCip(t.get("codeCip", String.class)).nom(t.get("nom", String.class))
                .prixAchat(t.get("prixAchat", Number.class)).prixVente(t.get("prixVente", Number.class))
                .emplacement(t.get("emplacement", String.class)).inventaireId(String.valueOf(t.get("inventaireId"))) // Conversion
                                                                                                                     // sûre
                .invName(t.get("invName", String.class)).qteSaisie(t.get("qteSaisie", Number.class))
                .qteInitiale(t.get("qteInitiale", Number.class)).build();
    }
}
