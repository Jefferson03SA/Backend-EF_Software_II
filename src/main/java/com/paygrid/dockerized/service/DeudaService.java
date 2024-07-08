package com.paygrid.dockerized.service;

import com.paygrid.dockerized.mapper.DeudaMapper;
import com.paygrid.dockerized.model.dto.DeudaRequestDTO;
import com.paygrid.dockerized.model.dto.DeudaResponseDTO;
import com.paygrid.dockerized.model.entity.Deuda;
import com.paygrid.dockerized.model.entity.Usuario;
import com.paygrid.dockerized.model.enums.Estado;
import com.paygrid.dockerized.repository.DeudaRepository;
import com.paygrid.dockerized.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeudaService {

        @Autowired
        private DeudaRepository deudaRepository;

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private DeudaMapper deudaMapper;

        @Autowired
        private NotificacionService notificacionService;

        @Transactional
        public DeudaResponseDTO registrarDeuda(DeudaRequestDTO deudaRequestDTO, String email) {
                Usuario usuario = usuarioRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

                if (deudaRepository.existsByNumeroDocumento(deudaRequestDTO.getNumeroDocumento())) {
                        throw new IllegalArgumentException("El número de documento ya existe.");
                }

                Deuda deuda = deudaMapper.toEntity(deudaRequestDTO);
                deuda.setUsuario(usuario);
                deuda.setEstado(Estado.PENDIENTE);
                deuda = deudaRepository.save(deuda);

                // Enviar notificación si la deuda vence hoy
                if (deuda.getFechaVencimiento().isEqual(LocalDate.now())) {
                        notificacionService.enviarAlerta(deudaMapper.toDTO(deuda), usuario);
                }

                return deudaMapper.toDTO(deuda);
        }

        public List<DeudaResponseDTO> consultarDeudasPorMes(String email, YearMonth mes) {
                Usuario usuario = usuarioRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

                LocalDate start = mes.atDay(1);
                LocalDate end = mes.atEndOfMonth();
                LocalDate now = LocalDate.now();

                // Obtener deudas pendientes no vencidas del mes seleccionado
                List<Deuda> deudasPendientesNoVencidasDelMes = deudaRepository
                                .findByUsuarioIdAndEstadoAndFechaVencimientoBetween(
                                                usuario.getId(), Estado.PENDIENTE, start, end);

                // Obtener todas las deudas pendientes vencidas hasta la fecha actual
                List<Deuda> deudasPendientesVencidas = deudaRepository
                                .findByUsuarioIdAndEstadoAndFechaVencimientoBefore(
                                                usuario.getId(), Estado.PENDIENTE, now);

                // Obtener deudas pagadas del mes seleccionado
                List<Deuda> deudasPagadasDelMes = deudaRepository.findByUsuarioIdAndEstadoAndFechaVencimientoBetween(
                                usuario.getId(), Estado.PAGADA, start, end);

                // Depuración
                System.out.println("Deudas pendientes no vencidas del mes: " + deudasPendientesNoVencidasDelMes);
                System.out.println("Deudas pendientes vencidas: " + deudasPendientesVencidas);
                System.out.println("Deudas pagadas del mes: " + deudasPagadasDelMes);

                // Filtrar deudas vencidas que pertenecen a meses anteriores al mes seleccionado
                List<Deuda> deudasPendientesVencidasMesesAnteriores = deudasPendientesVencidas.stream()
                                .filter(deuda -> deuda.getFechaVencimiento().isBefore(start))
                                .collect(Collectors.toList());

                List<Deuda> todasDeudas = new ArrayList<>();
                todasDeudas.addAll(deudasPendientesNoVencidasDelMes);
                todasDeudas.addAll(deudasPendientesVencidasMesesAnteriores);
                todasDeudas.addAll(deudasPagadasDelMes);

                return todasDeudas.stream()
                                .map(deudaMapper::toDTO)
                                .collect(Collectors.toList());
        }

        @Transactional
        public void marcarComoPagada(Long deudaId, String email) {
                Usuario usuario = usuarioRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

                Deuda deuda = deudaRepository.findById(deudaId)
                                .orElseThrow(() -> new IllegalArgumentException("Deuda no encontrada."));

                if (!deuda.getUsuario().getId().equals(usuario.getId())) {
                        throw new IllegalArgumentException("No autorizado.");
                }

                if (deuda.getEstado() == Estado.PAGADA) {
                        throw new IllegalArgumentException("La deuda ya está pagada.");
                }

                deuda.setEstado(Estado.PAGADA);
                deudaRepository.save(deuda);
        }

        // public List<DeudaResponseDTO> alertarVencimientosHoy(String email) {
        //         Usuario usuario = usuarioRepository.findByEmail(email)
        //                         .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        //         LocalDate hoy = LocalDate.now();
        //         List<Deuda> deudas = deudaRepository.findByUsuarioIdAndFechaVencimientoBetween(usuario.getId(), hoy,
        //                         hoy);

        //         List<DeudaResponseDTO> deudasResponse = deudas.stream()
        //                         .map(deudaMapper::toDTO)
        //                         .collect(Collectors.toList());

        //         deudas.forEach(deuda -> notificacionService.enviarAlerta(deudaMapper.toDTO(deuda), deuda.getUsuario()));

        //         return deudasResponse;
        // }

        public List<DeudaResponseDTO> alertarVencimientosHoy(String email) {
                Usuario usuario = usuarioRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
            
                LocalDate hoy = LocalDate.now();
                List<Deuda> deudas = deudaRepository.findByUsuarioIdAndFechaVencimientoBetween(usuario.getId(), hoy, hoy);
            
                return deudas.stream()
                        .map(deudaMapper::toDTO)
                        .collect(Collectors.toList());
            }
            

        public List<DeudaResponseDTO> obtenerDeudasVencenHoy() {
                LocalDate hoy = LocalDate.now();
                List<Deuda> deudas = deudaRepository.findByFechaVencimientoAndEstado(hoy, Estado.PENDIENTE);
                return deudas.stream()
                                .map(deudaMapper::toDTO)
                                .collect(Collectors.toList());
        }

}