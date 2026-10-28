package com.soderia.soderia_backend.features.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false)
    private String direccion;

    @Column(length = 150)
    private String email;

    // Para mandarle el estado de cuenta por WhatsApp
    @Column(length = 30)
    private String telefono;

    // Día fijo de reparto (MONDAY..SUNDAY)
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_entrega", length = 10)
    private DayOfWeek diaEntrega;

    // Envases de la sodería que tiene el cliente en su casa (llenos + vacíos)
    @Column(name = "sifones_en_casa", columnDefinition = "integer default 0 not null")
    @Builder.Default
    private Integer sifonesEnCasa = 0;

    // Baja lógica: un cliente dado de baja no se borra, se marca como inactivo
    @Column(columnDefinition = "boolean default true not null")
    @Builder.Default
    private Boolean activo = true;
}
