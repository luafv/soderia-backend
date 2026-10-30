package com.soderia.soderia_backend.features.dtos.response;

import com.soderia.soderia_backend.features.models.enums.EstadoRecorrido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Reporte de un día de reparto. Se calcula siempre desde las entregas y pagos de la fecha.
// saldoDelDia = totalVendido - totalCobrado:
//   positivo = quedó fiado; negativo = se cobró deuda de días anteriores.
public record ReporteRecorridoResponse(
        LocalDate fecha,
        EstadoRecorrido estado,
        LocalDateTime fechaInicio,
        LocalDateTime fechaCierre,
        Long idProducto,
        String producto,
        Integer stockInicial,
        Integer stockRestante,
        Integer totalVisitas,
        Integer entregados,
        Integer ausentes,
        Integer pendientes,
        Integer sifonesEntregados,
        Integer vaciosRetirados,
        BigDecimal totalVendido,
        BigDecimal cobradoEfectivo,
        BigDecimal cobradoTransferencia,
        BigDecimal totalCobrado,
        BigDecimal saldoDelDia,
        List<VisitaReporteResponse> visitas
) {
}
