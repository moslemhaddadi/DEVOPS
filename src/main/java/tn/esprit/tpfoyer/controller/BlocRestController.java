package tn.esprit.tpfoyer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer.entity.Bloc;
import tn.esprit.tpfoyer.service.BlocService;

import java.util.List;
@Tag(name= "Gestion bloc")
@RestController
@RequestMapping("/bloc")
public class BlocRestController {

    private final BlocService blocService;

    // ✅ Constructeur avec @Autowired pour l’injection de BlocService
    @Autowired
    public BlocRestController(BlocService blocService) {
        this.blocService = blocService;
    }
    @Operation(description ="récupérer toutes les bloc de la base de données")
    @GetMapping("/retrieve-all-blocs")
    public List<Bloc> getAllBlocs() {
        return blocService.retrieveAllBlocs();
    }
    @Operation(description ="récupérer un bloc par son id ")
    @GetMapping("/retrieve-bloc/{bloc-id}")
    public Bloc retrieveBloc(@PathVariable("bloc-id") Long id) {
        return blocService.retrieveBloc(id);
    }
    @Operation(description ="ajouter un bloc  ")
    @PostMapping("/add-bloc")
    public Bloc addBloc(@RequestBody Bloc bloc) {
        return blocService.addBloc(bloc);
    }
    @Operation(description ="supprimer un bloc  ")
    @DeleteMapping("/remove-bloc/{bloc-id}")
    public void removeBloc(@PathVariable("bloc-id") Long id) {
        blocService.removeBloc(id);
    }
    @Operation(description ="mettre a jour un bloc  ")
    @PutMapping("/modify-bloc")
    public Bloc modifyBloc(@RequestBody Bloc bloc) {
        return blocService.modifyBloc(bloc);
    }
}
