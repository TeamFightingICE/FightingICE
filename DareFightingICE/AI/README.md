# <div align="center"> 2024 DareFightingICE AI Competition</div>
# Owing to a few setbacks with Unity, we have decided to revert to our Java version and augment its capabilities to foster sound design research. We anticipate launching this platform on May 8, 2024. We extend our sincere apologies for any inconvenience.
----
<div align = "center"> Welcome to the 2024 DareFightingICE AI Competition. Here you will be provided all the necessary information needed to participate in the competition. Please remember to read carefully and thoroughly before contacting us for any questions you might have. </div>
<br>



### What is the DareFightingICE AI Competition:
----
In this competition, you are tasked with making an AI that plays DareFightingICE using only in-game sound data as the input. You will be provided with the interface that allows access to sound data and a sample AI. **Click the image below to watch our promotional video.**

[![Watch the video](./Logo.png)](https://youtu.be/CHaf0vfYkvM)

### Instruction on how to run the platform:
---
1. Download DareFightingICE from this [link](https://github.com/TeamFightingICE/FightingICE/releases) (latest version 6.3).
2. Extract the downloaded file and run the appropriated script for your OS (e.g. `run-windows-amd64.bat` for Windows).
3. Once the game screen opens, move to "Launch" with the arrow keys and press "Z" button.
4. Follow the instruction in this [link](https://github.com/TeamFightingICE/pyftg/tree/master/examples) on how to run Python AI.
5. Move to "Play" with the arrow keys and press "Z" button to start the fight.

### What To Submit:
---
For submission, please create a zip file containing:

- Source code (.py for a Python AI) and/or
- File-I/O folder with the same name as your AI
- The `requirements.txt` or `environment.yml` file. Prior to submission, verify that the file can be utilized on different machines. We encourage you to craft this file yourself to ensure its compatibility with other machines. Avoid relying on `pip freeze` or `conda env export` as they may not always be usable.
- A README file that describes the environment, file structure, and instructions to run your AI

Please also attach a PowerPoint (or OpenOffice) file describing the following information:

- AI Name, Developer's Name(s) and Affiliation(s)
- AI's Outline

### Sample AIs:
---

The source code and model of our deep reinforcement learning blind AI implemented using Python is available [here](https://github.com/TeamFightingICE/BlindAI).

See also the sample AI:
- The sample AI implemented using Python is available [here](https://github.com/TeamFightingICE/pyftg/tree/master/examples).

### Rules:
---
__General Information__

One game has three rounds. The maximum fighting time of a round is 60s. After one round, the character's positions and HPs will be reset, and a new round is started. All games in the competition are conducted with the options "--limithp 400 400" (limit HP of both players to be 400) and "--blind-player 2" (limit access only to sound data for both players) of DareFightingICE.

Even if the game will boot with `--blind-player` option, it is still recommended to notify the game that your AI will process only audio data by modifying `isBlind` method to be as follows:
- For Python,
``` python
def is_blind(self):
   return True
```

The FrameData sent to AIs will have a delay of 15 frames, but AudioData and ScreenData have no delay. Please keep in mind that although FrameData and ScreenData are accessible during AI training phase, in the competition, only **AudioData** is provided to all the participating AIs.

Your AI will be made publicly available and by submitting you will have agreed to this.

__Memory Limit__
- You are allowed to utilize a maximum of 64GB RAM and 32GB VRAM. If your AI exceeds this limit, it will be disqualified.

__Multi-threading__
- You are allowed to utilize multi-threading up to 16 threads (CPU) and employ any GPU computation library.

__File I/O__
- Attempts to read or write files in any other directory than your AI's will lead to disqualification.
- You are allowed to submit up to 64GB, including source code and data (AI model, etc...).

__The "Small Print"__
- We, the organizers of this competition, will do our utmost to ensure the competition is running smoothly and fairly. All our decisions are final.
- We expect participants to uphold the spirit of the competition. Any attempts to cheat will lead to immediate disqualification without appeal. Attempts to cheat include:
- trying to disturbing opponent's controller
- trying to circumvent the competition's security framework
- memory scanning
- trying to corrupt the file system
- trying to disrupt the ongoing competition in any way
- intentionally loosing games or creating specific game states on purpose
- We reserve the right to alter these rules at any time without notice.
If you have any questions or suggestions or encounter any problems, please email us and we will try to address the issue as quickly as possible.
We have made every effort to comply with copyright law. If you should have copyright concerns, please contact us.

The above rules are based on the Ms Pac-Man vs Ghosts League 2012 Competition.

### Evaluation:
---
Two leagues (Standard and Speedrunning) in this competition are described as follows:
The Standard League considers the winner of a round as the one with the HP above zero at the time its opponent's HP has reached zero. Both AIs will be given the initial HP of 400. The league for a given character type is conducted in a round-robin fashion with two games for any pair of entry AIs switching P1 and P2. The AI with highest number of winning rounds becomes the league winner; If necessary, remaining HPs are used for breaking ties. In this league, our weakened sample MctsAi with limited to 23 iterations per frame, played in the non-blind mode or with FrameData, and our [sample deep-learning blind AI](https://github.com/TeamFightingICE/BlindAI), played in the blind mode, will also be participating as baseline AIs.

In the Speedrunning League, the league winner of a given character type is the AI with the shortest average time to beat both of our aforementioned sample AIs. For each entry AI, 5 games are conducted with the entry AI being P1 and a sample AI being P2, and another set of 5 games with the entry AI being P2 and a sample AI being P1. Both AIs will be given the initial HP of 400. If a sample AI of interest cannot be beaten in 60s, the beating time of its opponent entry AI is penalized to 70s. <br>

In this competition, only "Zen" character, with the same [motion data](https://www.ice.ci.ritsumei.ac.jp/~ftgaic/Downloadfiles/Motion/ZEN/Motion.csv) as the motion data included in DareFightingICE will be used.

The ranking rules are as follows:
In each of the two leagues (in this order: Zen Standard, Zen Speedrunning), the AIs are ranked according to the number of winning rounds. If ties exit, their total remaining HPs will be used. Once the AIs are ranked in each league, league points are awarded to them according to their positions using **[the 2018 Formula-1 scoring system ](https://en.wikipedia.org/wiki/2018_Formula_One_World_Championship#Scoring_system)**. The competition winner is finally decided by the sum of league points across all two leagues.

### Evaluation Environments: 
---
1. Software:
   - OS: Linux Ubuntu 20.04.3 LTS (Focal Fossa)
   - OpenJDK: 21.0.2
   - DareFightingICE: 6.3
   - Python: 3.12.3
   - pyftg: 2.1
2. Hardware:
   - CPU: Intel(R) Xeon(R) Gold 6258R CPU @ 2.70GHz
   - RAM: 188 GB
   - GPU: NVIDIA A100 80GB VRAM

### <b>Prizes: (updated on March 26, 2024) </b>
---
IEEE CIS will award the qualified first-place, second-place, and third-place winners a monetary prize of $500, $300, and $200, respectively. For more details on the prize distribution policy, please see this <a href="https://cis.ieee.org/images/files/Documents/competitions/prize-dist-policy.pdf" target="_blank">page</a>.

### Organizers:
---
1. Van Thai Nguyen, Graduate School of Information Science and Engineering, Ritsumeikan University
2. Ibrahim Khan, Graduate School of Information Science and Engineering, Ritsumeikan University
3. Chollakorn Nimpattanavong, Graduate School of Information Science and Engineering, Ritsumeikan University
4. Kantinan Plupattanakit, Graduate School of Information Science and Engineering, Ritsumeikan University
5. Boyu Chuang, Graduate School of Information Science and Engineering, Ritsumeikan University
6. Ruck Thawonmas, College of Information Science and Engineering, Ritsumeikan University

### Submission:
---

Please submit your entry via this [page](https://forms.gle/fCRiRUPvVxYjaT5b9). Below are the deadlines.

Midterm deadline (We recommend you do midterm submission, but if you miss it, you can still submit your AI to us by the final deadline.)
- June 7, 2024 (AoE)

Final deadline
- July 29, 2024 (AoE)(no extension!!)

#### Note that all the submissions will be made publicly available after the competition.

### <b>Contact Us:</b>
---
d-ice-aic at ice dot ci dot ritsumei dot ac dot jp

### <b>DareFightingICE Project Page:</b>
---
Visit this [page](https://tinyurl.com/DareFightingICE). 
