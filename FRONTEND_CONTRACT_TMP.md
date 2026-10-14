# Contrato Frontend ↔ Backend — Client-G

## 0. Configuración base

| Concepto | Valor |
|---|---|
| Base URL | `http://localhost:8080/client-gest` |
| Auth | `Authorization: Bearer <jwtToken>` |
| Content-Type | `application/json` (excepto 1 endpoint, ver §3.3) |
| Vigencia del token | **3 horas**, sin refresh |
| Origen CORS permitido | **solo `http://localhost:4200`** |

**Crítico — CORS:** `SecurityConfiguration.corsConfigurationSource()` registra únicamente `http://localhost:4200` con `allowCredentials(true)`. El `@CrossOrigin("*")` de los controllers **no te salva**, porque la cadena de Spring Security aplica el bean. Si el frontend corre en Vite (`5173`), React (`3000`) o cualquier otro puerto, **todas las peticiones fallan por CORS**. O el frontend usa el puerto 4200, o hay que agregar el puerto en `SecurityConfiguration.java:171`.

---

## 1. Tipos de datos que el frontend debe declarar

```ts
// ---------- Auth ----------
interface UserDTO {          // request de register y login
  id?: number | null;
  username: string;
  password: string;
  authorities?: string;      // "ADMIN" o "ACCOUNTANT", separador ";" si son varias
}

interface TokenDTO {         // response del login
  jwtToken: string;
  id: number;                // ⚠️ es el id de USER, no el de Accountant
  authorities: string;       // string unido por ";" → hay que hacer .split(';')
}

// ---------- Accountant ----------
interface AccountantDTO {
  firstName: string;
  lastName: string;
  username: string;
  password: string;
}

// ---------- Client ----------
interface ClientDTO {              // POST /clients/register (sin id)
  firstName: string;
  lastName: string;
  ruc: string;                     // string, NO number (11 dígitos, inicia en 10/20/15)
  razonSocial: string;
  automaticPaymentDay: number;     // 1–31
  automaticPaymentAvailable: boolean;
}

interface ClientUpdateDTO extends ClientDTO {
  id: number;
}

interface ClientDeleteDTO {
  id: number;
  isActive: boolean;               // false = eliminar, true = recuperar
}

interface ClientListDTO {          // lo que devuelven los GET
  id: number;
  ruc: string;
  razonSocial: string;
  completeName: string;            // ya viene armado: "Apellido, Nombre"
  numberPendingPayments: number;
  numberLatePayments: number;
  automaticPaymentDay: number;
  automaticPaymentAvailable: boolean;
  createdAt: string;               // ISO sin zona: "2026-10-13T02:34:56.123456"
}

// ---------- Payment ----------
type PaymentMethod = 'TRANSFERENCIA' | 'EFECTIVO';
type Currency      = 'PEN' | 'US' | 'EU';       // ⚠️ "US" y "EU", no "USD"/"EUR"
type PaymentStatus = 'PENDIENTE' | 'PAGADO' | 'RETRASO';

interface PaymentDTO {             // POST /payments/register
  paymentMethod: PaymentMethod;
  currency: Currency;
  status: PaymentStatus;
  amount: number;                  // BigDecimal → llega como número JSON
  paymentDate: string;             // "YYYY-MM-DD" (LocalDate, sin hora)
  paymentReason: string;
  clientId: number;
}

interface PaymentListDTO {         // GET /payments/{clientId}
  id: number;
  paymentMethod: PaymentMethod;
  currency: Currency;
  status: PaymentStatus;
  amount: number;
  paymentDate: string;             // "YYYY-MM-DD"
  paymentReason: string;
  createdAt: string;               // ISO datetime
}

// ---------- Logs ----------
interface LoginLogDTO {
  id: number | null;
  dateTime: string;
  username: string;
}
```

**Fechas:** `LocalDate` → `"2026-10-13"`. `LocalDateTime` → `"2026-10-13T02:34:56.123456"` **sin zona horaria ni `Z`**. El frontend no debe pasarlo por `new Date(...)` asumiendo UTC, porque desplazaría las horas; conviene parsearlo como hora local.

---

## 2. Endpoints públicos (sin token)

