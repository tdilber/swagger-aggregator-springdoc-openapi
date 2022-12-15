# Swagger Aggregator Springdoc Openapi

Project Collecting all swagger yaml's and merge and serving merged swagger docs with swagger-ui and also as yaml.

Swagger for microservice architecture.

Note: This project developed with springdoc-openapi v2.0.0 because This project Just supported on Spring Boot v3.0.0 (
and above).

I using on my open-source microservice project. You can see how to work.

https://github.com/tdilber/anouncy

<img width="800" alt="image" src="https://user-images.githubusercontent.com/10117616/207860332-78cff55b-90a9-4cda-8266-a1ea35c2c548.png">

## Usage:

**Just add this 3 step:**

### 1

```xml

<dependencies>
    ...
    <dependency>
        <groupId>com.beyt.doc</groupId>
        <artifactId>swagger-aggregator-springdoc-openapi</artifactId>
        <version>0.0.1</version>
    </dependency>
</dependencies>

<repositories>
<repository>
    <id>github</id>
    <name>GitHub Apache Maven Packages</name>
    <url>https://maven.pkg.github.com/tdilber/swagger-aggregator-springdoc-openapi</url>
</repository>
</repositories>
```

### 2

Just Add the annotation on Spring Boot Application annotation.

```java
@EnableGroupedSwagger
        ..
@SpringBootApplication
```

### 3

```yaml
springdoc:
  grouping:
    enable: true
    refreshTimeoutMs: 4000  #default value 5000
    baseModule: #if you have security properties then just base module properties taking.
      description: "Location Service"
      url: http://127.0.0.1:8085/v3/api-docs.yaml
    modules:
      module1: # module names not important
        description: "User Service" #this name shown on Servers Tab
        url: http://127.0.0.1:8080/v3/api-docs.yaml
      module2:
        description: "Announce Service"
        url: http://127.0.0.1:8081/v3/api-docs.yaml
      module3:
        description: "Vote Service"
        url: http://127.0.0.1:8082/v3/api-docs.yaml
      module4:
        description: "Announce Listing Service"
        url: http://127.0.0.1:8083/v3/api-docs.yaml
      module5:
        description: "Region Service"
        url: http://127.0.0.1:8084/v3/api-docs.yaml
      module6:
        description: "Persist Service"
        url: http://127.0.0.1:8086/v3/api-docs.yaml
```
**Note:** You should not use ui dependency on sub modules. You can use the dependecy on sub modules for prevent unused swagger-ui dependency.
(Instead of springdoc-openapi-starter-webmvc-ui)

```xml 
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-api</artifactId>
    <version>2.0.0</version>
</dependency>
```

## How To Work

First fetching Base Module. (If base module not setted then create empty config.)

Then fetching one by one the others and **merging just 4 part of yaml**.

- Servers
- Paths
- Tags
- Schemas

Serving yaml path fixed and it is **/v3/grouped-api-docs** but ui path will be default url.
