# 🔧 Backend — Clínica Salud Integral S.A.C.

API REST construida con **Spring Boot 3 + Java 17** para la intranet de la **Clínica Salud Integral S.A.C.**

Sistema interno para la gestión de citas y atenciones médicas que centraliza la operación de la clínica en una sola plataforma.

---

## 🏥 ¿Qué es esta intranet?

Este proyecto es una **intranet con arquitectura cliente-servidor**, compuesta por dos repositorios:

| Repositorio | Rol | Deploy |
|---|---|---|
| clinica-salud-integral-backend | API REST (Spring Boot) | Railway |
| clinica-salud-integral-frontend | Interfaz SPA (Angular) | Vercel |

**Intranet** significa que es un sistema **privado, solo para el personal de la clínica**. Los pacientes NO acceden a este sistema.

### ¿Quién usa la intranet?

| Rol | Acciones |
|---|---|
| Administrador | Gestión de usuarios, turnos médicos y reportes |
| Recepción | Registro de pacientes, agenda, reprogramación y cancelación de citas |
| Médico | Agenda propia, registro de atenciones médicas y recetas digitales |

---

## 🎯 Objetivo

Centralizar la gestión de citas y atenciones médicas en una sola plataforma interna para eliminar los siguientes problemas operativos:

- ⏱️ Elevados tiempos de espera en recepción al programar o confirmar citas.
- 🔔 Alta tasa de inasistencias por falta de recordatorios automáticos.
- 🗂️ Errores y duplicidad en historias clínicas por manipulación de documentos físicos.

### Problema general

> Deficiencia en los procesos operativos y limitaciones en la calidad de atención al paciente, ocasionadas por la ausencia de un sistema centralizado para la gestión eficiente de citas y atenciones médicas.

---

## 🛠️ Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3 |
| Seguridad | Spring Security + JWT |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | Supabase (PostgreSQL en la nube) |
| Build | Maven |
| Tests | JUnit 5 + Mockito (TDD) |
| Deploy | Railway |

---

## 📂 Estructura del proyecto
```
clinica-salud-integral-backend/
├── src/
│   ├── main/
│   │   ├── java/pe/saludintegral/clinica/
│   │   │   ├── ClinicaApplication.java
│   │   │   ├── config/        → Security, JWT, CORS
│   │   │   ├── security/      → Filtros JWT, UserDetails
│   │   │   ├── auth/          → Login y autenticación
│   │   │   ├── usuarios/      → Gestión de usuarios
│   │   │   ├── pacientes/     → Gestión de pacientes
│   │   │   ├── especialidades/→ Catálogo de especialidades
│   │   │   ├── medicos/       → Perfiles de médicos
│   │   │   ├── turnos/        → Horarios y disponibilidad
│   │   │   ├── citas/         → Reserva, reprogramación y cancelación
│   │   │   ├── atenciones/    → Historia clínica y recetas
│   │   │   ├── notificaciones/→ Confirmaciones y recordatorios internos
│   │   │   ├── reportes/      → Reportes PDF y Excel
│   │   │   └── common/        → Excepciones, utilidades
│   │   └── resources/
│   │       ├── application.yml
│   │       └── data.sql
│   └── test/
│       └── java/pe/saludintegral/clinica/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```
---

## 📋 Módulos por Sprint

| Sprint | Módulos |
|---|---|
| Sprint 1 | auth, usuarios, pacientes |
| Sprint 2 | citas, turnos, medicos, especialidades |
| Sprint 3 | atenciones, historias, recetas |
| Sprint 4 | notificaciones, reportes |

Los módulos de las cuatro iteraciones están implementados en este backend. El alcance funcional sigue los requerimientos RF-01 a RF-09 del proyecto; las pruebas se ejecutan con una base H2 aislada y no modifican Supabase.

---

## 📅 Sprints y HU (según backlog priorizado)

| Sprint | Duración | HU | Prioridad | Días |
|---|---|---|---|---|
| Sprint 1 | 5 días | H.U.1 — Acceso al sistema | M | 2 |
| Sprint 1 | 5 días | H.U.2 — Administrar pacientes | M | 3 |
| Sprint 2 | 11 días | H.U.3 — Administrar turnos médicos | M | 3 |
| Sprint 2 | 11 días | H.U.4 — Reservar citas médicas | M | 5 |
| Sprint 2 | 11 días | H.U.5 — Reprogramar / cancelar citas | M | 3 |
| Sprint 3 | 5 días | H.U.6 — Registrar atención médica | S | 5 |
| Sprint 4 | 7 días | H.U.7 — Enviar notificaciones automáticas | S | 3 |
| Sprint 4 | 7 días | H.U.8 — Generar reportes operativos | C | 3 |
| Sprint 4 | 7 días | H.U.9 — Administrar cuenta | C | 1 |

