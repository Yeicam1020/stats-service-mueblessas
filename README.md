# Muebles SAS - Microservicio de Estadísticas (Clean Architecture)

Este microservicio es parte de la nueva arquitectura de **Muebles SAS**. Se encarga de **recibir, validar y procesar estadísticas de interacciones de clientes** usando tecnologías modernas como **Spring WebFlux**, **DynamoDB**, y **RabbitMQ**.  
Implementado bajo los principios de **Clean Architecture** utilizando el scaffold oficial de **Bancolombia**.

---

## Características principales

- Endpoint reactivo `POST /stats` con validación de hash MD5.
- Persiste estadísticas en **DynamoDB** si el hash es válido.
- Publica evento en **RabbitMQ** (`event.stats.validated`).
- Manejo de errores y logging estructurado con **Lombok**.
- Arquitectura desacoplada: `model`, `usecase`, `driven-adapters`, `entry-points`.
- Pruebas unitarias e integración con cobertura medido con JaCoCo

---

## Requisitos previos

- ✅ Java 17 o superior
- ✅ Gradle 7+
- ✅ Docker y Docker Compose
- ✅ Git
- ✅ Rancher Desktop (con `nerdctl` en lugar de Docker Desktop)

---


## Clonar el repositorio

```bash
https://github.com/Yeicam1020/stats-service-mueblessas.git
```

## Estructura del proyecto

```text
.
├── domain
│   └── model                 → Entidades y gateways
├── usecase                   → Casos de uso (lógica de negocio)
├── infrastructure
│   ├── driven-adapters       → Integraciones con DynamoDB, RabbitMQ
│   └── entry-points          → Routers y handlers (Spring WebFlux)
└── app-service               → Configuración general del microservicio
```
## Ejecución local

### Ejecución local con Rancher Desktop (`nerdctl`)

>  **Nota:** Se usó **Rancher Desktop** debido a restricciones de licencia con Docker Desktop. Por eso los contenedores se levantan con `nerdctl`.

### Levantar contenedores con nerdctl
```bash
nerdctl compose -f deployment/docker-compose.yml up -d

```
### Esto inicia los siguientes servicios:
```

DynamoDB Local en localhost:8000
 
RabbitMQ en localhost:5672

UI disponible en: http://localhost:15672

Usuario: guest

Contraseña: guest
```

### Verifica que están corriendo:
```
nerdctl ps
```
### Configurar perfil para DynamoDB local
```
aws configure --profile local-dynamodb
```

### Ingresa valores dummy:
```
AWS Access Key ID: dummy  
AWS Secret Access Key: dummy  
Region: us-east-1  
Output format: json
```

### Crear la tabla stats-table en DynamoDB local
```
aws dynamodb create-table --table-name stats-table --attribute-definitions AttributeName=timestamp,AttributeType=N --key-schema AttributeName=timestamp,KeyType=HASH --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 --endpoint-url http://localhost:8000 --profile local-dynamodb
```

### Consultar registros guardados
```
aws dynamodb scan --table-name stats-table --endpoint-url http://localhost:8000 --profile local-dynamodb

```

###  Ejecutar el microservicio:
```
./gradlew :app-service:bootRun

```
### Probar el endpoint
```bash

curl -X POST http://localhost:8080/stats \
-H "Content-Type: application/json" \
-d '{
    "totalContactoClientes": 250,
    "motivoReclamo": 25,
    "motivoGarantia": 10,
    "motivoDuda": 100,
    "motivoCompra": 100,
    "motivoFelicitaciones": 7,
    "motivoCambio": 8,
    "hash": "5484062a4be1ce5645eb414663e14f59"
}

```

### Si el hash es incorrecto
```
{
"code": 400,
"message": "Hash invalido",
"data": null
}
```
### Si el hash es válido

Publicación a RabbitMQ, el evento completo se publica en la cola:  
```
`event.stats.validated`
```

### Ver reporte de cobertura JaCoCo

Ejecutar pruebas y generar reporte de cobertura: 
```
./gradlew clean test jacocoRootReport
```

para poder observar las pruebas.
```
file:///C:/Users/yarojas/IdeaProjects/muebles/build/jacocoAggregateReport/index.html
```

