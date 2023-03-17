# JavaSamples

## File Description
- ```data/ai``` is where AI files should be placed.
- ```data/aiData``` is where the data required for AI loading is located.
- ```lib``` is where the library required to run this project is located.
- ```src``` contains the source code of example Java-implemented AI.
- ```AIToolkit.jar``` is a JAR file that includes the interfaces necessary for implementing Java AI for DareFightingICE. [Javadoc](https://www.ice.ci.ritsumei.ac.jp/~ftgaic/FTG-JavaAI-doc/)
- ```FTG-JavaAI.jar``` is the compiled JAR file for this project, and it is used for connecting to the DareFightingICE platform.
- ```run-linux.sh```, ```run-windows.bat``` are predefined scripts that include the commands to run this platform, along with the necessary parameters.

## Instruction
- Create an AI using the instructions provided [here](https://www.ice.ci.ritsumei.ac.jp/~ftgaic/index-2h.html).
- Place the compiled AI file in the `data/ai` directory, and any necessary data for running the AI in the `data/aiData/{Your_AI_Name}` directory.
- Boot DareFightingICE with the option `--grpc`.
- Use the appropriate script based on your operating system and modify the parameter to the desired name of the AI you wish to run.
```
java -cp FTG-JavaAI.jar;./lib/*; Main --a1 KickAI --a2 SomeAI
```
- Please note that this parameter can only be set to the AI name located in `data/ai`.
- Run the script to connect to DareFightingICE and deploy the AI.
