package com.soderia.soderia_backend.features.models;

<<<<<<< HEAD
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
=======
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "productos")
>>>>>>> cf4214d185f710eeb5a51d84527e40e36122e4c4
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
<<<<<<< HEAD
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(nullable = false)
    private BigDecimal precio;

=======
    private Long idProducto;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull
>>>>>>> cf4214d185f710eeb5a51d84527e40e36122e4c4
    @Column(nullable = false)
    private Integer stock;
}
