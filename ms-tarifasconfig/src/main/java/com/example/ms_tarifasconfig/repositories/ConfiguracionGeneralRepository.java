package com.example.ms_tarifasconfig.repositories;

import com.example.ms_tarifasconfig.entities.ConfiguracionGeneralEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ConfiguracionGeneralRepository extends JpaRepository<ConfiguracionGeneralEntity, Long> {
    @Query("SELECT c FROM ConfiguracionGeneralEntity c ORDER BY c.id ASC LIMIT 1")
    Optional<ConfiguracionGeneralEntity> findFirst();
}
