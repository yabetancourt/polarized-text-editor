FROM amazoncorretto:21-alpine
COPY target/*.jar app.jar
COPY SentiWordNet_3.0.0.txt SentiWordNet_3.0.0.txt
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
