FROM ubuntu/jre:17-22.04_edge

ARG TARGETARCH

WORKDIR /
USER root

COPY ./FightingICE.jar .
COPY ./data/ai ./data/ai
COPY ./data/characters/ZEN/gSetting.txt ./data/characters/ZEN/gSetting.txt
COPY ./data/characters/ZEN/Motion.csv ./data/characters/ZEN/Motion.csv
COPY ./lib/*.jar ./lib/
COPY ./lib/lwjgl/*.jar ./lib/
COPY ./lib/lwjgl/natives/linux/${TARGETARCH}/*.jar ./lib/

EXPOSE 31415/tcp

ENTRYPOINT [ "/opt/java/bin/java", "-cp", "FightingICE.jar:./lib/*", "Main", "--lightweight-mode" ]
CMD [ "--limithp", "400", "400", "--pyftg-mode" ]