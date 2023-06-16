FROM openjdk:11-jre-slim

ARG JAR_FILE=build/**/clematis.weather.api-*.jar
COPY ${JAR_FILE} app.jar

RUN mkdir -p /var/log/clematis
RUN mkdir -p /home/clematis/weather/images

ENTRYPOINT ["java","-jar","/app.jar"]
