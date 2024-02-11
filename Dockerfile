FROM openjdk:21 AS build
WORKDIR /work

COPY gradle gradle
COPY gradlew .
COPY build.gradle . 
COPY settings.gradle .
COPY src src

RUN ./gradlew bootJar


FROM openjdk:21
WORKDIR /app
COPY --from=build /work/build/libs/eventsignup.jar .

ENTRYPOINT ["java","-jar","eventsignup.jar"]

# add image to repository
LABEL org.opencontainers.image.source https://github.com/asteriskiry/eventsignup_backend
