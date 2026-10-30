package com.soderia.soderia_backend.features.models;

import com.soderia.soderia_backend.features.models.enums.EstadoRecorrido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Un día de reparto: se inicia al cargar el camión y se cierra cuando el repartidor termina.
// Los números del reporte NO se guardan acá: se calculan desde las entregas y los pagos de la fecha.
@Entity
@Table(name = "recorridos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recorrido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recorrido")
    private Long idRecorrido;

    // Un solo recorrido por día
    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    // Producto que se cargó en el camión (por ahora siempre el sifón)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    // Unidades con las que salió el camión
    @Column(name = "stock_inicial", nullable = false)
    private Integer stockInicial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoRecorrido estado = EstadoRecorrido.ABIERTO;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;
}
