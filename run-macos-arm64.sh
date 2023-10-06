#!/bin/bash
java -XstartOnFirstThread -cp FightingICE.jar:./lib/*:./lib/lwjgl/*:./lib/lwjgl/natives/macos/arm64/*:./lib/grpc/* Main --limithp 400 400 --grey-bg --grpc