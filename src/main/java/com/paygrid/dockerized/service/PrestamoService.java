package com.paygrid.dockerized.service;

import com.paygrid.dockerized.mapper.PrestamoMapper;
import com.paygrid.dockerized.mapper.CronogramaPagoMapper;
import com.paygrid.dockerized.model.entity.Prestamo;
import com.paygrid.dockerized.model.dto.CronogramaPagoDTO;
import com.paygrid.dockerized.model.dto.CronogramaPagoEmailDTO;
import com.paygrid.dockerized.model.dto.PrestamoRequestDTO;
import com.paygrid.dockerized.model.dto.PrestamoResponseDTO;
import com.paygrid.dockerized.model.dto.PrestamoDetalleDTO;
import com.paygrid.dockerized.model.entity.CronogramaPago;
import com.paygrid.dockerized.model.enums.Estado;
import com.paygrid.dockerized.model.entity.Usuario;
import com.paygrid.dockerized.repository.PrestamoRepository;
import com.paygrid.dockerized.repository.CronogramaPagoRepository;
import com.paygrid.dockerized.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private CronogramaPagoRepository cronogramaPagoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PrestamoMapper prestamoMapper;

    @Autowired
    private CronogramaPagoMapper cronogramaPagoMapper;

    @Autowired
    private NotificacionService notificacionService;

    @Transactional
    public PrestamoResponseDTO registrarPrestamo(PrestamoRequestDTO prestamoRequestDTO, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        Prestamo prestamo = prestamoMapper.toEntity(prestamoRequestDTO);
        prestamo.setUsuario(usuario);
        prestamo = prestamoRepository.save(prestamo);

        List<CronogramaPago> cronogramaPagos = generarCronograma(prestamo);
        cronogramaPagoRepository.saveAll(cronogramaPagos);

        // Verificar si la primera cuota vence hoy
        CronogramaPago primeraCuota = cronogramaPagos.get(1); // Cuota número 1
        LocalDate hoy = LocalDate.now();
        if (primeraCuota.getFechaVencimiento().isEqual(hoy)) {
            CronogramaPagoEmailDTO cronogramaPagoEmailDTO = cronogramaPagoMapper.toEmailDTO(primeraCuota);
            notificacionService.enviarAlertaPrestamo(cronogramaPagoEmailDTO, usuario);
        }

        return prestamoMapper.toDTO(prestamo);
    }

    private List<CronogramaPago> generarCronograma(Prestamo prestamo) {
        List<CronogramaPago> cronogramaPagos = new ArrayList<>();
        BigDecimal monto = prestamo.getMonto();
        BigDecimal interesAnual = prestamo.getInteres();
        BigDecimal interesMensual = interesAnual.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        int plazo = prestamo.getPlazo();
        LocalDate fechaDesembolso = prestamo.getFechaDesembolso();

        BigDecimal cuota = calcularCuota(monto, interesMensual, plazo);

        BigDecimal saldoPendiente = monto;

        for (int i = 0; i <= plazo; i++) {
            LocalDate fechaVencimiento = fechaDesembolso.plusMonths(i);

            CronogramaPago cronogramaPago = new CronogramaPago();
            cronogramaPago.setNumero(i);
            cronogramaPago.setFechaVencimiento(fechaVencimiento);
            cronogramaPago.setSaldo(saldoPendiente.setScale(2, RoundingMode.HALF_UP));

            if (i == 0) {
                cronogramaPago.setCapital(BigDecimal.ZERO);
                cronogramaPago.setInteres(BigDecimal.ZERO);
                cronogramaPago.setCuota(BigDecimal.ZERO);
                cronogramaPago.setEstado(Estado.PAGADO);
            } else {
                BigDecimal interesMes = saldoPendiente.multiply(interesMensual).setScale(2, RoundingMode.HALF_UP);
                BigDecimal capitalMes = cuota.subtract(interesMes).setScale(2, RoundingMode.HALF_UP);

                cronogramaPago.setCapital(capitalMes);
                cronogramaPago.setInteres(interesMes);
                cronogramaPago.setCuota(cuota.setScale(2, RoundingMode.HALF_UP));
                cronogramaPago.setEstado(Estado.PENDIENTE);

                saldoPendiente = saldoPendiente.subtract(capitalMes).setScale(2, RoundingMode.HALF_UP);
            }

            cronogramaPago.setPrestamo(prestamo);
            cronogramaPagos.add(cronogramaPago);
        }

        return cronogramaPagos;
    }

    private BigDecimal calcularCuota(BigDecimal monto, BigDecimal interesMensual, int plazo) {
        MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
        BigDecimal unoMasInteres = interesMensual.add(BigDecimal.ONE);
        BigDecimal unoMasInteresElevado = unoMasInteres.pow(plazo, mc);
        BigDecimal numerador = monto.multiply(interesMensual, mc).multiply(unoMasInteresElevado, mc);
        BigDecimal denominador = unoMasInteresElevado.subtract(BigDecimal.ONE, mc);
        return numerador.divide(denominador, mc).setScale(2, RoundingMode.HALF_UP);
    }

    public List<PrestamoResponseDTO> consultarPrestamos(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        List<Prestamo> prestamos = prestamoRepository.findByUsuarioId(usuario.getId());
        return prestamos.stream()
                .map(prestamoMapper::toDTO)
                .collect(Collectors.toList());
    }

    // public PrestamoDetalleDTO consultarPrestamoDetalle(Long prestamoId) {
    // Prestamo prestamo = prestamoRepository.findById(prestamoId)
    // .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado."));
    // List<CronogramaPago> cronogramaPagos =
    // cronogramaPagoRepository.findByPrestamoId(prestamoId);
    // List<CronogramaPagoDTO> cronogramaDTOs = cronogramaPagos.stream()
    // .map(cronogramaPagoMapper::toDTO)
    // .collect(Collectors.toList());

    // PrestamoDetalleDTO prestamoDetalleDTO = new PrestamoDetalleDTO();
    // prestamoDetalleDTO.setPrestamo(prestamoMapper.toDTO(prestamo));
    // prestamoDetalleDTO.setCronograma(cronogramaDTOs);

    // return prestamoDetalleDTO;
    // }

    @Transactional
    public void marcarPagoComoRealizado(Long prestamoId, int numero) {
        CronogramaPago cronogramaPago = cronogramaPagoRepository.findByPrestamoIdAndNumero(prestamoId, numero)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado para el número especificado."));

        cronogramaPago.setEstado(Estado.PAGADO);
        cronogramaPagoRepository.save(cronogramaPago);
    }

    public PrestamoDetalleDTO consultarCronograma(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado."));
        List<CronogramaPago> cronogramaPagos = cronogramaPagoRepository.findByPrestamoIdOrderByNumeroAsc(prestamoId);
        List<CronogramaPagoDTO> cronogramaPagoDTOS = cronogramaPagos.stream()
                .map(cronogramaPagoMapper::toDTO)
                .collect(Collectors.toList());

        PrestamoDetalleDTO prestamoDetalleDTO = new PrestamoDetalleDTO();
        prestamoDetalleDTO.setPrestamo(prestamoMapper.toDTO(prestamo));
        prestamoDetalleDTO.setCronograma(cronogramaPagoDTOS);
        return prestamoDetalleDTO;
    }

    @Transactional
    public void enviarNotificaciones() {
        List<CronogramaPago> pagosPendientes = cronogramaPagoRepository.findAllByEstado(Estado.PENDIENTE);
        LocalDate hoy = LocalDate.now();

        for (CronogramaPago pago : pagosPendientes) {
            if (pago.getFechaVencimiento().isEqual(hoy)) {
                Usuario usuario = pago.getPrestamo().getUsuario();
                CronogramaPagoEmailDTO cronogramaPagoEmailDTO = cronogramaPagoMapper.toEmailDTO(pago);
                notificacionService.enviarAlertaPrestamo(cronogramaPagoEmailDTO, usuario);
            }
        }
    }

    @Transactional
    public List<CronogramaPagoEmailDTO> alertarVencimientosHoy(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        LocalDate hoy = LocalDate.now();
        List<CronogramaPago> pagos = cronogramaPagoRepository
                .findByPrestamoUsuarioIdAndFechaVencimientoBetween(usuario.getId(), hoy, hoy);

        return pagos.stream()
                .map(cronogramaPagoMapper::toEmailDTO)
                .collect(Collectors.toList());
    }

}
