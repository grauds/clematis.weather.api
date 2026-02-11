FROM eclipse-temurin:25-jdk

ENV ENV_NAME=local

ARG JAR_FILE=build/**/clematis.weather.api-*.jar
COPY ${JAR_FILE} app.jar

RUN mkdir -p /var/log/clematis
RUN mkdir -p /home/clematis/weather/images

RUN apt-get update && apt-get install -y curl

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$ENV_NAME -jar app.jar"]
