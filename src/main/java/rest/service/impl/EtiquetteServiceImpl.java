
package rest.service.impl;

import dal.TBonLivraisonDetail;
import dal.TFamille;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import rest.service.EtiquetteService;
import rest.service.dto.EtiquetteDTO;
import util.DateConverter;

/**
 *
 * @author koben
 */
@Stateless
public class EtiquetteServiceImpl implements EtiquetteService{
 @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @Override
    public List<EtiquetteDTO> buildEtiquettes(String bonId, int startAt, String rasionSociale) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        List<EtiquetteDTO> etiquettes = new ArrayList<>();
        List<TBonLivraisonDetail>receiptItems=getItems( bonId);
        int index = 1;
        if (startAt > 1) {
            for (int i = 1; i <= startAt; i++) {
                etiquettes.add(EtiquetteDTO.builder()
                    .print(false)
                    .order(index)
                    .build());
                index++;
            }
            for (TBonLivraisonDetail item : receiptItems) {
                for (int i = 0; i < item.getIntQTERECUE(); i++) {
                    etiquettes.add(buildEtiquetteDTO(item, date, index, rasionSociale));
                    index++;
                }

            }
            etiquettes.sort(Comparator.comparing(EtiquetteDTO::getOrder));
            return etiquettes;
        }
        for (TBonLivraisonDetail item : receiptItems) {
            for (int i = 0; i < item.getIntQTERECUE(); i++) {
                etiquettes.add(buildEtiquetteDTO(item, date, index, rasionSociale));
                index++;
            }

        }

        etiquettes.stream().sorted(Comparator.comparing(EtiquetteDTO::getLibelle));
        return etiquettes;
    }
      private EtiquetteDTO buildEtiquetteDTO(TBonLivraisonDetail item, String date, int order,
        String rasionSociale) {
          TFamille famille=item.getLgFAMILLEID();
        return EtiquetteDTO.builder()
            .code(famille.getIntCIP())
            .prix(String.format("%s CFA", DateConverter.amountFormat(famille.getIntPRICE())))
            .print(true)
            .date(date)
            .order(order)
            .magasin(rasionSociale)
            .libelle(famille.getStrNAME())
            .build();
    }
    private List<TBonLivraisonDetail> getItems(String bonId){
        TypedQuery<TBonLivraisonDetail> q=em.createQuery("SELECT o FROM TBonLivraisonDetail o WHERE o.lgBONLIVRAISONID.lgBONLIVRAISONID=?1", TBonLivraisonDetail.class);
        q.setParameter(1, bonId);
        return q.getResultList();
                
    }
}
