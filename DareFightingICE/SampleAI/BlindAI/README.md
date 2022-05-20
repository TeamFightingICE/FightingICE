# Deep Reinforcement Learning Blind AI

This is the source code of Deep Reinforcement Learning Blind AI.

## Installation:
- Install miniconda for python 3.8: https://docs.conda.io/en/latest/miniconda.html.
- Clone the repo: `git clone https://github.com/TeamFightingICE/FightingICE`.
- Create and activate conda env:
  
    ```
    cd DareFightingICE/DareFightingICE/SampleAI/BlindAI
    conda env create -f environment.yml
    conda activate ice
    ```

- Merge all the java files under ```non_delay``` with the source code of DareFightingICE under ```src/aiinterface```.
- Boot the DareFightingICE with option "--py4j --limithp 400 400".
- Run ```train.py``` file to train. e.g ```python torch_train.py --p2 MctsAi65 --encoder fft --id rnn_1_frame_256_mctsai65 --n_frame 1 --recurrent``` or
- Run ```trained_ai/PvJ.py``` to test the Blind AI.

## File Description
- ```train.py``` is a file used to train the Blind AI. Please run ```python train.py -h``` for parameters explanation.
- ```train_ai``` folder contains source code for the trained AI which is used as the sample AI for the AI track. It uses FFT audio encoder and GRU. All weights are stored in ```trained_model```.
- ```non_delay``` folder contains modifications of ```AIController.java``` and ```AIInterface.java```. They make DareFightingICE with no frame delay.
- ```visualize.py``` is used to visualize the learning curve and calculate area under the learning curve.
- ```analyze_fight_result.py``` is used to calculate the win ratio and average HP difference between the Blind AI and MctsAi65.

## Get sound design evaluation metrics
- After finishing designing sound, please run the following command to train the Blind AI:
  ```python torch_train.py --p2 MctsAi65 --encoder fft --id {experiment_id} --n_frame 1 --recurrent```, you can decide ```experiment_id``` on your own
- After training, a result file with the name ```result_fft_{experiment_id}_rnn.txt``` is created. Please run ```visualize.py``` as follows: ```python visualize.py --file result_fft_{experiment_id}_rnn.txt --title FFT```. A plot will be shown and the area under the learning curve will be printed out.
- Before testing the performance of the Blind AI against MctsAI65, please remove all the files under ```log/point``` of DareFightingICE.
- Please revise ```path``` parameter of the Blind AI in the line number 22 of ```trained_ai/PvJ.py``` to your trained model location.
- Run ```trained_ai/PvJ.py``` to begin testing.
- After testing, please run ```analyze_fight_result.py```. Win ratio and average HP difference will be printed out.
## Tested Environment
- Intel(R) Xeon(R) W-2135 CPU @ 3.70GHz   3.70 GHz
- 16.0 GB RAM
- NVIDIA Quadro P1000 GPU
- Windows 10 Pro
- Python 3.8
- DareFightingICE 5.2 (from the project's Github)

## Deep learning library in use:
- pytorch 1.11.0
- torchaudio 0.11.0
- torchvision 0.12.0