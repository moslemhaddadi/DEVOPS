package tn.esprit.tpfoyer.service;

import tn.esprit.tpfoyer.entity.Chambre;

import java.util.List;

public interface ChambreService {
    List<Chambre> retrieveAllChambres();  // Récupérer toutes les chambres
    Chambre retrieveChambre(Long chambreId);  // Récupérer une chambre par ID
    Chambre addChambre(Chambre chambre);  // Ajouter une chambre
    void removeChambre(Long chambreId);  // Supprimer une chambre
    Chambre modifyChambre(Chambre chambre);  // Modifier une chambre
}