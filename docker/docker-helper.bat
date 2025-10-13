@echo off
setlocal enabledelayedexpansion

REM Helper script for Docker operations (Windows)

REM Check for help or no arguments first
if "%1"=="" (
    call :help
    echo.
    set /p command="Enter command: "
    call "%0" !command!
    exit /b
)
if "%1"=="help" (
    call :help
    exit /b
)

if "%1"=="build" (
    echo Compiling Java source...
    javac -d ..\target\classes ..\src\com\napier\sem\App.java
    if %errorlevel% neq 0 (
        echo Compilation failed!
        exit /b 1
    )
    docker build -t sem-app ..
    exit /b
)
if "%1"=="run" (
    docker run --rm -it -p 8080:8080 --env-file ..\.env.example sem-app
    exit /b
)
if "%1"=="start" (
    docker-compose -f ..\docker-compose.yml --env-file ..\.env.example up -d
    exit /b
)
if "%1"=="stop" (
    docker-compose -f ..\docker-compose.yml down
    exit /b
)
if "%1"=="logs" (
    docker-compose -f ..\docker-compose.yml logs -f app
    exit /b
)
if "%1"=="cleanup" (
    docker-compose -f ..\docker-compose.yml down -v --rmi all --remove-orphans
    exit /b
)
if "%1"=="info" (
    docker system info
    exit /b
)

REM Invalid command
echo Unknown command: %1
call :help
exit /b

:help
echo Available commands:
echo   build   - Compile Java source and build Docker image
echo   run     - Run single container with port mapping
echo   start   - Start full stack with docker-compose (app, db, mongo, adminer)
echo   stop    - Stop all containers
echo   logs    - View application logs
echo   cleanup - Remove containers, images, and volumes
echo   info    - Show Docker system information
echo   help    - Show this help message
exit /b
