package com.example.relay.catalog.internal.repository;

import com.example.relay.catalog.internal.domain.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkuRepository extends JpaRepository<Sku, Long> {

}
