

# <div align ="center"> 2025 DareFightingICE Sound-Design Competition </div>

----
<div align = "center"> Welcome to the DareFightingICE sound design competition. Here, you will be provided all the necessary information needed to participate in the sound design competition. Please remember to read carefully and thoroughly before contacting us for any questions you might have. </div>
<br>
<div align = "center">


</div>

### What is the Sound Design Competition:
----
As you can already tell from the name, you have to make a sound design for
DareFightingICE. Here, we define a sound design as a set of sound effects combined with the source code that implements their timing-control algorithm. Keep in mind that DareFightingICE is targeting visually impaired players, so the sound design you come up with should be made for visually impaired players. You will be provided with a sample sound design and all the sound effects used in the sample. **Click the image below to watch our promotional video.**

[![Watch the video](./Images/Logo.png)](https://youtu.be/lwfJV-8Ttig)


### What To Submit:
---
You are provided with our new generative sound AI interface that already has a sample sound design in it. You will be allowed to edit the source code as well as add your own sounds. Doing this gives you the full ability to make your own sound design. This new generative sound AI interface gives you the ability to create your own sound generative AI for DareFightingICE. 

You will submit:
- The sound effects.
- The source code files (if changed).
-  A slide file(ppt) explaining the changes and the reasoning behind them. 
-  A tutorial video (maximum length of video is 3 min) featuring the important features of your sound design (optional).
-  Keep in mind that editing the source code is not required. You can just change the sounds, but give them the same name as the sounds already in the sound folder. To change the background music just overwrite the “BGM0” audio file with your own background music (name should remain the same). 
-  You are allowed to add new source code files and any technique like procedural content generation to generate sounds. The programming language you are allowed to use is Python or any other language (for languages other than Python the participant will have to implement the interface themselves). <br>

Source code files you are allowed to edit will be found [here](https://github.com/TeamFightingICE/Generative-Sound-AI). <br>

Directory for sound effects:
-	Generative-Sound-AI\data\sounds <br>
~~~
For this competition, you will only need to come up with the sound design for a single character (Zen).
The sound effects should be in mono audio format unless you have changed the source code.
~~~

### Sample Sound Design:
---
The sample sound design (available in the Generative-Sound-AI/data/sounds [here](https://github.com/TeamFightingICE/Generative-Sound-AI/tree/main/data/sounds)) is the winner sound design from 2024 competition. There is a total of 51 sound effects in the sounds folder including the background music. For the sample sound design, some sound effects are the same for similar moves. To know in detail what the moves look like visit this [link](https://www.ice.ci.ritsumei.ac.jp/~ftgaic/ZEN_action_animations.htm).<br>
There are a few special sound effects added into the DareFightingICE.
-	<b>Heartbeat:</b> This sound effect is played when the player’s health is below 50. For player 1 the sound effect is played on the left speaker and for player 2 on the right.
-	<b>Energy Increase:</b> This sound effect is played when the player’s energy goes +50 from the previous value. For player 1 the sound effect is played on the left speaker and for player 2 on the right.
-	<b>Border Alert:</b> This sound is played when a player reaches the end of the stage on either side. The sound is played on the left side when a player reaches the end of the stage on the left side and the same for the right side.
<br>
These three special sounds are in place to help visually impaired players be aware of their and their opponent’s health and energy status. Also, it will help make them aware of their surroundings.


### Rules
---
- The sounds you submit must be copyright-free (legally made by yourself or use copyright-free sounds). Here copyright-free means [CC0](https://creativecommons.org/share-your-work/public-domain/cc0/) licence.
- For all the sound effects you have changed, please make a list explaning if you came up with the sound yourself or you used an already exsisting free sound (CC0). In case of already exsisting free sound, please provide the reference. 
- One team can only submit one sound design.
- Participants must not submit a previous year’s entry without modification.
- If you add any new sound (special sound effect) which is not in the sample sound design. You are asked to give a reason for the addition.
- If any sound effect is found not to be copyright free you will be contacted to change and resubmit before the deadline. If you are not able to resubmit before the deadline, your submission will be discarded. Your submission will also be discarded if your sounds are found not to be copyright free at any stage of the competition, even if the results are out and the participant below you will take your place. 
- Your sound design will be made publicly available and by submitting you will have agreed to this.

### Evaluation:
---
Evaluation of your sound designs will be done as follows: <br>
Players with vision will test your sound design while wearing a blindfold. The total number of players testing your sound design is not fixed. If the total number of sound designs is more than 10, the sound designs will go through the Pre-Screening process and the top 5 will be selected. The top 5 will then go through the screening process. If the number is 10 or lower, the pre-screening process will be skipped.<br>

Pre-screening:
- For each submitted sound design, a video of our sample AI agents playing one game ( three rounds) of DareFightingICE will be recorded. Then a sound aesthetic survey of these videos will be conducted targeting general respondents. The result of this survey will determine the top five sound designs.

Screening:
- The blindfolded players will play against a weak AI (a weak form of MCTS AI, the reason for the AI being weak is that these players are not visually impaired and are not used to playing a game blindfolded.
- For a sound design, each player will play against the AI for three games (three rounds per game, with an initial HP of 400 and a maximum round time of 60 seconds), and the score will be calculated by the health point (HP) difference between the player and the AI as well as the win ratio relative to playing without being blindfolded.
- After the play, they (the players) will be asked to complete a sound aesthetic survey. The results of this survey will also be counted in the final score of your sound design.
-  In addition, [our deep reinforcement learning blind AI](https://github.com/TeamFightingICE/BlindAI) will be newly trained with each sound design and then play the game (30 games or 90 rounds per sound design) against the same weak MCTS AI.
-  The sample blind AI's win-lose ratio and HP difference will also be used in the total score. In the end, the sound design with the highest overall score will win. <br>


### Details and Tips:
---
- To understand the working of our sound generative AI interface please read this [guide](./Guide/Details-of-the-Generative-Sound-AI-Interface.pdf).

### Installation Guide:
---
- To install DareFightingICE please visit this [link](https://github.com/TeamFightingICE/FightingICE/releases).
- To install our sound generative AI, please visit this [link](https://github.com/TeamFightingICE/Generative-Sound-AI)
- To run DareFightingICE with sound and for sound to transmit to AI agents, please boot the sound generative AI after DareFightingICE.

### <b>Prizes</b>
---
The awards for the first, second, and third place winners will include a monetary prize, with the amount to be announced later, pending the acceptance of our competition in the 2025 IEEE Conference on Games.

### Organizers:
---
  
1. Ibrahim Khan, Graduate School of Information Science and Engineering, Ritsumeikan University
1. Van Thai Nguyen, Graduate School of Information Science and Engineering, Ritsumeikan University
1. Zhang Menghan, Graduate School of Information Science and Engineering, Ritsumeikan University
1. Yuchen Tian, Graduate School of Information Science and Engineering, Ritsumeikan University
1. Ruck Thawonmas, College of Information Science and Engineering, Ritsumeikan University


### Submission:
---
Please submit your entry via this [page available soon]. Below are the deadlines.

Midterm deadline (We recommend you do midterm submission, but if you miss it, you can still submit your sound design to us by the final deadline.)
- June 7, 2025 (AoE)

Final deadline
- August 7, 2025 (AoE)(no extension!!)

#### Note that all the submissions will be made publicly available after the competition.

### <b>Contact Us:</b>
---
d-ice-sdc at ice dot ci dot ritsumei dot ac dot jp

### <b>DareFightingICE Project Page:</b>
---
Visit this [page](https://tinyurl.com/DareFightingICE). 







