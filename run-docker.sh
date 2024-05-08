#!/bin/bash
java -cp FightingICE.jar:./lib/*:./lib/lwjgl/*:./lib/lwjgl/natives/linux/amd64/*:./lib/grpc/* Main --limithp 400 400 --grey-bg --no-graphic --grpc-auto