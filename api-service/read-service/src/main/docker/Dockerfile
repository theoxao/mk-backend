FROM openjdk:8-jre-alpine
VOLUME /tmp
ADD @project.build.finalName@.jar app.jar
RUN sh -c 'touch /app.jar'
ENV ACTIVE_PROFILE="docker"
ENTRYPOINT [ "sh", "-c", "java -Dspring.profiles.active=$ACTIVE_PROFILE -jar /app.jar" ]