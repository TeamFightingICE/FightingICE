#!/bin/bash
java -XstartOnFirstThread -cp FightingICE.jar:./lib/*:./lib/lwjgl/*:./lib/lwjgl/natives/macos/amd64/*:./lib/grpc/* Main --limithp 400 400 --inverted-player 1 --grey-bg --grpc
