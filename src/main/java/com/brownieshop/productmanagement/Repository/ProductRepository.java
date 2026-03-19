package com.brownieshop.productmanagement.Repository;




import com.brownieshop.productmanagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByFeaturedTrue();

    @Query("""
        SELECT p FROM Product p
        WHERE (:category IS NULL OR LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%')))
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice  IS NULL OR p.price <= :maxPrice)
        """)
    List<Product> findFiltered(
            @Param("category") String category,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );
}