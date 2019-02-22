FROM maven:3.6.0-jdk-11-slim AS build

COPY pom.xml /pom.xml
COPY src /src/

RUN mvn clean package -f /pom.xml

# Run stage
FROM openjdk:12

ENV BOT_TOKEN=UNSET

RUN mkdir /config/
COPY --from=build /target/WarmBot-2.0.0-jar-with-dependencies.jar /Warmbot.jar

CMD /usr/bin/java -jar /Warmbot.jar $BOT_TOKEN