### 2.1 `POST /client-gest/users/register`
Body: `UserDTO` → `{username, password, authorities: "ADMIN"}`. Respuesta **201** con el `UserDTO` incluyendo el `id` generado y **la contraseña en texto plano**. Las autoridades deben existir previamente (`ADMIN` y `ACCOUNTANT` se siembran al arrancar en `ClientGApplication.java`); si mandas un nombre inexistente, se crea el usuario **sin ninguna autoridad** y quedará inutilizable, sin error.

### 2.2 `POST /client-gest/accountants/register`
Body: `AccountantDTO`. Respuesta **201** con el mismo DTO (password en plano). Asigna `ACCOUNTANT` automáticamente y crea `User` + `Accountant`.
- Username repetido → **406** `ErrorMessage` (`KeyRepeatedDataExeception`).
- **No devuelve el `id`** → tras registrar hay que ir al login.

### 2.3 `POST /client-gest/users/login`
Body: `{username, password}`. Respuesta **200** `TokenDTO`.

El frontend debe guardar `jwtToken`, hacer `authorities.split(';')` para el control de rutas, y mandar el token en cada petición.

**Importante:** credenciales malas lanzan `BadCredentialsException`, que **no tiene handler** en `GlobalExceptionHandler`. Llega un **401/403 con body vacío o el formato por defecto de Spring**, no un `ErrorMessage`. El frontend tiene que manejar ese caso a mano con un mensaje propio.

---

## 3. Endpoints autenticados

Todo lo que no está en el whitelist exige token (`anyRequest().authenticated()`).

### 3.1 `GET /client-gest/clients`
Lista los clientes **del contador logueado** (se resuelve por el username del token, no se manda ningún id). Respuesta **200** `ClientListDTO[]`.

> ⚠️ **Si el contador no tiene clientes, el backend lanza `RuntimeException("No hay clientes registrados")` → HTTP 500.** El frontend recibiría un error de servidor en el caso normalísimo de "cuenta nueva sin clientes". Esto hay que arreglarlo en el backend (`ClientServiceImpl.java:98`): debe devolver `[]`. Mientras no se arregle, el frontend tendría que interpretar el 500 como lista vacía, lo cual es muy frágil.

### 3.2 `POST /client-gest/clients/register`
Body: `ClientDTO`. Respuesta **201** con el eco del DTO.

> ⚠️ **La respuesta no incluye el `id` del cliente creado.** El frontend está obligado a re-pedir `GET /clients` después de cada registro para obtener el id.
> ⚠️ RUC o razón social duplicados lanzan `RuntimeException` → **500**, sin `ErrorMessage`.
> ⚠️ Aquí **no se valida el RUC** (solo se valida en el update), así que el frontend debe validarlo él mismo: 11 dígitos, solo numérico, empieza en `10`, `20` o `15`.

### 3.3 `PUT /client-gest/clients/is-active` — eliminar / recuperar
Body: `ClientDeleteDTO` → `{id, isActive}`.

> ⚠️ **Devuelve texto plano, no JSON:** `"Cliente eliminado exitosamente"` o `"Cliente recuperado exitosamente"`. En Angular hay que llamarlo con `{ responseType: 'text' }`; con el default JSON reventaría al parsear. Lo mismo aplica a `fetch` (`res.text()`, no `res.json()`).

### 3.4 `PUT /client-gest/clients/update`
Body: `ClientUpdateDTO` **completo** (no es un PATCH: los campos que omitas se escriben como `null`). Respuesta **200** con el eco.

Validaciones que devuelven **406** con `ErrorMessage`:
- RUC con caracteres no numéricos → `"Solo ingrese dígitos numéricos"`
- RUC de longitud distinta de 11 → `"El RUC debe tener 11 digitos"`
- RUC que no empieza en 10/20/15 → `"El RUC debe iniciar con 10, 20 o 15"`

Si el `id` no existe → `NullPointerException` → **500**.

### 3.5 `GET /client-gest/clients/{clientId}`
Respuesta **200** `ClientListDTO`.

> ⚠️ **No valida pertenencia:** cualquier usuario autenticado puede leer el cliente de otro contador. El frontend no debe apoyarse en el backend para el aislamiento de datos.

### 3.6 `POST /client-gest/payments/register`
**Requiere autoridad `ACCOUNTANT`** (es el único endpoint con restricción por rol). Body: `PaymentDTO`.

> ⚠️ Devuelve **200**, no 201 (inconsistente con los otros registros).
> ⚠️ Devuelve el eco del DTO **sin el `id`** del pago creado.
> ⚠️ Si `clientId` no existe, guarda el pago con `client = null` **sin lanzar error**, quedando huérfano e invisible.

