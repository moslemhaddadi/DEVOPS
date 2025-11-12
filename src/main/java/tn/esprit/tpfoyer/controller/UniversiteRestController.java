package tn.esprit.tpfoyer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer.entity.Universite;
import tn.esprit.tpfoyer.service.UniversiteService;

import java.util.List;
@Tag(name= "Gestion université")
@RestController
@RequestMapping("/universite")
public class UniversiteRestController {

    private final UniversiteService universiteService;

    // ✅ Constructeur avec @Autowired pour l'injection de dépendance
    @Autowired
    public UniversiteRestController(UniversiteService universiteService) {
        this.universiteService = universiteService;
    }
    @Operation(description ="récupérer toutes les université de la base de données")
    @GetMapping("/retrieve-all-universites")
    public List<Universite> getAllUniversites() {
        return universiteService.retrieveAllUniversites();
    }
    @Operation(description ="récupérer une  université par son id")
    @GetMapping("/retrieve-universite/{universite-id}")
    public Universite retrieveUniversite(@PathVariable("universite-id") Long id) {
        return universiteService.retrieveUniversite(id);
    }
    @Operation(description ="ajouter une université ")
    @PostMapping("/add-universite")
    public Universite addUniversite(@RequestBody Universite universite) {
        return universiteService.addUniversite(universite);
    }
    @Operation(description ="supprimer  une université ")
    @DeleteMapping("/remove-universite/{universite-id}")
    public void removeUniversite(@PathVariable("universite-id") Long id) {
        universiteService.removeUniversite(id);
    }
    @Operation(description ="modifier une université ")
    @PutMapping("/modify-universite")
    public Universite modifyUniversite(@RequestBody Universite universite) {
        return universiteService.modifyUniversite(universite);
    }
}
