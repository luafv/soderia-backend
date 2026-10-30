package com.soderia.soderia_backend.features.models;

import com.soderia.soderia_backend.features.models.enums.EstadoVisita;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "visitas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_visita")
    private Long idVisita;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoVisita estado = EstadoVisita.PENDIENTE;

    // Envases vacíos que se llevó el repartidor en la entrega (null mientras no se entrega)
    @Column(name = "vacios_retirados")
    private Integer vaciosRetirados;

    // Los detalles se guardan y borran junto con la visita
    @OneToMany(mappedBy = "visita", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleVisita> detalles = new ArrayList<>();

    public void agregarDetalle(DetalleVisita detalle) {
        detalles.add(detalle);
        detalle.setVisita(this);
    }
}
