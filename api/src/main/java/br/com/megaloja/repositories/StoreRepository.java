package br.com.megaloja.repositories;

import br.com.megaloja.models.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long>, JpaSpecificationExecutor<Store> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Store s SET s.active = false WHERE s.id =:id")
    void disableStore(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Store s SET s.active = true WHERE s.id =:id")
    void enableStore(@Param("id") Long id);

    boolean existsByName(String name);
}
