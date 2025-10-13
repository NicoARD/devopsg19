#!/bin/bash
# Helper script for Docker operations (Linux/Mac)
set -e

help() {
    echo "Available commands:"
    echo "  build   - Compile Java source and build Docker image"
    echo "  run     - Run single container with port mapping"
    echo "  start   - Start full stack with docker-compose (app, db, mongo, adminer)"
    echo "  stop    - Stop all containers"
    echo "  logs    - View application logs"
    echo "  cleanup - Remove containers, images, and volumes"
    echo "  info    - Show Docker system information"
    echo "  help    - Show this help message"
}

if [ -z "$1" ]; then
    help
    echo
    read -p "Enter command: " command
    exec "$0" "$command"
fi

case "$1" in
  help)
    help
    ;;
  build)
  build)
    echo "Compiling Java source..."
    javac -d ../target/classes ../src/com/napier/sem/App.java
    if [ $? -ne 0 ]; then
        echo "Compilation failed!"
        exit 1
    fi
    docker build -t sem-app ..
    ;;
  run)
    docker run --rm -it -p 8080:8080 --env-file ../.env.example sem-app
    ;;
  start)
    docker-compose -f ../docker-compose.yml --env-file ../.env.example up -d
    ;;
  stop)
    docker-compose -f ../docker-compose.yml down
    ;;
  logs)
    docker-compose -f ../docker-compose.yml logs -f app
    ;;
  cleanup)
    docker-compose -f ../docker-compose.yml down -v --rmi all --remove-orphans
    ;;
  info)
    docker system info
    ;;
  *)
    help
    ;;
esac
