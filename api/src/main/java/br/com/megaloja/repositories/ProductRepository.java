package br.com.megaloja.repositories;

import br.com.megaloja.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product p SET p.active = false WHERE p.id =:id")
    void disableProduct(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product p SET p.active = true WHERE p.id =:id")
    void enableProduct(@Param("id") Long id);

    boolean existsByName(String name);
}
