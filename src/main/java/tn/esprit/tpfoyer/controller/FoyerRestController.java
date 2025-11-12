package tn.esprit.tpfoyer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer.entity.Foyer;
import tn.esprit.tpfoyer.service.FoyerService;

import java.util.List;
@Tag(name= "Gestion Foyer")
@RestController
@RequestMapping("/foyer")
public class FoyerRestController {

    private final FoyerService foyerService;

    // ✅ Constructeur avec @Autowired pour injection de dépendance
    @Autowired
    public FoyerRestController(FoyerService foyerService) {
        this.foyerService = foyerService;
    }
    @Operation(description ="récupérer toutes les foyer de la base de données")
    @GetMapping("/retrieve-all-foyers")
    public List<Foyer> getAllFoyers() {
        return foyerService.retrieveAllFoyers();
    }
    @Operation(description ="récupérer  un foyer par son id ")
    @GetMapping("/retrieve-foyer/{foyer-id}")
    public Foyer retrieveFoyer(@PathVariable("foyer-id") Long id) {
        return foyerService.retrieveFoyer(id);
    }
    @Operation(description ="ajouter un foyer  ")
    @PostMapping("/add-foyer")
    public Foyer addFoyer(@RequestBody Foyer foyer) {
        return foyerService.addFoyer(foyer);
    }
    @Operation(description ="supprimer un foyer  ")
    @DeleteMapping("/remove-foyer/{foyer-id}")
    public void removeFoyer(@PathVariable("foyer-id") Long id) {
        foyerService.removeFoyer(id);
    }
    @Operation(description ="mettre a jour un foyer")
    @PutMapping("/modify-foyer")
    public Foyer modifyFoyer(@RequestBody Foyer foyer) {
        return foyerService.modifyFoyer(foyer);
    }
}
