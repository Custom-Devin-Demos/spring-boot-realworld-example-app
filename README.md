# ![RealWorld Example App using Kotlin and Spring](example-logo.png)

[![Actions](https://github.com/gothinkster/spring-boot-realworld-example-app/workflows/Java%20CI/badge.svg)](https://github.com/gothinkster/spring-boot-realworld-example-app/actions)

> ### Spring boot + MyBatis codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld-example-apps) spec and API.

This codebase was created to demonstrate a fully fledged full-stack application built with Spring boot + Mybatis including CRUD operations, authentication, routing, pagination, and more.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

# *NEW* GraphQL Support  

Following some DDD principles. REST or GraphQL is just a kind of adapter. And the domain layer will be consistent all the time. So this repository implement GraphQL and REST at the same time.

The GraphQL schema is https://github.com/gothinkster/spring-boot-realworld-example-app/blob/master/src/main/resources/schema/schema.graphqls and the visualization looks like below.

![](graphql-schema.png)

And this implementation is using [dgs-framework](https://github.com/Netflix/dgs-framework) which is a quite new java graphql server framework.
# How it works

The application uses Spring Boot (Web, Mybatis).

* Use the idea of Domain Driven Design to separate the business term and infrastructure term.
* Use MyBatis to implement the [Data Mapper](https://martinfowler.com/eaaCatalog/dataMapper.html) pattern for persistence.
* Use [CQRS](https://martinfowler.com/bliki/CQRS.html) pattern to separate the read model and write model.

And the code is organized as this:

1. `api` is the web layer implemented by Spring MVC
2. `core` is the business model including entities and services
3. `application` is the high-level services for querying the data transfer objects
4. `infrastructure`  contains all the implementation classes as the technique details

# Security

Integration with Spring Security and add other filter for jwt token process.

The secret key is stored in `application.properties`.

# Database

It uses a ~~H2 in-memory database~~ sqlite database (for easy local test without losing test data after every restart), can be changed easily in the `application.properties` for any other database.

# Getting started

## Prerequisites

You'll need **Java 17** installed. This project has been upgraded from Java 11 to Java 17.

### Java 17 Installation (macOS)

1. **Install OpenJDK 17 via Homebrew:**
   ```bash
   brew install openjdk@17
   ```

2. **Create system symlink:**
   ```bash
   sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
   ```

3. **Set JAVA_HOME environment variable:**
   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 17)
   ```
   
   For permanent setup, add to your `~/.zshrc` or `~/.bash_profile`:
   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 17)
   export PATH="$JAVA_HOME/bin:$PATH"
   ```

4. **Verify installation:**
   ```bash
   java -version
   # Should output: openjdk version "17.0.16" or similar
   ```

## Running the Application

    JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew bootRun

To test that it works, open a browser tab at http://localhost:8080/tags .  
Alternatively, you can run

    curl http://localhost:8080/tags

# Try it out with [Docker](https://www.docker.com/)

You'll need Docker installed.
	
    ./gradlew bootBuildImage --imageName spring-boot-realworld-example-app
    docker run -p 8081:8080 spring-boot-realworld-example-app

# Try it out with a RealWorld frontend

The entry point address of the backend API is at http://localhost:8080, **not** http://localhost:8080/api as some of the frontend documentation suggests.

# Run test

The repository contains a lot of test cases to cover both api test and repository test.

    JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew test

# Code format

Use spotless for code format.

    JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew spotlessJavaApply

## IDE Configuration

### IntelliJ IDEA
1. Open **File > Project Structure**
2. Under **Project Settings > Project**, set:
   - **Project SDK**: 17 (java version "17.0.16")
   - **Project language level**: 17
3. Under **Platform Settings > SDKs**, ensure Java 17 is configured
4. **File > Settings > Build, Execution, Deployment > Build Tools > Gradle**
   - Set **Gradle JVM** to Java 17

### VS Code
1. Install the **Extension Pack for Java**
2. Open **Command Palette** (Cmd+Shift+P)
3. Run **Java: Configure Java Runtime**
4. Set the project JDK to Java 17

## Troubleshooting

### Common Issues
- **Gradle Daemon Issues**: Run `./gradlew --stop` then retry your build command
- **IDE Project Reimport**: 
  - IntelliJ: **File > Reload Gradle Project**
  - VS Code: **Command Palette > Java: Reload Projects**
- **Code Formatting Violations**: Run `./gradlew spotlessApply`

# Help

Please fork and PR to improve the project.
