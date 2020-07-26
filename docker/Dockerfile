FROM maven:3.6.3-openjdk-15 AS build

COPY pom.xml /pom.xml
COPY src /src/

RUN mvn clean package -f /pom.xml

# Run stage
FROM openjdk:15

ENV BOT_TOKEN=UNSET

RUN mkdir /data/
COPY --from=build /target/modmail-jar-with-dependencies.jar /ModMail.jar

CMD /usr/bin/java -jar /ModMail.jar $BOT_TOKEN