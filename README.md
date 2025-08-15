Gestor de Tareas con Java y JDBC

Este proyecto es una implementación de un **CRUD de tareas** en Java usando **JDBC** y **PostgreSQL** como base de datos.  
Está diseñado con un patrón **DAO (Data Access Object)** para separar la lógica de acceso a datos de la lógica de negocio.

Características

- Crear, listar, buscar, actualizar y eliminar tareas (soft delete).
- Restaurar tareas eliminadas.
- Manejo de transacciones con commit y rollback.
- Uso de `PreparedStatement` para prevenir inyecciones SQL.
- Configuración externa de la conexión mediante archivo de propiedades.
