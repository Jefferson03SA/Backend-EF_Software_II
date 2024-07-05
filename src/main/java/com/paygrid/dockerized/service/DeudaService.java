package com.paygrid.dockerized.service;

import com.paygrid.dockerized.exception.DeudaNotFoundException;
import com.paygrid.dockerized.exception.UsuarioNotFoundException;
import com.paygrid.dockerized.mapper.DeudaMapper;
import com.paygrid.dockerized.model.dto.DeudaRequestDTO;
import com.paygrid.dockerized.model.dto.DeudaResponseDTO;
import com.paygrid.dockerized.model.entity.Deuda;
import com.paygrid.dockerized.repository.DeudaRepository;
import com.paygrid.dockerized.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeudaService {

    @Autowired
    private DeudaRepository deudaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DeudaMapper deudaMapper;

    public DeudaResponseDTO registrarDeuda(Long usuarioId, DeudaRequestDTO deudaRequestDTO) {
        Deuda deuda = deudaMapper.toEntity(deudaRequestDTO);
        deuda.setUsuario(usuarioRepository.findById(usuarioId).orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado")));
        deuda.setPagada(false);
        deudaRepository.save(deuda);
        return deudaMapper.toResponseDTO(deuda);
    }

    public List<DeudaResponseDTO> obtenerDeudasPorUsuario(Long usuarioId) {
        List<Deuda> deudas = deudaRepository.findByUsuarioId(usuarioId);
        return deudas.stream().map(deudaMapper::toResponseDTO).collect(Collectors.toList());
    }

    public DeudaResponseDTO marcarDeudaComoPagada(Long deudaId) {
        Deuda deuda = deudaRepository.findById(deudaId).orElseThrow(() -> new DeudaNotFoundException("Deuda no encontrada"));
        deuda.setPagada(true);
        deudaRepository.save(deuda);
        return deudaMapper.toResponseDTO(deuda);
    }
}
