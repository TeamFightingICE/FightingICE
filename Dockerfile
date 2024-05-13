FROM ubuntu/jre:17-22.04_edge

WORKDIR /
USER root

COPY ./FightingICE.jar .
COPY ./data/ai ./data/ai
COPY ./data/characters/ZEN/gSetting.txt ./data/characters/ZEN/gSetting.txt
COPY ./data/characters/ZEN/Motion.csv ./data/characters/ZEN/Motion.csv
COPY ./lib/*.jar ./lib/
COPY ./lib/lwjgl/*.jar ./lib/
COPY ./lib/lwjgl/natives/linux/amd64/*.jar ./lib/

ENTRYPOINT [ "/opt/java/bin/java", "-cp", "FightingICE.jar:./lib/*", "Main", "--lightweight-mode" ]
CMD [ "--auto", "--limithp", "400", "400" ]