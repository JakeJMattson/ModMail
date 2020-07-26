#!/bin/bash
docker build -t modmail:latest -f docker/Dockerfile --no-cache .
cmd="docker run -e BOT_TOKEN='$1' -v $2:/data modmail:latest"
eval $cmd