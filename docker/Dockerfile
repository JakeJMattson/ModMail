# Build this image
# docker build -t warmbot:latest -f docker/Dockerfile --no-cache .

# Run it -- specifying the token is required.
# docker run -e BOT_TOKEN='a valid discord bot token' warmbot:latest

FROM maven:3.6.0-jdk-11-slim AS build

COPY pom.xml /pom.xml
COPY src /src/

RUN mvn clean package -f /pom.xml

# Run stage
FROM openjdk:12

ENV BOT_TOKEN=UNSET

COPY --from=build /target/WarmBot-2.0.0-jar-with-dependencies.jar /Warmbot.jar

CMD /usr/bin/java -jar /Warmbot.jar $BOT_TOKEN