# Stage 0, "build-stage", based on Node.js, to build and compile the frontend
FROM gradle:4.10.0-jdk8-alpine AS build
COPY --chown=gradle:gradle ./home-work /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

# Stage 1, based on Nginx, to have only the compiled app, ready for production with Nginx
FROM java:8-jdk-alpine
ENV PORT=${PORT} \
    USERNAME=${USERNAME} \
    PASSWORD=${PASSWORD} \
    DATABASE=${DATABASE}
EXPOSE ${PORT}
RUN mkdir -p /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/home-work.jar
WORKDIR /app
ENTRYPOINT [ "java","-Dspring.datasource.url=jdbc:postgresql://44.228.173.60:5432/${DATABASE}","-Dspring.datasource.username=${USERNAME}","-Dserver.port=${PORT}","-Dspring.datasource.password=${PASSWORD}","-Djava.security.egd=file:/dev/./urandom","-jar","home-work.jar" ]
