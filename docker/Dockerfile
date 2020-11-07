FROM gradle:6.7.0-jdk15 AS build
COPY --chown=gradle:gradle . /modmail
WORKDIR /modmail
RUN gradle shadowJar --no-daemon

FROM openjdk:8-jre-slim
ENV BOT_TOKEN=UNSET
RUN mkdir /config/
COPY --from=build /modmail/build/libs/*.jar /ModMail.jar

ENTRYPOINT ["java", "-jar", "/ModMail.jar", "$BOT_TOKEN"]