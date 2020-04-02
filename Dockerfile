FROM openjdk:8-jre-alpine as build
WORKDIR /workspace/app

WORKDIR /topic-starters-app
COPY ./api/target/topicstarters-api.jar app.jar
COPY ./run.sh run.sh

RUN chmod +x run.sh

ENTRYPOINT ["./run.sh"]
