<font color="red">For the information on the competition from 2022, please see [this page](https://github.com/TeamFightingICE/FightingICE/tree/master/DareFightingICE).</font>

# FightingICE #

This repository is a repository for the development of the 2D fighting game FightingICE, which is used in international competitions for fighting game AI performance.<br>

### About FightingICE ###
FightingICE is a 2D fighting game used in the Fighting Game AI Competition (FTGAIC), an international competition that competes for the performance of fighting game AI certified by Computational Intelligence and Games (CIG).

### Quickstart with Docker ###
```sh
docker run -it --rm -v ${PWD}/log:/app/log -p 31415:31415 ghcr.io/teamfightingice/fightingice
```

### How to start development ###
- Clone the project into your local workspace.
- Download resources required for development from [here](https://github.com/TeamFightingICE/FightingICE/releases/download/v6.3/resources-6.3.zip) and extract into the root directory.
- Add all libraries from `lib` into your project. (for LWJGL, please add only one native match with your OS)

### Important contents ###
- data: Directory containing resources used in the game
	- ai: Directory containing AI jar files used in the game (for version 6.0 and later, internal use only)
	- aiData: Directory containing data files required by AI (for version 6.0 and later, internal use only)
	- characters: Directory containing character images and action parameter files
	- graphics: Directory containing graphics such as backgrounds and hadouken
	- sounds: Directory containing background music and sound effects
- lib: Directory containing libraries required for startup
- protos: Directory containing protocal buffer codes
- src: Directory containing source code

### Java libraries in use ###
- lwjgl: Containing libraries related to LWJGL module
	- natives: Containing libraries related to LWJGL module native to many architectures.
		- linux/amd64: for Linux
		- linux/arm64: for Linux with ARM-architecture CPU
		- macos/arm64: for macOS with Apple chip
		- windows/amd64: for Windows
	- lwjgl 3.3.3
	- lwjgl-glfw 3.3.3
	- lwjgl-openal 3.3.3
	- lwjgl-opengl 3.3.3
- failureaccess 1.0.1
- guava 32.1.2-jre
- jakarta.json-api 2.1.2
- javax.annotation-api 1.3.2
- lwjgl_util 2.9.3
- perfmark-api 0.26.0
- protobuf-java 3.24.3
- protobuf-java-util 3.24.3
