#!/bin/bash

SCRIPT_DIR=$(cd $(dirname $0); pwd)

cd $SCRIPT_DIR

# for linux
# java -cp FightingICE.jar:./lib/lwjgl/*:./lib/natives/linux/*:./lib/*  Main --py4j --mute --port 4242

# for macos
java -XstartOnFirstThread -cp FightingICE.jar:./lib/lwjgl/*:./lib/natives/macos/*:./lib/*  Main --py4j --mute --port 4242

