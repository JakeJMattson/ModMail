FROM maven:3.6.3-openjdk-15 AS build

COPY pom.xml /pom.xml
COPY src /src/

RUN mvn clean package -f /pom.xml

# Run stage
FROM openjdk:15

ENV BOT_TOKEN=UNSET

RUN mkdir /data/
COPY --from=build /target/WarmBot-jar-with-dependencies.jar /Warmbot.jar

CMD /usr/bin/java -jar /Warmbot.jar $BOT_TOKEN