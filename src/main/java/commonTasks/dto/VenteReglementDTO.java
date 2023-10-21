package commonTasks.dto;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class VenteReglementDTO {

    private @NotNull String typeReglement;
    private int montant;
    private int montantAttentu;

    public String getTypeReglement() {
        return typeReglement;
    }

    public VenteReglementDTO setTypeReglement(String typeReglement) {
        this.typeReglement = typeReglement;
        return this;
    }

    public int getMontant() {
        return montant;
    }

    public VenteReglementDTO setMontant(int montant) {
        this.montant = montant;
        return this;
    }

    public int getMontantAttentu() {
        return Objects.nonNull(montantAttentu) ? montantAttentu : montant;

    }

    public void setMontantAttentu(int montantAttentu) {
        this.montantAttentu = montantAttentu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VenteReglementDTO that = (VenteReglementDTO) o;
        return Objects.equals(typeReglement, that.typeReglement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeReglement);
    }
}