---

## 🚀 Plan de Lanzamientos (Release Plan)

| Release | Sprints incluidos | Contenido |
|---|---|---|
| Release 1 | Sprint 1 + Sprint 2 | Núcleo funcional: autenticación, pacientes, turnos y citas |
| Release 2 | Sprint 3 | Módulo clínico: consultas e historia clínica electrónica |
| Release 3 | Sprint 4 | Notificaciones, reportes PDF/Excel y cierre del proyecto |

---

## 🗄️ Base de datos — Supabase

Este proyecto usa **Supabase** como base de datos PostgreSQL en la nube.

### Configurar conexión

En application.yml:

spring:
  datasource:
    url: jdbc:postgresql://db.xxxxxxxxxxxxx.supabase.co:5432/postgres
    username: postgres
    password: TU_PASSWORD_DE_SUPABASE
  jpa:
    hibernate:
      ddl-auto: update

jwt:
  secret: clave-secreta-super-segura-2026
  expiration: 86400000

### Recomendación de seguridad

Nunca subas las credenciales reales al repositorio. Usa variables de entorno en producción.

### Datos ficticios para demostración

El script `database/schema-and-demo-data.sql` agrega especialidades, médicos, pacientes y citas ficticias para probar los módulos. En Supabase, abre **SQL Editor**, pega el contenido y ejecuta la consulta. Al volver a ejecutarlo, elimina y repone únicamente las filas de demostración identificadas por sus DNIs y emails `*.demo@clinica.test`; conserva el administrador `admin@clinica.test` y otros registros.

Las contraseñas nuevas deben tener un mínimo de 8 caracteres y se almacenan con BCrypt.

---

## 🚀 Cómo levantar el backend localmente

### Requisitos previos

- Java 17+
- Maven (o usar el wrapper mvnw)
- Cuenta en Supabase (gratuita)

### Ejecutar

./mvnw spring-boot:run

El servidor corre en http://localhost:8080.

### Crear el primer administrador de prueba

La tabla `usuarios` no incluye una cuenta inicial. Para crear una cuenta ficticia una sola vez, inicia el backend con estas variables en PowerShell:

```powershell
$env:APP_BOOTSTRAP_ADMIN_ENABLED = "true"
$env:APP_BOOTSTRAP_ADMIN_DNI = "00000000"
$env:APP_BOOTSTRAP_ADMIN_NOMBRES = "Admin"
$env:APP_BOOTSTRAP_ADMIN_APELLIDOS = "Prueba"
$env:APP_BOOTSTRAP_ADMIN_EMAIL = "admin@clinica.test"
$env:APP_BOOTSTRAP_ADMIN_PASSWORD = Read-Host "Contraseña temporal (mínimo 8 caracteres)"
& .\mvnw.cmd spring-boot:run
```

El inicializador solo crea el administrador si `usuarios` está vacía y cifra su contraseña con BCrypt. Usa datos ficticios. Después de confirmar el inicio de sesión, detén el proceso con `Ctrl+C`, desactiva `APP_BOOTSTRAP_ADMIN_ENABLED` y elimina la variable de contraseña antes de iniciar el backend normalmente:

Las contraseñas nuevas deben tener al menos 8 caracteres; el backend las almacena usando BCrypt.

```powershell
$env:APP_BOOTSTRAP_ADMIN_ENABLED = "false"
Remove-Item Env:APP_BOOTSTRAP_ADMIN_PASSWORD -ErrorAction SilentlyContinue
& .\mvnw.cmd spring-boot:run
```

---

