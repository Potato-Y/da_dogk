# Build Image
FROM openjdk:17-jdk-alpine AS TEMP_BUILD_IMAGE
ENV APP_HOME=/app
WORKDIR $APP_HOME

COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

COPY src ./src

#RUN chmod +x ./gradlew
RUN ./gradlew build -x test --stacktrace

# App 구동
FROM openjdk:17-jdk-alpine

ENV ARTIFACT_NAME=dadogk-1.0.1-RELEASE.jar
ENV APP_HOME=/app
WORKDIR $APP_HOME
COPY --from=TEMP_BUILD_IMAGE $APP_HOME/build/libs/$ARTIFACT_NAME $APP_HOME/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]