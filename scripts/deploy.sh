#!/bin/bash
docker build -t warmbot:latest -f docker/Dockerfile --no-cache .
cmd="docker run -e BOT_TOKEN='$1' -v $2:/data warmbot:latest"
eval $cmd