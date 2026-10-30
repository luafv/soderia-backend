package com.soderia.soderia_backend.features.services.interfaces;

import com.soderia.soderia_backend.features.dtos.request.EntregaRequest;
import com.soderia.soderia_backend.features.dtos.request.GenerarVisitasRequest;
import com.soderia.soderia_backend.features.dtos.response.EntregaResponse;
import com.soderia.soderia_backend.features.dtos.response.VisitaResponse;
import com.soderia.soderia_backend.features.models.enums.EstadoVisita;

import java.time.LocalDate;
import java.util.List;

public interface VisitaService {

    List<VisitaResponse> listarPorFecha(LocalDate fecha, EstadoVisita estado);

    List<VisitaResponse> listarPorCliente(Long idCliente);

    VisitaResponse buscarPorId(Long id);

    List<VisitaResponse> generarVisitasDelDia(GenerarVisitasRequest request);

    EntregaResponse entregar(Long id, EntregaRequest request);

    VisitaResponse marcarAusente(Long id);

    void eliminar(Long id);
}
