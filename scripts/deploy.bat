docker build -t modmail:latest -f docker/Dockerfile --no-cache .
docker run -e BOT_TOKEN=%1 -v %2:/data modmail:latest