FROM gradle:jdk8 as builder

#local: COPY --chown=gradle:gradle /path/to/local/emdates /home/gradle/emdates
RUN git clone --depth=1 https://github.com/culturesofknowledge/emdates /home/gradle/emdates
WORKDIR /home/gradle/emdates/dev
RUN gradle build

FROM openjdk:8-jre-slim
COPY --from=builder /home/gradle/emdates/dev/build/libs/lobsang-full.jar /app/
COPY --from=builder /home/gradle/emdates/dev/config-template.yml /app/config.yml
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "lobsang-full.jar", "server", "config.yml"]
