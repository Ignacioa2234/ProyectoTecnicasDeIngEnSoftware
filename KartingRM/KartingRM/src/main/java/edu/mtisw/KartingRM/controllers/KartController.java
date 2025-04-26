package edu.mtisw.KartingRM.controllers;

import edu.mtisw.KartingRM.entities.KartEntity;
import edu.mtisw.KartingRM.services.KartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/karts")
@CrossOrigin("*")
public class KartController {

    @Autowired
    private KartService kartService;

    @PostMapping
    public ResponseEntity<KartEntity> createKart(@RequestBody KartEntity kart) {
        KartEntity createdKart = kartService.createKart(kart);
        return new ResponseEntity<>(createdKart, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<KartEntity>> getAllKarts() {
        List<KartEntity> karts = kartService.getAllKarts();
        return new ResponseEntity<>(karts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KartEntity> getKartById(@PathVariable Long id) {
        Optional<KartEntity> kartOpt = kartService.getKartById(id);
        if (kartOpt.isPresent()) {
            return new ResponseEntity<>(kartOpt.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<KartEntity> getKartByCode(@PathVariable String code) {
        Optional<KartEntity> kartOpt = kartService.getKartByCode(code);
        if (kartOpt.isPresent()) {
            return new ResponseEntity<>(kartOpt.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KartEntity> updateKart(@PathVariable Long id, @RequestBody KartEntity kartDetails) {
        Optional<KartEntity> kartOpt = kartService.getKartById(id);
        if (kartOpt.isPresent()) {
            KartEntity existingKart = kartOpt.get();
            existingKart.setCode(kartDetails.getCode());
            KartEntity updatedKart = kartService.updateKart(existingKart);
            return new ResponseEntity<>(updatedKart, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKart(@PathVariable Long id) {
        kartService.deleteKart(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
