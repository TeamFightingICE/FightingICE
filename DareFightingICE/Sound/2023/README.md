
# <div align ="center"> Sound Design Track </div>
----
<div align = "center"> Welcome to the sound design track of DareFightingICE. Here you will be provided all the necessary information needed to participate in the sound design track. Please remember to read carefully and thoroughly before contacting us for any questions you might have. </div>
<br>


### What is Sound Design Track:
----
As you can already tell from the name, you have to make a sound design for the DareFightingICE. Keep in mind that DareFightingICE is targeting visually impaired players, so the sound design you come up with should be made for visually impaired players. You will be provided with a sample sound design and all the sound effects used in the sample. 


### What To Submit:
---
You are provided with a version of DareFightingICE that already has a sample sound design in it. You will be allowed to edit a part of the source code in the game as well as add your own sounds. Doing this gives you the full ability to make your own sound design. 

You will submit:
- The sound effects.
- The source code files you are allowed to edit.
-  A slide file(ppt) explaining the changes and the reasoning behind them. 
-  A tutorial video (maximum length of video is 3 min) featuring the important features of your sound design.
-  Keep in mind that you do not need to edit the source code, you can just change the sounds, but give them the same name as the sounds already in the sound folder. To change the background music just overwrite the “BGM0” audio file with your own background music (name should remain the same). 
-  You are allowed to add new source code files and any technique like procedural content generation to generate sounds. The programming language you are allowed to use is Java or any other languages that can be wrapped with Java. Make sure to put new source files in "DareFightingICE\src" and any data files in 	"DareFightingICE\data". <br>

Source code files you are allowed to edit are:
-	DareFightingICE\src\fighting\Character.java
-	DareFightingICE\src\manager\SoundManager.java
-	DareFightingICE\src\render\audio\SoundRender.java <br>

Directory for sound effects:
-	DareFightingICE\data\sounds <br>
~~~
For this competition, you will only need to come up with the sound design for a single character (Zen).
All the sound effects must be in .wav format. The sound effects should be mono unless you have changed the source code. 
~~~

### Sample Sound Design:
---
The sample sound design is made using openAL from the Lightweight Java Game Library (lwjgl). There is a total of 51 sound effects in the sounds folder including the background music. For the sample sound design, some sound effects are the same for similar moves. To know in detail what the moves look like visit this [link](https://www.ice.ci.ritsumei.ac.jp/~ftgaic/ZEN_action_animations.htm).<br>
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
- If you add any new sound (special sound effect) which is not in the sample sound design. You are asked to give a reason for the addition.
- If any sound effect is found not to be copyright free you will be contacted to change and resubmit before the deadline. If you are not able to resubmit before the deadline, your submission will be discarded. Your submission will also be discarded if your sounds are found not to be copyright free at any stage of the competition, even if the results are out and the participant below you will take your place. 
- Your sound design will be made publicly available and by submitting you will have agreed to this.

### Evaluation:
---
Evaluation of your sound designs will be done as follows: <br>
Normal players (players with vision) will test your sound design while wearing a blindfold. The total number of players testing your sound design is not static, but rest assured it will not be a very small number, like 3,4, etc. If the total number of sound designs is more than 5, the sound designs will go through the Pre-Screening process and the top 5 will be selected. The top 5 will then go through the screening process. If the number is 5 or lower, the pre-screening process will be skipped.<br>

Pre-screening:
- Two most capable normal test players will play against each other for one round using the default sound design. The replay of the same round will be played using all the submitted sound designs – all videos will be of the same match but different sound designs.
- A sound aesthetic survey will be conducted targeting general respondents. The result of this survey will determine the top five sound designs.

Screening:
- The blindfolded players will play against a [weak AI](../SampleAI/MctsAi65) (a weak form of [MCTS AI](http://www.ice.ci.ritsumei.ac.jp/~ftgaic/Downloadfiles/2018_Sample_AIs.zip)), the reason for the AI being weak is that these players are not visually impaired and are not used to playing a game blindfolded.
- Each player will play against the AI 3 games (3 rounds per game, with initial HP of 400 and the maximum round time of 60 seconds) for a sound design, and the score will be calculated by health point (HP) difference between the player and the AI, in relative to the HP difference when playing without being blindfolded.
- After the play, they (the players) will be asked to complete a sound aesthetic survey. The results of this survey will also be counted in the final score of your sound design.
- In addition, [our sample blind deep-learning (DL) AI](../SampleAI/Deep%20Learning%20AI) will be newly trained with each sound design and then play the game (3 games per sound design) against the same weak MCTS AI.
-  The sample blind DL AI's learning curve, win-lose ratio, and HP difference will also be used in the total score. In the end, the sound design with the highest overall score will win. <br>


### Instructions About Sound Design:
---
- To understand the working of our sample sound design please read this [guide](https://github.com/TeamFightingICE/FightingICE/blob/fe92d5dd5bfdd9e503a2fc111fb9457fa292d0a5/DareFightingICE/Sound/2023/Guides/Instructions%20and%20Tips.pdf?raw=true).
- For tips on how to change the sample sound design or create a new one, follow this [guide](https://github.com/TeamFightingICE/FightingICE/blob/fe92d5dd5bfdd9e503a2fc111fb9457fa292d0a5/DareFightingICE/Sound/2023/Guides/Instructions%20on%20Sound%20Design.pdf?raw=true).

### Installation Guide:
---
To install DareFightingICE please follow the instructions on this [link](https://www.ice.ci.ritsumei.ac.jp/~ftgaic/index-2.html).


### Submission:
---
Please submit your entry via email to ftg dot aic at gmail dot com with an email subject of "2022_Sound". In case the attached file's size is larger than 5.0 MB, please add a link in the email for us to download it. Below are the deadlines.<br>
<br>
Midterm deadline (We recommend you do midterm submission, but if you miss it, you can still submit your sound design to us by the final deadline.) <br>
- May 24, 2022 (23:59 JST)

Final deadline
- July 29, 2022 (23:59 JST)(no extension!!)









