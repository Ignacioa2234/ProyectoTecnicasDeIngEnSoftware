package edu.mtisw.KartingRM.repositories;

import edu.mtisw.KartingRM.entities.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    Optional<ClientEntity> findByName(String name);

    Optional<ClientEntity> findByEmailAndPassword(String email, String password);
}
