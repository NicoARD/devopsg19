#!/bin/bash
# Simple Docker helper for basic operations

case "$1" in
  build)
    echo "Building with Maven..."
    mvn package
    echo "Building Docker image..."
    docker build -t devopsimage ..
    ;;
  run)
    docker-compose -f ../docker-compose.yml up
    ;;
  stop)
    docker-compose -f ../docker-compose.yml down
    ;;
  *)
    echo "Available commands:"
    echo "  build - Build JAR and Docker image"
    echo "  run   - Start application and database" 
    echo "  stop  - Stop all containers"
    ;;
esac
