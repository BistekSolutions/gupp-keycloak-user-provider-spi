# General

This projects combines the implementation of a Keycloak UserProvider with a Rest-API, that exposes <br/>
the endpoints from the [RestResource](src/main/java/com/bismarck/keycloak/realm/provider/RestResource.java)

# Important

This project uses the [gupp-keycloak-sdk](https://github.com/BistekSolutions/gupp-keycloak-sdk) project. Because this project uses the maven-shade-plugin, to bundle pom-dependencies, <br/>
it should be sufficient, to implement it locally.

# Installation

Build the project via <b>mvn install</b> <br/>
Put the generated gupp-keycloak-user-provider-spi-bundled.jar into the providers folder of your keycloak server <br/>
together with the needed [mysql-jar](https://dev.mysql.com/downloads/connector/j/5.1.html). <br>

Restart your keycloak server and configure the user provider under: <b>User federation</b>

# SPI Endpoints
## Usage

You can call an endpoint with the baseUrl: <b>keycloakBaseUri/realms/realmName/master/gupp</b> <br/>
e.g.: [localhost:8080/realms/master/gupp]()

## Implemented Endpoints

| Endpoint     | Method      | Parameters               | Description                                                                                                                                                                                                                       | Required Permissions                                                                                                                                                                                              |
|--------------|-------------|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| /tenant/sync | <b>POST</b> | JSON: {user: BigInteger} | Fetches the tenant from the [UserProvider](src/main/java/com/bismarck/keycloak/realm/provider/user/RestUserProvider.java) and tries <br/> to add the user (see: Parameters) to a keycloak group with the same name as the tenant. | <b>admin</b> or <b>super_admin</b> if the requester is not <br/> in the same group as it's tenant fetched from the [UserProvider](src/main/java/com/bismarck/keycloak/realm/provider/user/RestUserProvider.java). |

## Add your own Endpoint

1) Add a new Task-Class, that extends the [BasicRestTask](src/main/java/com/bismarck/keycloak/realm/provider/task/BasicRestTask.java)
2) Override the run-Method from the [BasicRestTask](src/main/java/com/bismarck/keycloak/realm/provider/task/BasicRestTask.java) and return a valid Jakarta-WS Response 
3) Add a new Endpoint-Method to the [RestResource](src/main/java/com/bismarck/keycloak/realm/provider/RestResource.java)
4) Call the getResponseFromTask-method from the initialised TaskRunner-variable in the [RestResource](src/main/java/com/bismarck/keycloak/realm/provider/RestResource.java) with an instance of the new Task-Class as first parameter

## Sources, if you want to implement your own REST API with a UserProvider 

### SPI

Official keycloak documentation for SPI implementation: <br/>
https://www.keycloak.org/docs/latest/server_development/index.html#_providers <br/>
<br/>
StackOverflow discussion regarding calling of a custom keycloak SPI via REST api: <br/>
https://stackoverflow.com/questions/54900598/keycloak-rest-url-for-custom-endpoint <br/>
<br/>
Discussion about custom keycloak SPI token implementation: <br/>
https://github.com/keycloak/keycloak/discussions/22779 <br/>
<br/>
StackOverflow discussion about implementation of a custom keycloak SPI: <br/>
https://stackoverflow.com/questions/54482881/keycloak-custom-spi-rest-endpoint-with-authorization

### User Provider

Official keycloak documentation for custom user provider implementation: <br/>
https://www.keycloak.org/docs/latest/server_development/index.html#_user-storage-spi <br/>
<br/>
Baeldung documentation for implementation of a basic user provider: <br/>
https://www.baeldung.com/java-keycloak-custom-user-providers <br/>
<br/>
GitHub project example for user provider implementation: <br>
https://github.com/glatrofa/keycloak-user-federation-db

### Configuration

Example implementation of a custom user provider with admin ui configuration: <br/>
https://github.com/thomasdarimont/keycloak-avatar-minio-extension
