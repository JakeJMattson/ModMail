#!/bin/bash
docker build -t warmbot:latest -f docker/Dockerfile --no-cache .
cmd="docker run -e BOT_TOKEN='$1' -v $2:/config warmbot:latest"
eval $cmd