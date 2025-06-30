package com.paygrid.dockerized.controller;

import com.paygrid.dockerized.model.dto.OCRResponseDTO;
import com.paygrid.dockerized.service.OCRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/ocr")
@CrossOrigin(origins = "*")
public class OCRController {

    @Autowired
    private OCRService ocrService;

    @PostMapping("/procesar-imagen")
    public ResponseEntity<OCRResponseDTO> procesarImagen(
            @RequestParam("imagen") MultipartFile archivo,
            Principal principal) {
        
        // Validar que se haya subido un archivo
        if (archivo.isEmpty()) {
            OCRResponseDTO errorResponse = new OCRResponseDTO();
            errorResponse.setExito(false);
            errorResponse.setMensajeError("No se ha subido ningún archivo");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Validar tipo de archivo
        String contentType = archivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            OCRResponseDTO errorResponse = new OCRResponseDTO();
            errorResponse.setExito(false);
            errorResponse.setMensajeError("El archivo debe ser una imagen");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Validar tamaño del archivo (máximo 10MB)
        if (archivo.getSize() > 10 * 1024 * 1024) {
            OCRResponseDTO errorResponse = new OCRResponseDTO();
            errorResponse.setExito(false);
            errorResponse.setMensajeError("El archivo es demasiado grande. Máximo 10MB");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            // Procesar la imagen con OCR
            OCRResponseDTO resultado = ocrService.procesarImagen(archivo);
            
            if (resultado.isExito()) {
                return ResponseEntity.ok(resultado);
            } else {
                return ResponseEntity.badRequest().body(resultado);
            }
            
        } catch (Exception e) {
            OCRResponseDTO errorResponse = new OCRResponseDTO();
            errorResponse.setExito(false);
            errorResponse.setMensajeError("Error interno del servidor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("OCR Controller funcionando correctamente");
    }
} 