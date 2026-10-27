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

    // Día fijo de reparto (LUNES..DOMINGO se guarda como MONDAY..SUNDAY)
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_entrega", length = 10)
    private DayOfWeek diaEntrega;
}
