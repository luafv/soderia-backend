# Sodería - Backend

API REST para la app de gestión de una sodería. La usa el repartidor desde el celular para llevar el recorrido del día: a qué clientes visita, cuántos sifones deja, cuántos vacíos retira y quién paga o queda debiendo.

Proyecto final de la Tecnicatura Universitaria en Programación (UTN).

## Tecnologías

- Java 21
- Spring Boot 4.1 (Web, Data JPA, Validation, Security)
- PostgreSQL (Supabase)
- Lombok
- springdoc-openapi (Swagger)
- Maven (se usa el wrapper, no hace falta instalarlo)

## Cómo levantarlo

Hace falta tener instalado el JDK 21 y la variable `JAVA_HOME` apuntando a él.

1. Clonar el repo.
2. Crear un archivo `.env` en la raíz del proyecto (al lado del `pom.xml`) con los datos de conexión a la base:

   ```
   DB_URL=jdbc:postgresql://<host>:<puerto>/postgres
   DB_USERNAME=<usuario>
   DB_PASSWORD=<contraseña>
   ```

   Los datos están en Supabase, en *Connect*. El `.env` está en el `.gitignore`, no se sube.

3. Correr:

   ```
   .\mvnw.cmd spring-boot:run      (Windows)
   ./mvnw spring-boot:run          (Linux/Mac)
   ```

Levanta en `http://localhost:8080`. Las tablas las crea Hibernate solo (`ddl-auto=update`), no hay que correr ningún script.

## Probar la API

- **Swagger:** http://localhost:8080/swagger-ui.html
- **Postman:** en la carpeta `postman/` está la colección con todos los requests y sus tests. Se importa desde Postman y se corre entera con *Run collection*. La carpeta de flujo completo arma un caso de punta a punta (producto, cliente, visita, entrega, pago).

Por ahora la seguridad está abierta (todos los endpoints son públicos). El login con roles está pendiente.

## Cómo funciona

La idea es que el cliente no hace pedidos. El repartidor pasa siempre los mismos días por los mismos clientes y en la puerta carga lo que dejó.

**Recorrido del día**

1. `POST /api/recorridos/iniciar`: se carga el stock con el que sale el camión y se generan las visitas del día, una por cada cliente activo que tiene ese día de entrega. Las visitas arrancan en `PENDIENTE`.
2. En cada casa, la visita se marca como entregada (`PATCH /api/visitas/{id}/entregar`) o ausente (`PATCH /api/visitas/{id}/ausente`). Al entregar se indican los sifones que se dejan, los vacíos que se retiran y, si paga en el momento, el pago.
3. `POST /api/recorridos/{fecha}/finalizar`: cierra el día. Las visitas que quedaron pendientes pasan a `AUSENTE`. Con el día cerrado ya no se pueden registrar entregas.
4. `GET /api/recorridos/{fecha}` da el resumen: entregas, ausentes, sifones entregados, cuánto se vendió y cuánto se cobró en efectivo y por transferencia.

**Algunas reglas**

- Cada visita trae un **sugerido**: la cantidad de sifones que el cliente tiene en su casa, que normalmente es lo que se le vuelve a dejar.
- `sifonesEnCasa` cuenta todos los envases del cliente, llenos y vacíos. Al entregar: `nuevo = antes + llenos dejados - vacíos retirados`. Si no se informan los vacíos, se toma el mínimo entre lo que se deja y lo que tenía.
- El stock del producto baja al entregar. Los vacíos no cuentan como stock.
- La **deuda se lleva en sifones**, no en plata, y se valoriza al precio actual. Si sube el precio, la deuda en pesos sube también. Un pago en efectivo se pasa a sifones al precio del día (puede dar con decimales).
- Medios de pago: `EFECTIVO` y `TRANSFERENCIA`.
- Cada entrega tiene un estado de pago calculado (`PAGADA`, `PARCIAL`, `IMPAGA`). Un pago hecho en una entrega cubre primero esa entrega. Un pago suelto cubre primero las entregas más viejas.
- Los clientes no se borran: se dan de baja y se pueden reactivar.

## Endpoints

| Recurso | Endpoints |
|---|---|
| Productos | `GET /api/productos`, `GET /api/productos/{id}`, `POST`, `PUT /{id}`, `PATCH /{id}/precio`, `PATCH /{id}/stock`, `DELETE /{id}` |
| Clientes | `GET /api/clientes` (filtros: `buscar`, `direccion`, `incluirInactivos`), `GET /{id}`, `GET /{id}/detalle`, `GET /dia/{dia}`, `POST`, `PUT /{id}`, `PATCH /{id}/sifones`, `PATCH /{id}/reactivar`, `DELETE /{id}` (baja) |
| Visitas | `GET /api/visitas?fecha=`, `GET /{id}`, `GET /cliente/{idCliente}`, `POST /generar`, `PATCH /{id}/entregar`, `PATCH /{id}/ausente`, `DELETE /{id}` |
| Pagos | `POST /api/pagos`, `GET /api/pagos/cliente/{idCliente}`, `DELETE /api/pagos/{id}` |
| Cuenta del cliente | `GET /api/clientes/{idCliente}/deuda`, `GET /api/clientes/{idCliente}/estado-cuenta` |
| Recorridos | `POST /api/recorridos/iniciar`, `POST /api/recorridos/{fecha}/finalizar`, `GET /api/recorridos/{fecha}`, `GET /api/recorridos?desde=&hasta=` |

El detalle de cada body y respuesta está en Swagger.

Errores: `400` si los datos son inválidos, `404` si no existe el recurso, `409` si la operación no se puede hacer en ese estado (por ejemplo, entregar una visita de un día ya cerrado).

## Estructura

```
src/main/java/com/soderia/soderia_backend/
├── config/          Security y Swagger
├── controllers/     separados por método HTTP (Get, Post, Put, Patch, Delete)
├── exceptions/      excepciones propias y el handler global
└── features/
    ├── dtos/        request y response (records)
    ├── mappers/     entidad <-> DTO
    ├── models/      entidades JPA y enums
    ├── repositories/
    └── services/    interfaces e implementaciones
```

## Pendiente

- Sifones sugeridos para cargar el camión al día siguiente.
- Deshacer una entrega con el día abierto, y que el admin pueda reabrir y corregir un día cerrado dejando registro.
- PDF con el estado de cuenta del cliente para mandar por WhatsApp.
- Login y roles (admin / repartidor).