### 3.7 `GET /client-gest/payments/{clientId}`
Respuesta **200** `PaymentListDTO[]` (solo los `isActive = true`). Devuelve `[]` correctamente cuando no hay pagos — este sí se comporta bien.

### 3.8 `GET /client-gest/logins/{user-id}` — **ROTO, no lo integres aún**

Dos fallas:
1. `@GetMapping("/{user-id}")` con `@PathVariable Long userId` — los nombres no coinciden, Spring no puede resolver el argumento → **500 en toda llamada**. Hay que poner `@PathVariable("user-id")` o renombrar la plantilla a `{userId}`.
2. `LoginLogServiceImpl.java:34` construye cada DTO con `id = null` y `dateTime = LocalDateTime.now()` en lugar de leer el `login_at` real. Aunque se arregle el path, el frontend recibiría N registros idénticos con la hora actual, sin valor informativo.

---

## 4. Las tres formas de error que el frontend debe manejar

El backend no tiene un formato de error único. Hay que escribir un interceptor que cubra los tres:

**A) `ErrorMessage` — 404 y 406** (excepciones controladas):
```json
{
  "status": 406,
  "exception": "InvalidDataRangeException",
  "message": "El RUC debe tener 11 digitos",
  "requestDescription": "uri=/client-gest/clients/update",
  "timestamp": "2026-10-13T02:34:56.123"
}
```
Solo aparece en: `IncompleteDataException` (406), `InvalidDataRangeException` (406), `ResourceNotFoundException` (404), `KeyRepeatedDataExeception` (406).

**B) Error por defecto de Spring — 500** (toda `RuntimeException` suelta: lista vacía, RUC duplicado, id inexistente, token expirado):
```json
{ "timestamp": "...", "status": 500, "error": "Internal Server Error", "path": "/client-gest/clients" }
```
El mensaje real **no viaja**, así que el frontend no puede mostrarle al usuario por qué falló. Mensaje genérico obligatorio.

**C) 401 / 403 de Spring Security** — body vacío o mínimo. Acción: borrar el token y redirigir al login.

---

## 5. Manejo de sesión: la trampa del token expirado

`JwtRequestFilter` llama `extractUsername(token)` sin `try/catch`. Con un token vencido, jjwt lanza `ExpiredJwtException` **dentro del filtro**, que escapa de la cadena → el frontend recibe un **500**, no un 401.

Por eso el frontend **debe decodificar el claim `exp` del JWT en el cliente** y cerrar sesión proactivamente antes de que venza (o al detectar el 500 en un endpoint que antes funcionaba). El token dura 3 h y **no hay endpoint de refresh ni de logout**.

Claims disponibles al decodificar el JWT (útiles para la UI):
- `sub` → username
- `authorities` → array de strings
- `user_id` → id del User
- `exp`, `iat`

---

## 6. Lo que el backend **todavía no ofrece** y el frontend va a necesitar

Esto es lo que bloquearía pantallas completas:

| Falta | Impacto en el frontend |
|---|---|
| **Cambiar el estado de un pago** (`PENDIENTE` → `PAGADO`) | El task automático (`PaymentTask`) genera pagos `PENDIENTE` cada día a las 00:00 para los clientes cuyo `automaticPaymentDay` es hoy. **Nada los pasa a `PAGADO` jamás.** Es el hueco funcional más grave: no se puede construir la pantalla de cobranza. Hace falta un `PUT /payments/{id}/status`. |
| **Perfil del contador logueado** | `TokenDTO` solo da `id` y `authorities`. No hay forma de obtener `firstName`/`lastName` → no se puede mostrar el nombre en el header. Hace falta `GET /accountants/me`. |
| **Listar clientes eliminados** | `GET /clients` filtra por `isActive = true`. Existe el "recuperar cliente" (`PUT /is-active` con `true`) pero **no hay manera de ver qué clientes están eliminados**, así que la función es inalcanzable desde la UI. Hace falta `GET /clients?isActive=false`. |
| **Transición a `RETRASO`** | `ClientListDTO.numberLatePayments` siempre será `0`: ningún código asigna el estado `RETRASO`. La columna "en retraso" de la tabla será permanentemente cero. |
| Eliminar / anular un pago | `Payment` tiene `isActive` y `deletedAt`, pero no hay endpoint que los use. |
| Obtener un pago individual | Solo existe el listado por cliente. |
| Paginación, filtros y orden | Los listados devuelven todo sin parámetros. Toda la paginación y el ordenamiento tienen que ser en memoria en el frontend. |
| Cambiar contraseña | No existe. |
| Endpoints de `ADMIN` | La autoridad `ADMIN` se crea al arrancar pero **ningún endpoint la usa**. Ver el punto siguiente. |

