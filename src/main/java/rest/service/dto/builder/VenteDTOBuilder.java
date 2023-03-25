package rest.service.dto.builder;

import commonTasks.dto.AyantDroitDTO;
import commonTasks.dto.ClientDTO;
import commonTasks.dto.FamilleDTO;
import commonTasks.dto.TiersPayantDTO;
import commonTasks.dto.UserDTO;
import dal.MvtTransaction;
import dal.TAyantDroit;
import dal.TClient;
import dal.TFamille;
import dal.TPreenregistrement;
import dal.TPreenregistrementCompteClient;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TPreenregistrementDetail;
import dal.TTiersPayant;
import dal.TTypeReglement;
import dal.TTypeVente;
import dal.TUser;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import rest.service.dto.CodeInfo;
import rest.service.dto.PreenregistrementCompteClientDTO;
import rest.service.dto.TransactionDTO;
import rest.service.dto.VenteDTO;
import rest.service.dto.VenteItemDTO;
import rest.service.dto.VenteTiersPayantItemDTO;
import util.DateUtil;

/**
 *
 * @author koben
 */
public final class VenteDTOBuilder {

    public static VenteDTO buildVenteDTO(TPreenregistrement p, MvtTransaction mt) {
        return VenteDTO.builder()
                .user(VenteDTOBuilder.user(p.getLgUSERID()))
                .vendeur(VenteDTOBuilder.user(p.getLgUSERVENDEURID()))
                .caissier(VenteDTOBuilder.user(p.getLgUSERCAISSIERID()))
                .cmuAmount(p.getCmuAmount())
                .client(VenteDTOBuilder.client(p.getClient()))
                .typeVente(VenteDTOBuilder.typeVente(p.getLgTYPEVENTEID()))
                .strREFTICKET(p.getStrREFTICKET())
                .strREF(p.getStrREF())
                .strREFBON(p.getStrREFBON())
                .intCUSTPART(p.getIntCUSTPART())
                .intPRICE(p.getIntPRICE())
                .intPRICEREMISE(p.getIntPRICEREMISE())
                .dtUPDATED(DateUtil.convertDateToDD_MM_YYYY_HH_mm(p.getDtUPDATED()))
                .items(VenteDTOBuilder.items(p.getTPreenregistrementDetailCollection()))
                .assurances(VenteDTOBuilder.assurances(p.getTPreenregistrementCompteClientTiersPayentCollection()))
                .differes(VenteDTOBuilder.differes(p.getTPreenregistrementCompteClientCollection()))
                .reglement(VenteDTOBuilder.buildReglementDTO(mt))
                .ayantDroit(VenteDTOBuilder.ayantDroit(p.getAyantDroit()))
                .strTYPEVENTE(p.getStrTYPEVENTE())
                .build();
    }

    public static List<PreenregistrementCompteClientDTO> differes(Collection<TPreenregistrementCompteClient> pts) {
        if (CollectionUtils.isEmpty(pts)) {
            return List.of();
        }
        return pts.stream().map(VenteDTOBuilder::buildCompteClientDTO).collect(Collectors.toList());
    }

    public static List<VenteItemDTO> items(Collection<TPreenregistrementDetail> pts) {
        if (CollectionUtils.isEmpty(pts)) {
            return List.of();
        }
        return pts.stream().map(VenteDTOBuilder::buildVenteItemDTO).collect(Collectors.toList());
    }

    public static List<VenteTiersPayantItemDTO> assurances(Collection<TPreenregistrementCompteClientTiersPayent> pts) {
        if (CollectionUtils.isEmpty(pts)) {
            return List.of();
        }
        return pts.stream().map(VenteDTOBuilder::buildVenteTiersPayantDTO).collect(Collectors.toList());
    }

    public static VenteItemDTO buildVenteItemDTO(TPreenregistrementDetail detail) {
        return VenteItemDTO.builder().
                cmuPrice(detail.getCmuPrice())
                .id(detail.getLgPREENREGISTREMENTDETAILID())
                .intAVOIRSERVED(detail.getIntAVOIRSERVED())
                .intPRICE(detail.getIntPRICE())
                .intPRICEUNITAIR(detail.getIntPRICEUNITAIR())
                .intQUANTITY(detail.getIntQUANTITY())
                .intQUANTITYSERVED(detail.getIntQUANTITYSERVED())
                .produit(VenteDTOBuilder.produit(detail.getLgFAMILLEID()))
                .build();
    }

