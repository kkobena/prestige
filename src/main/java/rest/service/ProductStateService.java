package rest.service;

import dal.ProductState;
import dal.TFamille;
import dal.enumeration.ProductStateEnum;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author koben
 */
@Local
public interface ProductStateService {

    void removeByProduitAndState(TFamille produit, ProductStateEnum state);

    void remove(ProductState state);

    void addState(TFamille produit, ProductStateEnum state);

    List<ProductState> fetchByProduit(TFamille produit);

    List<ProductState> fetchByProduitAndState(TFamille produit, ProductStateEnum state);

    void manageProduitState(TFamille produit, ProductStateEnum precendentState, ProductStateEnum currentState);

    void remove(TFamille produit, ProductStateEnum state);
}
