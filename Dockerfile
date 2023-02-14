FROM openjdk:17 as build
WORKDIR /work

COPY gradle gradle
COPY gradlew .
COPY build.gradle . 
COPY settings.gradle .
COPY src src

RUN ./gradlew bootJar


FROM openjdk:17
WORKDIR /app
COPY --from=build /work/build/libs/eventsignup-1.0.0.jar .

ENTRYPOINT ["java","-jar","eventsignup-1.0.0.jar"]