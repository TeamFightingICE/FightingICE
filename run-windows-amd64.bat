@ECHO OFF
java -cp FightingICE.jar;./lib/*;./lib/lwjgl/*;./lib/lwjgl/natives/windows/amd64/*;./lib/grpc/*; Main --limithp 400 400 --grey-bg --auto --lightweight-mode --save-sound-on-replay
