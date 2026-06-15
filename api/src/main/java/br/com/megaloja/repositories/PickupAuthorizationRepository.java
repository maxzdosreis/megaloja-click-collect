package br.com.megaloja.repositories;

import br.com.megaloja.models.PickupAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupAuthorizationRepository extends JpaRepository<PickupAuthorization, Long> {
}
