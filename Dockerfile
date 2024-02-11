FROM eclipse-temurin:21.0.2_13-jdk-alpine AS build
RUN apk update && apk --no-cache add findutils

WORKDIR /work

COPY gradle gradle
COPY gradlew .
COPY build.gradle . 
COPY settings.gradle .
COPY src src

RUN ./gradlew bootJar


FROM eclipse-temurin:21.0.2_13-jdk-alpine
WORKDIR /app
COPY --from=build /work/build/libs/eventsignup.jar .

ENTRYPOINT ["java","-jar","eventsignup.jar"]

# add image to repository
LABEL org.opencontainers.image.source=https://github.com/asteriskiry/eventsignup_backend
