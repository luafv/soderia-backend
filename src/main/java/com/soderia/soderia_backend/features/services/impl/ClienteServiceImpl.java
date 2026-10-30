package com.soderia.soderia_backend.features.services.impl;

import com.soderia.soderia_backend.exceptions.RecursoNoEncontradoException;
import com.soderia.soderia_backend.features.dtos.request.ClienteRequest;
import com.soderia.soderia_backend.features.dtos.request.SifonesRequest;
import com.soderia.soderia_backend.features.dtos.response.ClienteDetalleResponse;
import com.soderia.soderia_backend.features.dtos.response.ClienteResponse;
import com.soderia.soderia_backend.features.dtos.response.VisitaResponse;
import com.soderia.soderia_backend.features.mappers.ClienteMapper;
import com.soderia.soderia_backend.features.mappers.VisitaMapper;
import com.soderia.soderia_backend.features.models.Cliente;
import com.soderia.soderia_backend.features.models.enums.EstadoVisita;
import com.soderia.soderia_backend.features.repositories.ClienteRepository;
import com.soderia.soderia_backend.features.repositories.VisitaRepository;
import com.soderia.soderia_backend.features.services.interfaces.ClienteService;
import com.soderia.soderia_backend.features.services.interfaces.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final VisitaRepository visitaRepository;
    private final PagoService pagoService;
    private final ClienteMapper clienteMapper;
    private final VisitaMapper visitaMapper;

    // HU-05: lista con filtros opcionales por nombre/apellido y por dirección
    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar(String buscar, String direccion, boolean incluirInactivos) {
        return clienteRepository.buscar(texto(buscar), texto(direccion), incluirInactivos).stream()
                .map(clienteMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        return clienteMapper.toResponse(obtenerCliente(id));
    }

    // HU-06: datos (con sifones en casa), visitas pendientes y estado de cuenta
    @Override
    @Transactional(readOnly = true)
    public ClienteDetalleResponse detalle(Long id) {
        Cliente cliente = obtenerCliente(id);
        List<VisitaResponse> pendientes = visitaRepository
                .findByClienteIdClienteAndEstadoOrderByFechaAsc(id, EstadoVisita.PENDIENTE).stream()
                .map(visitaMapper::toResponse)
                .toList();
        return new ClienteDetalleResponse(
                clienteMapper.toResponse(cliente),
                pendientes,
                pagoService.estadoDeCuenta(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listarPorDia(DayOfWeek dia) {
        return clienteRepository.findByDiaEntregaAndActivoTrueOrderByDireccionAsc(dia).stream()
                .map(clienteMapper::toResponse)
                .toList();
    }

    @Override
    public ClienteResponse crear(ClienteRequest request) {
        Cliente guardado = clienteRepository.save(clienteMapper.toEntity(request));
        return clienteMapper.toResponse(guardado);
    }

    @Override
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = obtenerCliente(id);
        clienteMapper.updateEntity(cliente, request);
        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    // Corrección manual de los envases que tiene el cliente
    @Override
    public ClienteResponse actualizarSifones(Long id, SifonesRequest request) {
        Cliente cliente = obtenerCliente(id);
        cliente.setSifonesEnCasa(request.sifonesEnCasa());
        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    // HU-04: baja lógica. El cliente queda en la base con todo su historial.
    @Override
    public void darDeBaja(Long id) {
        Cliente cliente = obtenerCliente(id);
        cliente.setActivo(false);
        clienteRepository.save(cliente);
    }

    @Override
    public ClienteResponse reactivar(Long id) {
        Cliente cliente = obtenerCliente(id);
        cliente.setActivo(true);
        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    // null o vacío = sin filtro
    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private Cliente obtenerCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente con id " + id));
    }
}
