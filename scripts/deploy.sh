#!/bin/bash
docker build -t warmbot:latest -f docker/Dockerfile --no-cache .
cmd="docker run -e BOT_TOKEN='$1' -v /home/fox/dev/kotlin/WarmBot/config:/config warmbot:latest"
eval $cmd