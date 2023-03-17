<font color="red">For the information on the competition from 2022, please see [this page](https://github.com/TeamFightingICE/FightingICE/tree/master/DareFightingICE).</font>

# FightingICE #
This repository is a repository for the development of the 2D fighting game FightingICE, which is used in international competitions for fighting game AI performance.<br>

### About FightingICE ###
FightingICE is a 2D fighting game used in the Fighting Game AI Competition (FTGAIC), an international competition that competes for the performance of fighting game AI certified by Computational Intelligence and Games (CIG).

### How to start development ###
Important contents
- data: Directory containing resources used in the game [Download here](https://github.com/TeamFightingICE/FightingICE/releases/download/v6.0.1/resources-6.0.1.zip)
	- ai: Directory containing AI jar files used in the game (for version 6.0 and later, internal use only)
	- aiData: Directory containing data files required by AI (for version 6.0 and later, internal use only)
	- characters: Directory containing character images and action parameter files
	- graphics: Directory containing graphics such as backgrounds and hadouken
	- sounds: Directory containing background music and sound effects
- lib: Directory containing libraries required for startup
- protoc-gen: Directory containing libraries required for generate gRPC code
- src: Directory containing source code

See FightingICE official website for more detail. <http://www.ice.ci.ritsumei.ac.jp/~ftgaic/index.htm>

### Java libraries in use ###
- grpc: Containing libraries related to gRPC module
	- grpc-api 1.52.1
	- grpc-context 1.52.1
	- grpc-core 1.52.1
	- grpc-netty-shaded 1.52.1
	- grpc-protobuf 1.52.1
	- grpc-protobuf-lite 1.52.1
	- grpc-stub 1.52.1
	- protobuf-java 3.21.12
	- protobuf-java-util 3.21.12
	- protoc-gen-grpc-java 1.53.0 (use for generate gRPC code)
- lwjgl: Containing libraries related to LWJGL module
	- natives: Containing libraries related to LWJGL module native to many architectures.
		- linux/amd64: for Linux
		- linux/arm64: for Linux with ARM-architecture CPU
		- macos/amd64: for macOS with Intel chip
		- macos/arm64: for macOS with Apple chip
		- windows/amd64: for Windows
		- windows/arm64: for Windows with ARM-architecture CPU
	- lwjgl 3.3.1
	- lwjgl-glfw 3.3.1
	- lwjgl-openal 3.3.1
	- lwjgl-opengl 3.3.1
- annotations-api 6.0.53
- failureaccess 1.0.1
- guava 31.1-jre
- javax.json 1.0.4
- lwjgl_util 2.9.3
- perfmark-api 0.26.0
- py4j 0.10.4
