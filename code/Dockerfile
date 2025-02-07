# Stage 1: Build the WAR using Maven
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
# Copy the Maven descriptor to leverage caching
COPY pom.xml .
# Copy the source code
COPY src ./src
# Package the application (adjust if needed)
RUN mvn clean package

# Stage 2: Build the final image with Tomcat
FROM tomcat:9.0-jdk11-openjdk
# Optionally remove the default ROOT app
RUN rm -rf /usr/local/tomcat/webapps/ROOT
# Copy the WAR file built in stage 1 to Tomcat’s deployment folder
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

