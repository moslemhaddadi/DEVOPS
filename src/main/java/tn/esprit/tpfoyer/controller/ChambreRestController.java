package tn.esprit.tpfoyer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer.entity.Chambre;
import tn.esprit.tpfoyer.service.ChambreService;

import java.util.List;
@Tag(name= "Gestion Chambre")
@RestController
@RequestMapping("/chambre")  // Chemin de base pour toutes les requêtes liées aux chambres
public class ChambreRestController {

    private final ChambreService chambreService;

    // ✅ Constructeur avec @Autowired pour l’injection de ChambreService
    @Autowired
    public ChambreRestController(ChambreService chambreService) {
        this.chambreService = chambreService;
    }



    // 🔍 GET : Récupérer toutes les chambres

    @Operation(description = "récupérer toutes les chambres de la base de données")

    @GetMapping("/retrieve-all-chambres")
    public List<Chambre> getChambres() {
        return chambreService.retrieveAllChambres();
    }

    // 🔎 GET : Récupérer une chambre par ID

    @Operation(description = "récupérer une chambres par son id")
    @GetMapping("/retrieve-chambre/{chambre-id}")
    public Chambre retrieveChambre(@PathVariable("chambre-id") Long chId) {
        return chambreService.retrieveChambre(chId);
    }

    // ➕ POST : Ajouter une chambre
    @Operation(description = "ajouter une chambre à la base de données")
    @PostMapping("/add-chambre")
    public Chambre addChambre(@RequestBody Chambre c) {
        return chambreService.addChambre(c);
    }

    // 🗑️ DELETE : Supprimer une chambre par ID
    @Operation(description = "supprimer une chambre de la base de données")
    @DeleteMapping("/remove-chambre/{chambre-id}")
    public void removeChambre(@PathVariable("chambre-id") Long chId) {
        chambreService.removeChambre(chId);
    }

    // 📝 PUT : Modifier une chambre
    @Operation(description = "modifier une chambre de la base de données")
    @PutMapping("/modify-chambre")
    public Chambre modifyChambre(@RequestBody Chambre c) {
        return chambreService.modifyChambre(c);
    }
}