    public static VenteTiersPayantItemDTO buildVenteTiersPayantDTO(TPreenregistrementCompteClientTiersPayent payent) {
        return VenteTiersPayantItemDTO.builder()
                .id(payent.getLgPREENREGISTREMENTCOMPTECLIENTPAYENTID())
                .intPRICE(payent.getIntPRICE())
                .intPERCENT(payent.getIntPERCENT())
                .intPRICERESTE(payent.getIntPRICERESTE())
                .strREFBON(payent.getStrREFBON())
                .strSTATUTFACTURE(payent.getStrSTATUTFACTURE())
                .tiersPayant(tiersPayant(payent.getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID()))
                .build();
    }

    public static PreenregistrementCompteClientDTO buildCompteClientDTO(TPreenregistrementCompteClient client) {
        return PreenregistrementCompteClientDTO.builder()
                .dtUPDATED(DateUtil.convertDateToDD_MM_YYYY_HH_mm(client.getDtCREATED()))
                .id(client.getLgPREENREGISTREMENTCOMPTECLIENTID())
                .intPRICE(client.getIntPRICE())
                .intPRICERESTE(client.getIntPRICERESTE())
                .build();
    }

    public static TransactionDTO buildReglementDTO(MvtTransaction mvtTransaction) {
        if (Objects.nonNull(mvtTransaction)) {
            return TransactionDTO.builder()
                    .checked(mvtTransaction.getChecked())
                    .cmuAmount(mvtTransaction.getCmuAmount())
                    .flaged(mvtTransaction.getFlaged())
                    .marge(mvtTransaction.getMarge())
                    .margeug(mvtTransaction.getMargeug())
                    .montant(mvtTransaction.getMontant())
                    .montantCredit(mvtTransaction.getMontantCredit())
                    .montantNet(mvtTransaction.getMontantNet())
                    .montantPaye(mvtTransaction.getMontantPaye())
                    .montantRegle(mvtTransaction.getMontantRegle())
                    .montantRemise(mvtTransaction.getMontantRemise())
                    .montantRestant(mvtTransaction.getMontantRestant())
                    .montantTva(mvtTransaction.getMontantTva())
                    .montantTvaUg(mvtTransaction.getMontantTvaUg())
                    .montantVerse(mvtTransaction.getMontantVerse())
                    .reglement(reglement(mvtTransaction.getReglement()))
                    .build();
        }
        return null;
    }

    public static CodeInfo reglement(TTypeReglement reglement) {
        if (Objects.nonNull(reglement)) {
            return CodeInfo.builder()
                    .code(reglement.getLgTYPEREGLEMENTID())
                    .libelle(reglement.getStrNAME())
                    .build();
        }
        return null;
    }

    private static FamilleDTO produit(TFamille f) {
        FamilleDTO o = new FamilleDTO();
        o.setStrNAME(f.getStrNAME());
        o.setIntCIP(f.getIntCIP());
        o.setIntEAN13(f.getIntEAN13());
        return o;
    }

    private static TiersPayantDTO tiersPayant(TTiersPayant payant) {
        TiersPayantDTO o = new TiersPayantDTO();
        o.setStrFULLNAME(payant.getStrFULLNAME());
        o.setStrNAME(payant.getStrNAME());
        return o;
    }

    private static UserDTO user(TUser user) {
        if (Objects.nonNull(user)) {
            UserDTO us = new UserDTO();
            us.setStrFIRSTNAME(user.getStrFIRSTNAME());
            us.setStrLASTNAME(user.getStrLASTNAME());
            return us;
        }
        return null;

    }

    private static ClientDTO client(TClient client) {
        if (Objects.nonNull(client)) {
            return new ClientDTO(client);
        }
        return null;

    }

    public static CodeInfo typeVente(TTypeVente typeVente) {
        if (Objects.nonNull(typeVente)) {
            return CodeInfo.builder()
                    .code(typeVente.getLgTYPEVENTEID())
                    .libelle(typeVente.getStrDESCRIPTION())
                    .build();
        }
        return null;
    }

    private static AyantDroitDTO ayantDroit(TAyantDroit ayantDroit) {
        if (Objects.nonNull(ayantDroit)) {

            return new AyantDroitDTO(ayantDroit);
        }
        return null;

    }
}
