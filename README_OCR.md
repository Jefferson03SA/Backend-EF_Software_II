# Funcionalidad OCR - PayGrid

## Descripción
Esta funcionalidad permite extraer automáticamente datos de facturas, recibos y otros documentos usando AWS Textract (OCR).

## Endpoints Disponibles

### 1. Procesar Imagen con OCR
**POST** `/api/v1/ocr/procesar-imagen`

**Descripción:** Sube una imagen y extrae automáticamente los datos de la deuda.

**Parámetros:**
- `imagen` (MultipartFile): Archivo de imagen (JPG, PNG, etc.)

**Respuesta Exitosa:**
```json
{
  "empresa": "Luz del Sur S.A.",
  "monto": 150.50,
  "fechaVencimiento": "2024-01-15",
  "numeroDocumento": "F001-00012345",
  "textoExtraido": "Texto completo extraído de la imagen...",
  "exito": true,
  "mensajeError": null
}
```

**Respuesta de Error:**
```json
{
  "empresa": null,
  "monto": null,
  "fechaVencimiento": null,
  "numeroDocumento": null,
  "textoExtraido": "",
  "exito": false,
  "mensajeError": "Descripción del error"
}
```

### 2. Test del Controlador
**GET** `/api/v1/ocr/test`

**Descripción:** Verifica que el controlador OCR esté funcionando.

## Cómo Usar

### 1. Configuración AWS (Opcional)
Para desarrollo local, puedes:
- Usar el perfil AWS por defecto
- Configurar credenciales en `application.properties`:
  ```properties
  aws.accessKeyId=tu-access-key
  aws.secretKey=tu-secret-key
  aws.region=us-east-1
  ```

### 2. Ejemplo con cURL
```bash
curl -X POST \
  http://localhost:8080/api/v1/ocr/procesar-imagen \
  -H "Content-Type: multipart/form-data" \
  -F "imagen=@/ruta/a/tu/factura.jpg"
```

### 3. Ejemplo con JavaScript/Fetch
```javascript
const formData = new FormData();
formData.append('imagen', fileInput.files[0]);

fetch('/api/v1/ocr/procesar-imagen', {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => {
  if (data.exito) {
    // Rellenar formulario con datos extraídos
    document.getElementById('empresa').value = data.empresa;
    document.getElementById('monto').value = data.monto;
    document.getElementById('fechaVencimiento').value = data.fechaVencimiento;
    document.getElementById('numeroDocumento').value = data.numeroDocumento;
  } else {
    alert('Error: ' + data.mensajeError);
  }
});
```

## Formatos Soportados

### Tipos de Imagen
- JPG/JPEG
- PNG
- BMP
- TIFF

### Tamaño Máximo
- 10MB por archivo

### Patrones Reconocidos

#### Empresa
- "EMPRESA: [nombre]"
- "PROVEEDOR: [nombre]"
- "SERVICIO: [nombre]"
- "[nombre] S.A."
- "[nombre] E.I.R.L"
- "[nombre] S.R.L"

#### Monto
- "TOTAL: S/ [monto]"
- "MONTO: S/ [monto]"
- "IMPORTE: S/ [monto]"
- "S/ [monto]"
- "[monto] SOLES"

#### Fecha de Vencimiento
- "VENCE: [fecha]"
- "VENCIMIENTO: [fecha]"
- "FECHA VENCIMIENTO: [fecha]"
- "PAGAR HASTA: [fecha]"

Formatos de fecha soportados:
- dd/MM/yyyy
- d/M/yyyy
- dd-MM-yyyy
- d-M-yyyy

#### Número de Documento
- "NRO DOC: [número]"
- "DOCUMENTO: [número]"
- "FACTURA: [número]"
- "BOLETA: [número]"
- "RECIBO: [número]"

## Limitaciones

1. **Precisión:** Depende de la calidad de la imagen y el formato del documento
2. **Idioma:** Optimizado para documentos en español
3. **Formato:** Funciona mejor con documentos estructurados (facturas, recibos)
4. **Costo:** AWS Textract tiene una capa gratuita de 1,000 páginas por mes

## Solución de Problemas

### Error: "No se ha subido ningún archivo"
- Verifica que estés enviando el archivo con el nombre correcto: `imagen`

### Error: "El archivo debe ser una imagen"
- Asegúrate de que el archivo sea una imagen válida (JPG, PNG, etc.)

### Error: "El archivo es demasiado grande"
- Comprime la imagen o usa una de menor resolución

### Error: "Error al procesar la imagen"
- Verifica tu conexión a internet
- Revisa las credenciales de AWS
- Asegúrate de que la imagen sea legible

## Integración con Frontend

Para integrar esta funcionalidad en tu frontend:

1. **Agregar botón de escaneo** en el formulario de registro de deudas
2. **Implementar subida de archivo** usando el endpoint
3. **Rellenar automáticamente** los campos del formulario con los datos extraídos
4. **Permitir edición manual** de los datos extraídos antes de guardar

## Costos AWS

- **Capa gratuita:** 1,000 páginas por mes
- **Después de la capa gratuita:** ~$1.50 por 1,000 páginas
- **Para un proyecto universitario:** Muy probable que no excedas la capa gratuita 