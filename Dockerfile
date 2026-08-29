FROM eclipse-temurin:25-jdk AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

COPY sample-core/pom.xml sample-core/pom.xml
COPY sample-backend-client/pom.xml sample-backend-client/pom.xml
COPY sample-backend/pom.xml sample-backend/pom.xml
COPY sample-batch/pom.xml sample-batch/pom.xml
COPY sample-bff/pom.xml sample-bff/pom.xml
COPY sample-loadtest/pom.xml sample-loadtest/pom.xml

RUN chmod +x mvnw
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -pl sample-batch -am dependency:go-offline

COPY sample-core sample-core
COPY sample-backend-client sample-backend-client
COPY sample-backend sample-backend
COPY sample-batch sample-batch
COPY sample-bff sample-bff
COPY sample-loadtest sample-loadtest

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -pl sample-batch -am clean package -DskipTests


FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /workspace/sample-batch/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]