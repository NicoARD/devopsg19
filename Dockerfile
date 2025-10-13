# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /tmp

# Copy compiled classes
# compile before running!
# javac -d target/classes src/main/java/com/napier/sem/App.java
COPY target/classes/com /tmp/com

# Set entrypoint to run the main method
ENTRYPOINT ["java", "-cp", ".", "com.napier.sem.App"]
