# Build this image
# docker build -t warmbot:latest -f docker/Dockerfile --no-cache .

# Run it -- specifying the token is required.
# docker run -e BOT_TOKEN='a valid discord bot token' warmbot:latest

FROM openjdk:12

COPY target/WarmBot-2.0.0-jar-with-dependencies.jar /Warmbot.jar

COPY config/ /config/

ENV BOT_TOKEN=UNSET

CMD /usr/bin/java -jar /Warmbot.jar $BOT_TOKEN