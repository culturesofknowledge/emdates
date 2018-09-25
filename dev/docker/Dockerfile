FROM gradle:jdk8 as builder

#local: COPY --chown=gradle:gradle . /home/gradle/emdates
RUN svn export https://github.com/culturesofknowledge/emdates/trunk/dev /home/gradle/emdates
WORKDIR /home/gradle/emdates
RUN gradle -PprojVersion='master-docker' build

FROM openjdk:8-jre-slim
COPY --from=builder /home/gradle/emdates/build/libs/lobsang-full.jar /app/
COPY --from=builder /home/gradle/emdates/config-template.yml /app/config.yml
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "lobsang-full.jar", "server", "config.yml"]
