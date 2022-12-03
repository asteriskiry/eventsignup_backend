FROM openjdk:17
COPY build/libs/eventsignup-1.0-SNAPSHOT.jar eventsignup.jar
ENTRYPOINT ["java","-jar","/eventsignup.jar"]
