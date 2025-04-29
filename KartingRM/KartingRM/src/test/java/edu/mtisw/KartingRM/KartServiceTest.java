// src/test/java/edu/mtisw/KartingRM/KartServiceTest.java
package edu.mtisw.KartingRM;

import edu.mtisw.KartingRM.entities.KartEntity;
import edu.mtisw.KartingRM.repositories.KartRepository;
import edu.mtisw.KartingRM.services.KartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KartServiceTest {

    @Mock KartRepository kartRepo;
    @InjectMocks KartService kartService;

    @Test
    void createKart_delegates() {
        KartEntity k = new KartEntity();
        when(kartRepo.save(k)).thenReturn(k);
        assertSame(k, kartService.createKart(k));
    }

    @Test
    void getKartById_found() {
        KartEntity k = new KartEntity();
        when(kartRepo.findById(1L)).thenReturn(Optional.of(k));
        assertTrue(kartService.getKartById(1L).isPresent());
    }

    @Test
    void getKartById_notFound() {
        when(kartRepo.findById(2L)).thenReturn(Optional.empty());
        assertTrue(kartService.getKartById(2L).isEmpty());
    }

    @Test
    void getAllKarts_delegates() {
        KartEntity k = new KartEntity();
        when(kartRepo.findAll()).thenReturn(List.of(k));
        var list = kartService.getAllKarts();
        assertEquals(1, list.size());
        assertSame(k, list.get(0));
    }

    @Test
    void getKartByCode_foundAndNotFound() {
        KartEntity k = new KartEntity();
        when(kartRepo.findByCode("A")).thenReturn(Optional.of(k));
        assertTrue(kartService.getKartByCode("A").isPresent());
        when(kartRepo.findByCode("B")).thenReturn(Optional.empty());
        assertTrue(kartService.getKartByCode("B").isEmpty());
    }

    @Test
    void updateAndDelete_delegates() {
        KartEntity k = new KartEntity();
        when(kartRepo.save(k)).thenReturn(k);
        assertSame(k, kartService.updateKart(k));
        doNothing().when(kartRepo).deleteById(5L);
        kartService.deleteKart(5L);
        verify(kartRepo).deleteById(5L);
    }
}
