FROM openjdk:latest
COPY target/classes/data ./data
COPY target/classes/schema ./schema

ADD target/FinalProject-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8080
