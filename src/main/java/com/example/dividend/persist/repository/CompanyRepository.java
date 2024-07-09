package com.example.dividend.persist.repository;

import com.example.dividend.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity,Long> {
    boolean existsByTicker(String ticker);

    Optional<CompanyEntity>findByName(String name);
    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String keyword, PageRequest pageRequest);

    Optional<CompanyEntity>findByTicker(String ticker);
}
