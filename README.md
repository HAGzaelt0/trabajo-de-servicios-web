# Gestion de productos

API REST de practica con Java 25 (LTS), Spring Boot, Spring Data JPA, Hibernate, PostgreSQL y Flyway.

## Requisitos

- JDK 25 (LTS)
- Maven 3.9 o posterior
- Docker Desktop, o PostgreSQL 16 instalado localmente
- Postman (opcional; se incluye una coleccion importable)

## Ejecutar

Desde la carpeta del proyecto, inicia PostgreSQL:

```powershell
docker compose up -d
```

Las credenciales locales de ejemplo son `postgres` / `postgres`. Para otra instalacion, configura `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` como variables de entorno. La base `gestion_productos` debe existir; Flyway crea sus tablas al arrancar.

Ejecuta pruebas y aplicacion:

```powershell
mvn test
mvn spring-boot:run
```

La API queda en `http://localhost:8080`. Hibernate usa `ddl-auto=validate`: verifica el esquema aplicado por Flyway y no crea ni modifica tablas por su cuenta.

## Endpoints

| Metodo | Ruta | Uso |
| --- | --- | --- |
| GET | `/api/categorias` | Listar categorias |
| POST | `/api/categorias` | Crear categoria |
| GET | `/api/proveedores` | Listar proveedores |
| POST | `/api/proveedores` | Crear proveedor |
| GET | `/api/productos` | Listar productos con sus relaciones |
| GET | `/api/productos/{id}` | Buscar producto |
| POST | `/api/productos` | Crear producto relacionado |
| PUT | `/api/productos/{id}` | Actualizar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |
| GET | `/api/productos/categoria/{categoriaId}` | Filtrar por categoría |
| GET | `/api/productos/etiqueta/{etiquetaId}` | Filtrar por etiqueta |
| GET / POST | `/api/etiquetas` | Listar o crear etiquetas |
| POST | `/api/productos/{productoId}/etiquetas/{etiquetaId}` | Asociar etiqueta |
| DELETE | `/api/productos/{productoId}/etiquetas/{etiquetaId}` | Eliminar solo la asociación |

Los POST aceptan JSON. Para crear un producto, usa los identificadores existentes `categoriaId` y `proveedorId`; las respuestas incluyen tambien los nombres asociados. En la base, `proveedor_id` admite nulos para conservar productos preexistentes al aplicar V3, aunque el endpoint exige proveedor al crear productos nuevos.

Importa `postman/gestion-productos.postman_collection.json` para la práctica guiada y `postman/gestion-productos-lab1.postman_collection.json` para el Laboratorio Uno. Ambas colecciones guardan los IDs necesarios para ejecutar los flujos en orden.

## Migraciones

- `V1__crear_tablas.sql`: categoria, producto y FK a categoria.
- `V2__agregar_descripcion_producto.sql`: agrega descripcion al producto.
- `V3__agregar_proveedor.sql`: crea proveedor y agrega su FK e indice en producto.
- `V4__crear_etiquetas.sql`: crea etiquetas y la tabla intermedia `producto_etiqueta`.

No edites migraciones ya aplicadas. Flyway conserva el historial en `flyway_schema_history`.

## Pruebas

`mvn test` ejecuta las migraciones y pruebas HTTP de CRUD, consultas por categoría y etiquetas sobre H2 en modo PostgreSQL. Para ejecutar los dos productos de prueba del Laboratorio Uno contra PostgreSQL, inicia la API y ejecuta `powershell -ExecutionPolicy Bypass -File scripts/probar-laboratorio1.ps1`.