**Un `ADMIN` no puede usar la aplicación.** `ClientServiceImpl` resuelve el contador con `accountantRepository.findByUser_username(...)`, que para un admin devuelve `null`. Resultado: `GET /clients` → lista vacía → **500**; y `POST /clients/register` guarda el cliente con `accountant = null`, invisible para siempre. En la práctica **el frontend solo debe permitir el login de cuentas `ACCOUNTANT`**, o bien se implementa el flujo de admin en el backend.

---

## 7. Validaciones que el frontend debe hacer porque el backend no las hace

A pesar de que `spring-boot-starter-validation` está en el `pom.xml`, **ningún DTO tiene anotaciones** (`@NotNull`, `@Size`, etc.) y ningún controller tiene `@Valid`. Todo campo omitido entra como `null` y se persiste así. El frontend es la única barrera:

- **RUC:** 11 dígitos numéricos, prefijo `10`/`20`/`15` (replicar la regla del update también en el create).
- **`automaticPaymentDay`:** el backend acepta cualquier `Integer`. El task compara contra `LocalDate.now().getDayOfMonth()`, así que un `30` o `31` **no se dispara en febrero** y un valor fuera de 1–31 nunca se dispara. Recomiendo limitar el selector a **1–28**.
- **`amount`:** debe ser > 0 y con 2 decimales; el backend acepta negativos y `null`.
- **Campos obligatorios:** `firstName`, `lastName`, `razonSocial`, `username`, `password`, `paymentReason` — todos aceptan `null` o `""`.
- **Longitud de password:** sin mínimo en el backend.

---

## 8. Resumen operativo de rutas

| Método | Ruta | Auth | Request | Response | Nota |
|---|---|---|---|---|---|
| POST | `/users/register` | — | `UserDTO` | 201 `UserDTO` | |
| POST | `/users/login` | — | `{username,password}` | 200 `TokenDTO` | 401 sin formato |
| POST | `/accountants/register` | — | `AccountantDTO` | 201 `AccountantDTO` | 406 si username repetido |
| GET | `/clients` | token | — | 200 `ClientListDTO[]` | **500 si está vacío** |
| POST | `/clients/register` | token | `ClientDTO` | 201 `ClientDTO` | sin `id`; 500 si duplicado |
| PUT | `/clients/is-active` | token | `ClientDeleteDTO` | 200 **texto plano** | `responseType:'text'` |
| PUT | `/clients/update` | token | `ClientUpdateDTO` | 200 `ClientUpdateDTO` | 406 en RUC inválido |
| GET | `/clients/{clientId}` | token | — | 200 `ClientListDTO` | sin control de dueño |
| POST | `/payments/register` | `ACCOUNTANT` | `PaymentDTO` | **200** `PaymentDTO` | sin `id` |
| GET | `/payments/{clientId}` | token | — | 200 `PaymentListDTO[]` | OK |
| GET | `/logins/{user-id}` | token | — | **500 siempre** | roto |

---

## Lo que arreglaría en el backend antes de empezar el frontend

Por orden de impacto:

1. **`GET /clients` debe devolver `[]`** en lugar de lanzar excepción — hoy rompe el caso de uso más básico.
2. **Agregar el puerto real del frontend al CORS** (o usar 4200).
3. **Devolver los `id`** en las respuestas de `POST /clients/register` y `/payments/register`, para evitar re-fetches.
4. **Endpoint para cambiar el estado de un pago** — sin él no hay módulo de cobranza.
5. **`try/catch` en `JwtRequestFilter`** para que un token vencido dé 401 y no 500.
6. **Arreglar `GET /logins/{user-id}`** (el `@PathVariable` y el `login_at` real).
7. **Que `PUT /clients/is-active` devuelva JSON** (`{"message": "..."}`) para uniformar el cliente HTTP.
8. **Handler para `BadCredentialsException`** y reemplazar los `RuntimeException` sueltos por las excepciones del paquete `exceptions`, para que el frontend siempre reciba un `ErrorMessage`.
