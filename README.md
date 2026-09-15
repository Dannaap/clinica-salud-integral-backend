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
│   │   │   ├── citas/         → (Sprint 2)
│   │   │   ├── atenciones/    → (Sprint 3)
│   │   │   ├── reportes/      → (Sprint 4)
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

---

## 📋 Módulos por Sprint

| Sprint | Módulos |
|---|---|
| Sprint 1 | auth, usuarios, pacientes |
| Sprint 2 | citas, turnos, medicos, especialidades |
| Sprint 3 | atenciones, historias, recetas |
| Sprint 4 | notificaciones, reportes |

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

---

## 🚀 Cómo levantar el backend localmente

### Requisitos previos

- Java 17+
- Maven (o usar el wrapper mvnw)
- Cuenta en Supabase (gratuita)

### Ejecutar

./mvnw spring-boot:run

El servidor corre en http://localhost:8080.

---

## 🔐 Endpoints principales

| Método | Endpoint | Descripción |
|---|---|---|
| POST | /api/auth/login | Login con JWT |
| GET | /api/pacientes | Listar pacientes |
| GET | /api/pacientes/{dni} | Buscar por DNI |
| POST | /api/pacientes | Crear paciente |
| PUT | /api/pacientes/{id} | Actualizar paciente |
| DELETE | /api/pacientes/{id} | Eliminar paciente |
| GET | /api/usuarios | Listar usuarios (Admin) |
| POST | /api/usuarios | Crear usuario (Admin) |

---

## 🧪 Ejecutar tests

./mvnw test

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
