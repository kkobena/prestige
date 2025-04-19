package rest.service.impl;

import dal.ProductState;
import dal.TFamille;
import dal.enumeration.ProductStateEnum;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.collections4.CollectionUtils;
import rest.service.ProductStateService;

/**
 *
 * @author koben
 */
@Stateless
public class ProductStateServiceImpl implements ProductStateService {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public void removeByProduitAndState(TFamille produit, ProductStateEnum state) {
        produit.getProductStates().stream().filter(produitState -> produitState.getProduitStateEnum() == state)
                .forEach(this::remove);
    }

    @Override
    public void remove(ProductState state) {
        em.remove(state);
    }

    @Override
    public void addState(TFamille produit, ProductStateEnum state) {
        ProductState productState = new ProductState();
        productState.setProduit(produit);
        productState.setProduitStateEnum(state);
        this.em.persist(productState);
    }

    @Override
    public List<ProductState> fetchByProduit(TFamille produit) {
        return produit.getProductStates();
    }

    @Override
    public List<ProductState> fetchByProduitAndState(TFamille produit, ProductStateEnum state) {
        List<ProductState> productStates = produit.getProductStates();
        if (CollectionUtils.isNotEmpty(productStates)) {
            return productStates.stream().sorted(Comparator.comparing(ProductState::getUpdated))
                    .filter(produitState -> produitState.getProduitStateEnum() == state).collect(Collectors.toList());
        }
        return List.of();

    }

    @Override
    public void manageProduitState(TFamille produit, ProductStateEnum precendentState, ProductStateEnum currentState) {
        if (precendentState != currentState) {
            List<ProductState> productStates = this.fetchByProduitAndState(produit, precendentState);
            if (CollectionUtils.isNotEmpty(productStates)) {
                if (productStates.size() == 1) {
                    productStates.forEach(this::remove);
                } else {
                    this.remove(productStates.get(0));
                }
            }
        }

        this.addState(produit, currentState);
    }

    @Override
    public void remove(TFamille produit, ProductStateEnum state) {
        List<ProductState> productStates = this.fetchByProduitAndState(produit, state);
        if (CollectionUtils.isNotEmpty(productStates)) {
            if (productStates.size() == 1) {
                productStates.forEach(this::remove);
            } else {
                this.remove(productStates.get(0));
            }
        }
    }
}
