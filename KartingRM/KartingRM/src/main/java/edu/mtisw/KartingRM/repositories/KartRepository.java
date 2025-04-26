package edu.mtisw.KartingRM.repositories;

import edu.mtisw.KartingRM.entities.KartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface KartRepository extends JpaRepository<KartEntity, Long> {

    Optional<KartEntity> findByCode(String code);

    List<KartEntity> findAllByOrderByCodeAsc();

    boolean existsByCode(String code);
}