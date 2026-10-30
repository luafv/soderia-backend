package com.soderia.soderia_backend.features.services.interfaces;

import com.soderia.soderia_backend.features.dtos.request.IniciarRecorridoRequest;
import com.soderia.soderia_backend.features.dtos.response.ReporteRecorridoResponse;
import com.soderia.soderia_backend.features.dtos.response.ResumenRecorridoResponse;

import java.time.LocalDate;
import java.util.List;

public interface RecorridoService {

    // Carga el camión, abre el día y genera las visitas
    ReporteRecorridoResponse iniciar(IniciarRecorridoRequest request);

    // Cierra el día: las visitas pendientes pasan a AUSENTE
    ReporteRecorridoResponse finalizar(LocalDate fecha);

    ReporteRecorridoResponse reporte(LocalDate fecha);

    // Para el calendario
    List<ResumenRecorridoResponse> historial(LocalDate desde, LocalDate hasta);
}
