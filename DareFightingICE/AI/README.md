# <div align="center"> AI Track </div>
----
<div align = "center"> Welcome to the AI track of DareFightingICE. Here you will be provided all the necessary information needed to participate in the AI track. Please remember to read carefully and thoroughly before contacting us for any questions you might have. </div>

<br>


### What is AI Track:
----
On this track, you will be asked to make an AI that plays DareFightingICE using only in-game sound data as input. You will be provided with an interface that allows access to sound data and a sample AI.


### What To Submit:
---
For submission, please create a zip file containing:

- Your AI's jar file (if you use java)
- Source code (for training AI and evaluating AI, in .java for a Java AI or .py for a Python AI) and/or
- File-I/O folder with the same name as your AI
- A README file that describes the environment, file structure, and instructions to run your AI

Please also attach a PowerPoint (or OpenOffice) file describing the following information:

- AI Name, Developer's Name(s) and Affiliation(s)
- AI's Outline 


### Sample AI:
---
The source code and model of our deep reinforcement learning blind AI is available [here](../SampleAI/BlindAI).


### Rules:
---
One game has three rounds. The maximum fighting time of a round is 60s. After one round, the character's positions and HPs will be reset, and a new round is started. All games in the competition are conducted with the options "--limithp [P1HP] [P2HP]" (limit-HP mode) and "--blind-player 1/2/0" (limit access only to sound data for players 1/2/both) of DareFightingICE. Please note that the FrameData information sent to AIs will have a delay of 15 frames, but sound data and visual data have no delay.

Please keep in mind that although FrameData and visual data are accessible during AI training phase, in the competition, only sound data is provided to all the participating AIs.

Your AI will be made publicly available and by submitting you will have agreed to this.

### Evaluation:
---
Two leagues (Standard and Speedrunning) in this track are described as follows:
The Standard League considers the winner of a round as the one with the HP above zero at the time its opponent's HP has reached zero. Both AIs will be given the initial HP of 400. The league for a given character type is conducted in a round-robin fashion with two games for any pair of entry AIs switching P1 and P2. The AI with highest number of winning rounds becomes the league winner; If necessary, remaining HPs are used for breaking ties. In this league, our weakened sample MctsAi (MctsAi65's jar file and its source code are available [here](../SampleAI/MctsAi65)), played in the non-blind mode or with FrameData, and our [sample deep-learning blind AI](../SampleAI/BlindAI), played in the blind mode, will also be participating as baseline AIs.

In the Speedrunning League, the league winner of a given character type is the AI with the shortest average time to beat both of our aforementioned sample AIs. For each entry AI, 5 games are conducted with the entry AI being P1 and a sample AI being P2, and another set of 5 games with the entry AI being P2 and a sample AI being P1. Both AIs will be given the initial HP of 400. If a sample AI of interest cannot be beaten in 60s, the beating time of its opponent entry AI is penalized to 70s. <br>

In this track, only "Zen" character, with the same [motion data](https://www.ice.ci.ritsumei.ac.jp/~ftgaic/Downloadfiles/Motion/ZEN/Motion.csv) as the motion data included in DareFightingICE will be used.

The ranking rules are as follows:
In each of the two leagues (in this order: Zen Standard, Zen Speedrunning), the AIs are ranked according to the number of winning rounds. If ties exit, their total remaining HPs will be used. Once the AIs are ranked in each league, league points are awarded to them according to their positions using **[the 2018 Formula-1 scoring system ](https://en.wikipedia.org/wiki/2018_Formula_One_World_Championship#Scoring_system)**. The competition winner is finally decided by the sum of league points across all two leagues.


### Evaluation Environments: 
---
1. Software:
   - Python: 3.7 or higher
   - Java: 1.8 or higher
   - OS: Windows 10
2. Hardware:
   - CPU: Intel(R) Xeon(R) W-2135 CPU @ 3.70GHz   3.70 GHz
   - RAM: 16 GB


### Installation Guide:
---
To install DareFightingICE please follow the instructions on this [link](https://www.ice.ci.ritsumei.ac.jp/~ftgaic/index-2.html). (latest version 5.2)


### Submission:
---

Please submit your entry via email to ftg dot aic at gmail dot com with an email subject of "2022_AI". In case the attached file's size is larger than 5.0 MB, please add a link in the email for us to download it. Below are the deadlines.

Midterm deadline (We recommend you do midterm submission, but if you miss it, you can still submit your AI to us by the final deadline.)
- May 24, 2022 (23:59 JST)

Final deadline
- July 29, 2022 (23:59 JST)(no extension!!)
