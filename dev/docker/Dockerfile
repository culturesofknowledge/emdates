FROM gradle:jdk8 as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM openjdk:8-jre-slim
COPY --from=builder /home/gradle/src/build/libs/lobsang-full.jar /app/
COPY --from=builder /home/gradle/src/config-template.yml /app/config.yml
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "lobsang-full.jar", "server", "config.yml"]
