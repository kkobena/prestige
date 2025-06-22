package rest.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import rest.service.InfoArticleService;
import rest.service.dto.InfoArticleDTO;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.Tuple;

@Stateless
public class InfoArticleServiceImpl implements InfoArticleService {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public List<InfoArticleDTO> getInfoArticles(int start, int limit, String searchTerm) {
        String queryStr = getQuery(searchTerm);
        Query query = em.createNativeQuery(queryStr, Tuple.class);

        // Calcul de la date de début (6 mois avant le mois en cours)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -6);
        Date dateDebut = cal.getTime();

        // Toujours définir le premier paramètre (date de début)
        query.setParameter(1, dateDebut);

        // Définir les paramètres de recherche seulement si searchTerm n'est pas vide
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            query.setParameter(2, "%" + searchTerm.toLowerCase() + "%");
            query.setParameter(3, "%" + searchTerm.toLowerCase() + "%");
        }

        // Pagination
        query.setFirstResult(start);
        query.setMaxResults(limit);

        List<Tuple> results = query.getResultList();
        List<InfoArticleDTO> dtos = new ArrayList<>();

        for (Tuple tuple : results) {
            dtos.add(mapTupleToDto(tuple));
        }

        return dtos;
    }

    @Override
    public Long countInfoArticles(String searchTerm) {
        String queryStr = getCountQuery(searchTerm);
        Query query = em.createNativeQuery(queryStr);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -6);
        Date dateDebut = cal.getTime();

        query.setParameter(1, dateDebut);

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            query.setParameter(2, "%" + searchTerm.toLowerCase() + "%");
            query.setParameter(3, "%" + searchTerm.toLowerCase() + "%");
        }

        return ((Number) query.getSingleResult()).longValue();
    }

    private String getQuery(String searchTerm) {
        StringBuilder baseQuery = new StringBuilder("SELECT ").append("g.str_LIBELLE AS grossiste, ")
                .append("z.str_LIBELLEE AS emplacement, ").append("p.lg_FAMILLE_ID AS produitId, ")
                .append("p.int_CIP AS codeCip, ").append("p.str_NAME AS libelle, ").append("p.int_PRICE AS prixVente, ")
                .append("p.int_PAF AS prixAchat, ").append("f.int_NUMBER AS stock, ")
                .append("SUM(CASE WHEN venteDetail.dateVente <> MONTH(CURDATE()) ")
                .append("THEN venteDetail.quantiteVendue ELSE 0 END) AS quantiteVendue, ")
                .append("ROUND(SUM(CASE WHEN venteDetail.dateVente <> MONTH(CURDATE()) ")
                .append("THEN venteDetail.quantiteVendue ELSE 0 END)/3, 2) AS moyenne, ")
                .append("GROUP_CONCAT(venteDetail.quantiteVendue, ':', venteDetail.dateVente) AS quantite_mois ")
                .append("FROM ").append("t_famille p ").append("JOIN ")
                .append("t_zone_geographique z ON p.lg_ZONE_GEO_ID = z.lg_ZONE_GEO_ID ").append("JOIN ")
                .append("t_grossiste g ON p.lg_GROSSISTE_ID = g.lg_GROSSISTE_ID ").append("JOIN ")
                .append("t_famille_stock f ON f.lg_FAMILLE_ID = p.lg_FAMILLE_ID ").append("JOIN ").append("( ")
                .append("SELECT ").append("d.lg_FAMILLE_ID AS produitId, ")
                .append("SUM(d.int_QUANTITY) AS quantiteVendue, ").append("MONTH(v.dt_UPDATED) AS dateVente ")
                .append("FROM ").append("t_preenregistrement_detail d ").append("JOIN ")
                .append("t_preenregistrement v ").append("ON d.lg_PREENREGISTREMENT_ID = v.lg_PREENREGISTREMENT_ID ")
                .append("WHERE ").append("v.b_IS_CANCEL = 0 ").append("AND v.str_STATUT = 'is_Closed' ")
                .append("AND v.int_PRICE > 0 ").append("AND DATE(v.dt_UPDATED) BETWEEN ?1 AND CURDATE() ")
                .append("GROUP BY ").append("d.lg_FAMILLE_ID, MONTH(v.dt_UPDATED) ").append(") AS venteDetail ")
                .append("ON p.lg_FAMILLE_ID = venteDetail.produitId ").append("WHERE ")
                .append("p.str_STATUT = 'enable' ");

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            baseQuery.append("AND (LOWER(p.int_CIP) LIKE ?2 OR LOWER(p.str_NAME) LIKE ?3) ");
        }

        baseQuery.append("GROUP BY ").append("p.lg_FAMILLE_ID ").append("ORDER BY ").append("p.str_NAME");

        return baseQuery.toString();
    }

    private String getCountQuery(String searchTerm) {
        StringBuilder baseQuery = new StringBuilder("SELECT COUNT(DISTINCT p.lg_FAMILLE_ID) ").append("FROM ")
                .append("t_famille p ").append("JOIN ")
                .append("t_zone_geographique z ON p.lg_ZONE_GEO_ID = z.lg_ZONE_GEO_ID ").append("JOIN ")
                .append("t_grossiste g ON p.lg_GROSSISTE_ID = g.lg_GROSSISTE_ID ").append("JOIN ")
                .append("t_famille_stock f ON f.lg_FAMILLE_ID = p.lg_FAMILLE_ID ").append("JOIN ").append("( ")
                .append("SELECT ").append("d.lg_FAMILLE_ID AS produitId ").append("FROM ")
                .append("t_preenregistrement_detail d ").append("JOIN ").append("t_preenregistrement v ")
                .append("ON d.lg_PREENREGISTREMENT_ID = v.lg_PREENREGISTREMENT_ID ").append("WHERE ")
                .append("v.b_IS_CANCEL = 0 ").append("AND v.str_STATUT = 'is_Closed' ").append("AND v.int_PRICE > 0 ")
                .append("AND DATE(v.dt_UPDATED) BETWEEN ?1 AND CURDATE() ").append("GROUP BY ")
                .append("d.lg_FAMILLE_ID, MONTH(v.dt_UPDATED) ").append(") AS venteDetail ")
                .append("ON p.lg_FAMILLE_ID = venteDetail.produitId ").append("WHERE ")
                .append("p.str_STATUT = 'enable' ");

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            baseQuery.append("AND (LOWER(p.int_CIP) LIKE ?2 OR LOWER(p.str_NAME) LIKE ?3) ");
        }

        return baseQuery.toString();
    }

    private InfoArticleDTO mapTupleToDto(Tuple tuple) {
        InfoArticleDTO dto = new InfoArticleDTO();
        dto.setGrossiste(getString(tuple, "grossiste"));
        dto.setEmplacement(getString(tuple, "emplacement"));
        dto.setProduitId(getString(tuple, "produitId"));
        dto.setCodeCip(getString(tuple, "codeCip"));
        dto.setLibelle(getString(tuple, "libelle"));
        dto.setPrixVente(getInteger(tuple, "prixVente"));
        dto.setPrixAchat(getInteger(tuple, "prixAchat"));
        dto.setStock(getInteger(tuple, "stock"));
        dto.setQuantiteVendue(getInteger(tuple, "quantiteVendue"));
        dto.setMoyenne(getDouble(tuple, "moyenne"));
        dto.setQuantiteMois(getString(tuple, "quantite_mois"));
        return dto;
    }

    private String getString(Tuple tuple, String key) {
        Object value = tuple.get(key);
        return value != null ? value.toString() : null;
    }

    private Integer getInteger(Tuple tuple, String key) {
        Object value = tuple.get(key);
        if (value == null)
            return null;
        if (value instanceof BigDecimal)
            return ((BigDecimal) value).intValue();
        if (value instanceof Number)
            return ((Number) value).intValue();
        return Integer.parseInt(value.toString());
    }

    private Double getDouble(Tuple tuple, String key) {
        Object value = tuple.get(key);
        if (value == null)
            return null;
        if (value instanceof BigDecimal)
            return ((BigDecimal) value).doubleValue();
        if (value instanceof Number)
            return ((Number) value).doubleValue();
        return Double.parseDouble(value.toString());
    }

}