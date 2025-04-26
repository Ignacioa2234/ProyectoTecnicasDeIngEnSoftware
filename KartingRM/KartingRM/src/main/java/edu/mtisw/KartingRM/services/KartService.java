package edu.mtisw.KartingRM.services;

import edu.mtisw.KartingRM.entities.KartEntity;
import edu.mtisw.KartingRM.repositories.KartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class KartService {

    @Autowired
    private KartRepository kartRepository;

    public KartEntity createKart(KartEntity kart) {
        return kartRepository.save(kart);
    }

    public Optional<KartEntity> getKartById(Long id) {
        return kartRepository.findById(id);
    }

    public List<KartEntity> getAllKarts() {
        return kartRepository.findAll();
    }

    public Optional<KartEntity> getKartByCode(String code) {
        return kartRepository.findByCode(code);
    }

    public KartEntity updateKart(KartEntity kart) {
        return kartRepository.save(kart);
    }

    public void deleteKart(Long id) {
        kartRepository.deleteById(id);
    }
}
