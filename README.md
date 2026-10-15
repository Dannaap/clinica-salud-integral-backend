# Backend — Clínica Salud Integral S.A.C.

API REST construida con **Spring Boot 3 + Java 17** para la intranet de la **Clínica Salud Integral S.A.C.**

Sistema interno para la gestión de citas y atenciones médicas que centraliza la operación de la clínica en una sola plataforma.

---

## ¿Qué es esta intranet?

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

## Objetivo

Centralizar la gestión de citas y atenciones médicas en una sola plataforma interna para eliminar los siguientes problemas operativos:

- Elevados tiempos de espera en recepción al programar o confirmar citas.
- Alta tasa de inasistencias por falta de recordatorios automáticos.
- Errores y duplicidad en historias clínicas por manipulación de documentos físicos.

### Problema general

> Deficiencia en los procesos operativos y limitaciones en la calidad de atención al paciente, ocasionadas por la ausencia de un sistema centralizado para la gestión eficiente de citas y atenciones médicas.

---

## Stack tecnológico

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

## Estructura del proyecto

```text
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
├── database/
│   └── schema-and-demo-data.sql   → Script de base de datos y datos de demo
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
