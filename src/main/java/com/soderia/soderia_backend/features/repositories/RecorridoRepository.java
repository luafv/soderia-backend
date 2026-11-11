package com.soderia.soderia_backend.features.repositories;

import com.soderia.soderia_backend.features.models.Recorrido;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface RecorridoRepository extends JpaRepository<Recorrido, Long> {

    Optional<Recorrido> findByFecha(LocalDate fecha);

    boolean existsByFecha(LocalDate fecha);

    // Para el calendario
    List<Recorrido> findByFechaBetweenOrderByFechaAsc(LocalDate desde, LocalDate hasta);
}
