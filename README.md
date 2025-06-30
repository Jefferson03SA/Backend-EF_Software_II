# Backend PayGrid - Software II

Este es el backend de la aplicación PayGrid, un sistema de gestión de préstamos y deudas.

## Endpoints de la API

### Usuarios (`/usuarios`)

#### Registro y Autenticación
- `POST /usuarios/registro` - Registrar un nuevo usuario
  - Body: `{ "email": string, "username": string, "password": string }`

- `POST /usuarios/login` - Iniciar sesión
  - Body: `{ "email": string, "password": string }`

- `POST /usuarios/logout` - Cerrar sesión

- `GET /usuarios/check-auth` - Verificar autenticación

### Préstamos (`/prestamos`)

- `POST /prestamos/registrar` - Registrar un nuevo préstamo
  - Requiere autenticación
  - Body: Detalles del préstamo

- `GET /prestamos` - Consultar todos los préstamos del usuario
  - Requiere autenticación

- `GET /prestamos/{prestamoId}/cronograma` - Consultar el cronograma de un préstamo
  - Requiere autenticación

- `PATCH /prestamos/{prestamoId}/cronograma/{numero}/pagar` - Marcar un pago como realizado
  - Requiere autenticación

- `POST /prestamos/enviar-notificaciones` - Enviar notificaciones de préstamos
  - Requiere autenticación

### Deudas (`/deudas`)

- `POST /deudas/registro` - Registrar una nueva deuda
  - Requiere autenticación
  - Body: Detalles de la deuda

- `GET /deudas/consulta?mes={YYYY-MM}` - Consultar deudas por mes
  - Requiere autenticación
  - Parámetro: mes en formato YYYY-MM

- `PATCH /deudas/{deudaId}/pagar` - Marcar una deuda como pagada
  - Requiere autenticación

- `POST /deudas/alertas` - Obtener alertas de vencimientos del día
  - Requiere autenticación

### WhatsApp (`/whatsapp`)

- `POST /whatsapp/session/start` - Iniciar una sesión de WhatsApp
  - Parámetro: `phoneNumber`

- `POST /whatsapp/message/send` - Enviar un mensaje de WhatsApp
  - Body: Detalles del mensaje

- `GET /whatsapp/session/status` - Verificar el estado de una sesión
  - Parámetro: `phoneNumber`

- `PUT /whatsapp/session/status` - Actualizar el estado de una sesión
  - Parámetros: 
    - `phoneNumber`
    - `isActive`

## Estructura de Datos

### Entidades

#### Usuario
```java
{
    Long id;                    // ID único del usuario
    String email;              // Email único del usuario (requerido)
    String username;           // Nombre de usuario único (requerido)
    String password;           // Contraseña del usuario (requerido)
}
```

#### Prestamo
```java
{
    Long id;                    // ID único del préstamo
    BigDecimal monto;          // Monto del préstamo
    BigDecimal interes;        // Tasa de interés
    LocalDate fechaDesembolso; // Fecha de desembolso
    Integer plazo;             // Plazo en meses
    String entidad;            // Entidad financiera
    Usuario usuario;           // Usuario asociado
    List<CronogramaPago> cronogramaPagos; // Lista de pagos programados
}
```

#### Deuda
```java
{
    Long id;                    // ID único de la deuda
    String numeroDocumento;     // Número de documento único (requerido)
    String empresa;            // Empresa acreedora (requerido)
    BigDecimal monto;          // Monto de la deuda (requerido)
    LocalDate fechaVencimiento; // Fecha de vencimiento (requerido)
    Estado estado;             // Estado de la deuda (ENUM)
    Usuario usuario;           // Usuario asociado
}
```

#### CronogramaPago
```java
{
    Long id;                    // ID único del pago
    Integer numero;            // Número de cuota
    LocalDate fechaVencimiento; // Fecha de vencimiento
    BigDecimal saldo;          // Saldo pendiente
    BigDecimal capital;        // Capital a pagar
    BigDecimal interes;        // Interés a pagar
    BigDecimal cuota;          // Monto total de la cuota
    Estado estado;             // Estado del pago (ENUM)
    Prestamo prestamo;         // Préstamo asociado
}
```

#### WhatsAppSession
```java
{
    Long id;                    // ID único de la sesión
    String phoneNumber;         // Número de teléfono
    String sessionToken;        // Token de sesión
    boolean isActive;          // Estado de la sesión
    LocalDateTime lastConnection; // Última conexión
    LocalDateTime createdAt;    // Fecha de creación
    LocalDateTime updatedAt;    // Fecha de actualización
}
```

### Estados (Enum)
```java
enum Estado {
    PENDIENTE,
    PAGADO,
    VENCIDO
}
```

## Notas de Autenticación

- La mayoría de los endpoints requieren autenticación mediante token
- Las credenciales se envían en el header de la petición
- El token se obtiene al hacer login y debe incluirse en las peticiones subsecuentes

## Tecnologías Utilizadas

- Spring Boot
- Spring Security
- JPA/Hibernate
- PostgreSQL
- WhatsApp Web API
