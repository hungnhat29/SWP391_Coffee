package com.SWP391.G3PCoffee.repository;


import com.SWP391.G3PCoffee.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query(value = "select p from Product p " +
            "where p.categoryId = ?1")
    List<Product> findByCategoryId(Integer categoryId);

}

