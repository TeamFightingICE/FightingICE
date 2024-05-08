FROM openjdk:21

RUN mkdir /app
WORKDIR /app

COPY ./FightingICE.jar .
COPY ./data ./data
COPY ./lib/*.jar ./lib/
COPY ./lib/grpc/*.jar ./lib/
COPY ./lib/lwjgl/*.jar ./lib/
COPY ./lib/lwjgl/natives/linux/amd64/*.jar ./lib/

ENTRYPOINT [ "java", "-cp", "FightingICE.jar:./lib/*", "Main", "--no-graphic" ]
CMD [ "--grpc-auto", "--limithp", "400", "400" ]