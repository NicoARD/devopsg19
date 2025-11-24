@echo off
REM Simple Docker helper for basic operations

if "%1"=="build" (
    echo Building with Maven...
    mvn package
    echo Building Docker image...
    docker build -t devopsimage ..
    exit /b
)

if "%1"=="run" (
    docker-compose -f ..\docker-compose.yml up
    exit /b
)

if "%1"=="stop" (
    docker-compose -f ..\docker-compose.yml down
    exit /b
)

REM Default help
echo Available commands:
echo   build - Build JAR and Docker image  
echo   run   - Start application and database
echo   stop  - Stop all containers
