
package rest.service.dto.analyse;

import java.util.List;

/**
 *
 * @author airman
 */
public class AnalyseAvanceeDTO {
    private boolean success = true;
    private List<SyntheseEmplacementDTO> synthese;
    private List<AnalyseAbcDTO> analyseABC;
    private List<DetailProduitDTO> detailProduits;
    // Getters et Setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<SyntheseEmplacementDTO> getSynthese() {
        return synthese;
    }

    public void setSynthese(List<SyntheseEmplacementDTO> synthese) {
        this.synthese = synthese;
    }

    public List<AnalyseAbcDTO> getAnalyseABC() {
        return analyseABC;
    }

    public void setAnalyseABC(List<AnalyseAbcDTO> analyseABC) {
        this.analyseABC = analyseABC;
    }

    public List<DetailProduitDTO> getDetailProduits() {
        return detailProduits;
    }

    public void setDetailProduits(List<DetailProduitDTO> detailProduits) {
        this.detailProduits = detailProduits;
    }

}
