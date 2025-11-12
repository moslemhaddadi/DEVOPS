package tn.esprit.tpfoyer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer.entity.Etudiant;
import tn.esprit.tpfoyer.service.EtudiantService;

import java.util.List;
@Tag(name= "Gestion Etudiant")
@RestController
@RequestMapping("/etudiant")
public class EtudiantRestController {

    private final EtudiantService etudiantService;

    // ✅ Constructeur avec @Autowired pour injection de dépendance
    @Autowired
    public EtudiantRestController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }
    @Operation(description ="récupérer toutes les etudiant de la base de données")
    @GetMapping("/retrieve-all-etudiants")
    public List<Etudiant> getAllEtudiants() {
        return etudiantService.retrieveAllEtudiants();
    }

    @Operation(description ="récupérer un etudiant par son id")
    @GetMapping("/retrieve-etudiant/{etudiant-id}")
    public Etudiant retrieveEtudiant(@PathVariable("etudiant-id") Long id) {
        return etudiantService.retrieveEtudiant(id);
    }
    @Operation(description ="ajouter un etudiant ")
    @PostMapping("/add-etudiant")
    public Etudiant addEtudiant(@RequestBody Etudiant etudiant) {
        return etudiantService.addEtudiant(etudiant);
    }
    @Operation(description ="supprimer un etudiant de la base de données")
    @DeleteMapping("/remove-etudiant/{etudiant-id}")
    public void removeEtudiant(@PathVariable("etudiant-id") Long id) {
        etudiantService.removeEtudiant(id);
    }
    @Operation(description ="mettre a jour un etudiant ")
    @PutMapping("/modify-etudiant")
    public Etudiant modifyEtudiant(@RequestBody Etudiant etudiant) {
        return etudiantService.modifyEtudiant(etudiant);
    }
}
