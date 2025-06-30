package com.paygrid.dockerized.service;

import com.paygrid.dockerized.model.dto.OCRResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OCRService {

    private final TextractClient textractClient;

    @Autowired
    public OCRService(TextractClient textractClient) {
        this.textractClient = textractClient;
    }

    public OCRResponseDTO procesarImagen(MultipartFile archivo) {
        OCRResponseDTO respuesta = new OCRResponseDTO();
        
        try {
            // Convertir archivo a bytes
            byte[] imagenBytes = archivo.getBytes();
            
            // Crear documento para AWS Textract
            Document documento = Document.builder()
                    .bytes(SdkBytes.fromByteArray(imagenBytes))
                    .build();

            // Configurar request para detección de texto
            DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
                    .document(documento)
                    .build();

            // Llamar a AWS Textract
            DetectDocumentTextResponse response = textractClient.detectDocumentText(request);
            
            // Extraer todo el texto
            String textoCompleto = extraerTextoCompleto(response);
            
            // Parsear datos específicos
            String empresa = extraerEmpresa(textoCompleto);
            BigDecimal monto = extraerMonto(textoCompleto);
            LocalDate fechaVencimiento = extraerFechaVencimiento(textoCompleto);
            String numeroDocumento = extraerNumeroDocumento(textoCompleto);

            // Construir respuesta exitosa
            respuesta.setEmpresa(empresa);
            respuesta.setMonto(monto);
            respuesta.setFechaVencimiento(fechaVencimiento);
            respuesta.setNumeroDocumento(numeroDocumento);
            respuesta.setTextoExtraido(textoCompleto);
            respuesta.setExito(true);
            respuesta.setMensajeError(null);

        } catch (Exception e) {
            // Construir respuesta de error
            respuesta.setExito(false);
            respuesta.setMensajeError("Error al procesar la imagen: " + e.getMessage());
            respuesta.setTextoExtraido("");
        }

        return respuesta;
    }

    private String extraerTextoCompleto(DetectDocumentTextResponse response) {
        StringBuilder texto = new StringBuilder();
        List<Block> blocks = response.blocks();
        
        for (Block block : blocks) {
            if (block.blockType().equals(BlockType.LINE)) {
                texto.append(block.text()).append(" ");
            }
        }
        
        return texto.toString();
    }

    private String extraerEmpresa(String texto) {
        // Patrones específicos para empresas de servicios públicos
        String[] patrones = {
            // Patrones específicos para recibos de luz
            "([A-ZÁÉÍÓÚÑ\\s]+)\\s+R\\.U\\.C\\.",
            "([A-ZÁÉÍÓÚÑ\\s]+)\\s+RUC:",
            "([A-ZÁÉÍÓÚÑ\\s]+)\\s+RUCN",
            // Patrones generales
            "EMPRESA:\\s*([A-ZÁÉÍÓÚÑ\\s]+)",
            "PROVEEDOR:\\s*([A-ZÁÉÍÓÚÑ\\s]+)",
            "SERVICIO:\\s*([A-ZÁÉÍÓÚÑ\\s]+)",
            "([A-ZÁÉÍÓÚÑ\\s]{3,})\\s+S\\.A\\.?",
            "([A-ZÁÉÍÓÚÑ\\s]{3,})\\s+E\\.I\\.R\\.L",
            "([A-ZÁÉÍÓÚÑ\\s]{3,})\\s+S\\.R\\.L"
        };

        for (String patron : patrones) {
            Pattern pattern = Pattern.compile(patron, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(texto);
            if (matcher.find()) {
                String empresa = matcher.group(1).trim();
                // Filtrar resultados muy cortos o que contengan palabras no deseadas
                if (empresa.length() > 2 && !empresa.contains("RECIBO") && !empresa.contains("N°")) {
                    return empresa;
                }
            }
        }

        // Si no encuentra patrones específicos, buscar palabras en mayúsculas al inicio
        Pattern pattern = Pattern.compile("^([A-ZÁÉÍÓÚÑ\\s]{3,})", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(texto);
        if (matcher.find()) {
            String empresa = matcher.group(1).trim();
            if (!empresa.contains("RECIBO") && !empresa.contains("N°")) {
                return empresa;
            }
        }

        return "Empresa no identificada";
    }

    private BigDecimal extraerMonto(String texto) {
        // Patrones específicos para recibos de servicios públicos
        String[] patrones = {
            // Patrones específicos para "TOTAL A PAGAR"
            "TOTAL\\s+A\\s+PAGAR\\s+S/\\s*\\*+([0-9,]+\\.[0-9]{2})",
            "TOTAL\\s+A\\s+PAGAR\\s+S/\\s*([0-9,]+\\.[0-9]{2})",
            "TOTAL\\s+PAGAR\\s+S/\\s*([0-9,]+\\.[0-9]{2})",
            // Patrones generales
            "TOTAL:\\s*S/\\s*([0-9,]+\\.[0-9]{2})",
            "MONTO:\\s*S/\\s*([0-9,]+\\.[0-9]{2})",
            "IMPORTE:\\s*S/\\s*([0-9,]+\\.[0-9]{2})",
            "S/\\s*([0-9,]+\\.[0-9]{2})",
            "([0-9,]+\\.[0-9]{2})\\s*SOLES"
        };

        for (String patron : patrones) {
            Pattern pattern = Pattern.compile(patron, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(texto);
            if (matcher.find()) {
                String montoStr = matcher.group(1).replace(",", "");
                try {
                    BigDecimal monto = new BigDecimal(montoStr);
                    // Preferir montos mayores a 1.00 (evitar decimales pequeños)
                    if (monto.compareTo(BigDecimal.ONE) >= 0) {
                        return monto;
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }

        return BigDecimal.ZERO;
    }

    private LocalDate extraerFechaVencimiento(String texto) {
        // Patrones para fechas de vencimiento
        String[] patrones = {
            "FECHA\\s+DE\\s+VENCIMIENTO\\s*([0-9]{1,2}/[0-9]{1,2}/[0-9]{4})",
            "VENCE:\\s*([0-9]{1,2}/[0-9]{1,2}/[0-9]{4})",
            "VENCIMIENTO:\\s*([0-9]{1,2}/[0-9]{1,2}/[0-9]{4})",
            "PAGAR\\s+HASTA:\\s*([0-9]{1,2}/[0-9]{1,2}/[0-9]{4})",
            "FECHA\\s+CORTE:\\s*([0-9]{1,2}/[0-9]{1,2}/[0-9]{4})"
        };

        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("d-M-yyyy")
        };

        for (String patron : patrones) {
            Pattern pattern = Pattern.compile(patron, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(texto);
            if (matcher.find()) {
                String fechaStr = matcher.group(1);
                
                for (DateTimeFormatter formatter : formatters) {
                    try {
                        return LocalDate.parse(fechaStr, formatter);
                    } catch (DateTimeParseException e) {
                        continue;
                    }
                }
            }
        }

        // Si no encuentra fecha específica, usar fecha actual + 30 días
        return LocalDate.now().plusDays(30);
    }

    private String extraerNumeroDocumento(String texto) {
        // Patrones para números de documento
        String[] patrones = {
            "RECIBO\\s+N°\\s*([A-Z0-9-]+)",
            "FACTURA\\s+N°\\s*([A-Z0-9-]+)",
            "BOLETA\\s+N°\\s*([A-Z0-9-]+)",
            "NRO\\s+DOC:\\s*([0-9-]+)",
            "DOCUMENTO:\\s*([0-9-]+)",
            "FACTURA:\\s*([0-9-]+)",
            "BOLETA:\\s*([0-9-]+)",
            "RECIBO:\\s*([0-9-]+)"
        };

        for (String patron : patrones) {
            Pattern pattern = Pattern.compile(patron, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(texto);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }

        // Si no encuentra, generar un número único
        return "DOC-" + System.currentTimeMillis();
    }
} 