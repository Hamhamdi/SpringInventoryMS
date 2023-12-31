# Microservices Project

This is a microservices project built with Spring Boot. It consists of the following modules:

- **inventory-service** - Manages product inventory
- **order-service** - Handles order management
- **product-service** - Provides product data
- **discovery-server** - Eureka discovery server for service registration and discovery
- **api-gateway** - Zuul API gateway that routes requests to services
- **notification-service** - Sends notifications for different events

## Technologies

- Java 17
- Spring Boot 3.0.5
- Spring Cloud 2022.0.1

## Building and Running

- To build:
`mvn clean install
`
- To run:
`java -jar <service-jar>.jar
`

E.g.
` java -jar notification-service/target/notification-service-1.0-SNAPSHOT.jar`


The services will be registered with the discovery server on startup.

## Containerization

The services are configured for containerization using Jib maven plugin.

- To build a Docker image:
`mvn compile jib:dockerBuild`

This will build a Docker image for each service and push to Docker Hub under `microservices-tutorial` organization.

## Future Work

- Configure Kubernetes deployment
- Add monitoring with Prometheus and Grafana
- Implement circuit breakers with Resilience4j
- Improve logging with ELK stack

