package com.soderia.soderia_backend.features.repositories;

import com.soderia.soderia_backend.features.models.Recorrido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecorridoRepository extends JpaRepository<Recorrido, Long> {

    Optional<Recorrido> findByFecha(LocalDate fecha);

    boolean existsByFecha(LocalDate fecha);

    // Para el calendario
    List<Recorrido> findByFechaBetweenOrderByFechaAsc(LocalDate desde, LocalDate hasta);
}