## 🔐 Endpoints implementados

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/auth/login` | Iniciar sesión y obtener JWT |
| GET, POST | `/api/usuarios` | Listar y crear usuarios (ADMIN) |
| GET, PUT, DELETE | `/api/usuarios/{id}` | Consultar, editar y eliminar usuario (ADMIN) |
| GET, POST | `/api/pacientes` | Listar y registrar pacientes (ADMIN, RECEPCION) |
| GET | `/api/pacientes/dni/{dni}` | Buscar paciente por DNI |
| PUT, DELETE | `/api/pacientes/{id}` | Editar y eliminar paciente |
| GET, POST, PUT, DELETE | `/api/especialidades` | Administrar especialidades (escritura ADMIN) |
| GET, POST, PUT, DELETE | `/api/medicos` | Administrar perfiles médicos (escritura ADMIN) |
| GET | `/api/turnos?medicoId={id}&desde={fecha-hora}&hasta={fecha-hora}` | Consultar agenda y disponibilidad; MEDICO solo consulta su agenda |
| POST, PUT, DELETE | `/api/turnos` y `/api/turnos/{id}` | Crear, editar y eliminar turnos (ADMIN) |
| GET, POST | `/api/citas` | Consultar agenda y reservar una cita |
| PUT | `/api/citas/{id}/reprogramar` | Reprogramar una cita reservada |
| DELETE | `/api/citas/{id}` | Cancelar una cita reservada |
| POST | `/api/atenciones` | Registrar atención con diagnóstico y código CIE-10 (médico asignado/ADMIN) |
| GET | `/api/atenciones/paciente/{pacienteId}` | Consultar historia clínica (MEDICO, ADMIN) |
| GET, POST | `/api/atenciones/{atencionId}/recetas` | Consultar y emitir recetas digitales |
| GET | `/api/notificaciones` | Consultar confirmaciones y recordatorios internos (ADMIN, RECEPCION) |
| GET | `/api/reportes/diario/{yyyy-MM-dd}/pdf` | Descargar reporte diario PDF |
| GET | `/api/reportes/diario/{yyyy-MM-dd}/excel` | Descargar reporte diario Excel `.xlsx` |
| GET, PUT | `/api/cuenta` | Consultar y actualizar el perfil propio |
| PUT | `/api/cuenta/contrasena` | Cambiar contraseña verificando la actual |

Las reservas, reprogramaciones y cancelaciones generan notificaciones asociadas a la cita; se crea automáticamente un recordatorio para citas del día siguiente. Las citas reservadas cuyo turno termina sin atención se marcan como `AUSENTE` para el índice de ausentismo. Por defecto, las notificaciones quedan registradas en la intranet. Para habilitar envío real por correo, configura SMTP mediante variables de entorno y activa la opción:

```powershell
$env:APP_NOTIFICATIONS_EMAIL_ENABLED = "true"
$env:SPRING_MAIL_HOST = "smtp.example.test"
$env:SPRING_MAIL_PORT = "587"
$env:SPRING_MAIL_USERNAME = "usuario-smtp"
$env:SPRING_MAIL_PASSWORD = "secreto-smtp"
$env:SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH = "true"
$env:SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE = "true"
$env:APP_NOTIFICATIONS_EMAIL_FROM = "no-reply@example.test"
```

No guardes credenciales SMTP en el repositorio. Sin SMTP habilitado, el sistema mantiene la notificación interna y registra el estado de entrega.

El reporte diario incluye citas atendidas, ausentes, canceladas, porcentaje de ausentismo (`ausentes / (atendidas + ausentes)`) y el detalle de atención, diagnóstico CIE-10 y recetas.

---

## 🧪 Ejecutar tests

`./mvnw test` (Windows PowerShell desde la carpeta raíz: `mvnw.cmd test`)

---

## ☁️ Despliegue en Railway

### Requisitos

- Cuenta en Railway (railway.app)
- Repositorio en GitHub conectado

### Pasos generales

1. Crear un nuevo proyecto en Railway
2. Conectar el repositorio de GitHub
3. Configurar las variables de entorno (Supabase URL, usuario, password, JWT secret)
4. Railway detecta automáticamente que es un proyecto Maven y despliega
5. Obtener la URL pública del backend para conectar con el frontend

### Plan gratuito

Railway ofrece $5 de crédito inicial. Después, aproximadamente $1/mes para mantener un servicio pequeño activo 24/7.

---

## 🔗 Repositorios relacionados

- **Backend (este repo):** clinica-salud-integral-backend
- **Frontend:** clinica-salud-integral-frontend

---

## 📄 Licencia

Proyecto académico — **Clínica Salud Integral S.A.C.**

MIT License © 2026
