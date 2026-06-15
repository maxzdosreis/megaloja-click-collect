package br.com.megaloja.repositories;

import br.com.megaloja.models.PickupAuthorization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupAuthorizationRepository extends JpaRepository<PickupAuthorization, Long> {

    Page<PickupAuthorization> findByOrderId(Long orderId, Pageable pageable);
}
