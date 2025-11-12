package tn.esprit.tpfoyer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer.entity.Reservation;
import tn.esprit.tpfoyer.service.ReservationService;

import java.util.List;
@Tag(name= "Gestion Reservation")
@RestController
@RequestMapping("/reservation")
public class ReservationRestController {

    private final ReservationService reservationService;

    // ✅ Constructeur avec @Autowired pour l’injection de ReservationService
    @Autowired
    public ReservationRestController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    @Operation(description ="récupérer toutes les réservations de la base de données")
    @GetMapping("/retrieve-all-reservations")
    public List<Reservation> getAllReservations() {
        return reservationService.retrieveAllReservations();
    }

    @Operation(description ="récupérer une réservation par son id")
    @GetMapping("/retrieve-reservation/{reservation-id}")
    public Reservation retrieveReservation(@PathVariable("reservation-id") String id) {
        return reservationService.retrieveReservation(id);
    }
    @Operation(description ="ajouter une réservation à la base de données")
    @PostMapping("/add-reservation")
    public Reservation addReservation(@RequestBody Reservation reservation) {
        return reservationService.addReservation(reservation);
    }
    @Operation(description ="supprimer une réservation de la base de données")
    @DeleteMapping("/remove-reservation/{reservation-id}")
    public void removeReservation(@PathVariable("reservation-id") String id) {
        reservationService.removeReservation(id);
    }
    @Operation(description ="modifier une réservation de la base de données")
    @PutMapping("/modify-reservation")
    public Reservation modifyReservation(@RequestBody Reservation reservation) {
        return reservationService.modifyReservation(reservation);
    }
}
