package com.example.relay.catalog.internal.repository;

import com.example.relay.catalog.internal.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    public Category findByPublicId(UUID publicId);
}
