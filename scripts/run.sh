#!/bin/bash
cmd="docker run -e BOT_TOKEN='$1' -v /home/fox/dev/kotlin/WarmBot/config:/config warmbot:latest"
eval $cmd