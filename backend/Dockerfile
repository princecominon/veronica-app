# Use an official Gradle image to build the project
FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle installDist --no-daemon

# Use a lighter image to run the application
FROM eclipse-temurin:21-jre-jammy
RUN mkdir /app
COPY --from=build /home/gradle/src/build/install/veronica-backend/ /app/
WORKDIR /app
CMD ["./bin/veronica-backend"]