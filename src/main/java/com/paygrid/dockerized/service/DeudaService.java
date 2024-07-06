package com.paygrid.dockerized.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.paygrid.dockerized.mapper.DeudaMapper;
import com.paygrid.dockerized.model.dto.DeudaRequestDTO;
import com.paygrid.dockerized.model.dto.DeudaResponseDTO;
import com.paygrid.dockerized.model.entity.Deuda;
import com.paygrid.dockerized.model.entity.Usuario;
import com.paygrid.dockerized.repository.DeudaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeudaService {

    @Autowired
    private DeudaRepository deudaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DeudaMapper deudaMapper;

    public DeudaResponseDTO registrarDeuda(DeudaRequestDTO deudaRequestDTO, Long usuarioId) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
        Deuda deuda = deudaMapper.toEntity(deudaRequestDTO);
        deuda.setUsuario(usuario);
        Deuda nuevaDeuda = deudaRepository.save(deuda);
        return deudaMapper.toDTO(nuevaDeuda);
    }

    public List<DeudaResponseDTO> obtenerDeudasPorUsuario(Long usuarioId) {
        List<Deuda> deudas = deudaRepository.findByUsuarioId(usuarioId);
        return deudas.stream()
                .map(deudaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public DeudaResponseDTO marcarDeudaComoPagada(Long deudaId) {
        Deuda deuda = deudaRepository.findById(deudaId).orElseThrow(() -> new RuntimeException("Deuda no encontrada"));
        deuda.setEstado("Pagada");
        Deuda deudaActualizada = deudaRepository.save(deuda);
        return deudaMapper.toDTO(deudaActualizada);
    }
}
