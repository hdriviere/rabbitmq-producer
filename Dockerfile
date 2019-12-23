FROM mozilla/sbt

RUN apt-get update && \
    git clone "https://github.com/hdriviere/rabbitmq-producer.git" && \
    mkdir /rabbitmq-producer/src/main/resources

WORKDIR /rabbitmq-producer

RUN sbt compile

