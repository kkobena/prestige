/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest.service;

import java.util.List;
import rest.service.dto.AnalyseInvDTO;

/**
 *
 * @author airman
 */
public interface AnalyseInvDTOService {
    /**
     * Récupère la liste des données d'analyse d'inventaire pour un ID d'inventaire donné. * @param inventaireId
     * L'identifiant de l'inventaire à analyser.
     *
     * @param inventaireId
     *
     * @return Une liste d'objets AnalyseInvDTO.
     */
    List<AnalyseInvDTO> listAnalyseInv(String inventaireId);

    // Vous pouvez ajouter d'autres méthodes ici, par exemple pour la pagination ou des filtres plus complexes
    // List<AnalyseInvDTO> listAnalyseInv(String inventaireId, int start, int limit);
}
