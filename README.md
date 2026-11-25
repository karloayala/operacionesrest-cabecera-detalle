# operacionesrest-cabecera-detalle

## Modelo Lógico
![Modelo Lógico](modelo_logico/ucos-modelo_logico.svg)

## Diagrama de Flujo
![Diagrama de Flujo](modelo_logico/ucos-diagrama_de_flujo.svg)

## Migraciones de base de datos

Antes de desplegar el estado `PAGADO_AL_CONTADO` en una base PostgreSQL existente,
ejecuta `database/migrations/postgresql/2026-11-25-add-pagado-al-contado.sql`.
Hibernate no actualiza automáticamente las restricciones `CHECK` existentes.