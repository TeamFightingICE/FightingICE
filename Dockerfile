FROM openjdk:21

RUN mkdir /app
WORKDIR /app

COPY ./run-docker.sh script.sh
COPY ./FightingICE.jar .

COPY ./data ./data

COPY ./lib/*.jar ./lib/
COPY ./lib/grpc/*.jar ./lib/grpc/
COPY ./lib/lwjgl/*.jar ./lib/lwjgl/
COPY ./lib/lwjgl/natives/linux/amd64/*.jar ./lib/lwjgl/natives/linux/amd64/

CMD [ "/bin/bash", "script.sh" ]