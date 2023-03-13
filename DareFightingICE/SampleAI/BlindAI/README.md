# Deep Reinforcement Learning Blind AI

This page contains the source code and model of our deep reinforcement learning blind AI (Blind AI), the details of which are given in this [paper](https://arxiv.org/abs/2205.07444).

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
- Boot DareFightingICE with the option "--py4j --limithp 400 400".
- Run the ```train.py``` file to train. e.g ```python torch_train.py --p2 MctsAi65 --encoder fft --id rnn_1_frame_256_mctsai65 --n_frame 1 --recurrent``` or
- Run the ```trained_ai/PvJ.py``` to test the Blind AI.

## File Description
- ```train.py``` is a file used to train Blind AI. Please run ```python train.py -h``` for our explanation of the parameters.
- ```train_ai``` folder contains source code for the trained AI which is used as a sample AI for the AI track. It uses the FFT audio encoder and GRU. All weights are stored in ```trained_model```.
- ```non_delay``` folder contains modifications of ```AIController.java``` and ```AIInterface.java```. They make DareFightingICE with no frame delay.
- ```visualize.py``` is used to visualize the learning curve and calculate the area under the learning curve.
- ```analyze_fight_result.py``` is used to calculate the win ratio and average HP difference between Blind AI and MctsAi65.

## Get sound design evaluation metrics
- After finishing your sound design, please run the following command to train Blind AI:
  ```python torch_train.py --p2 MctsAi65 --encoder fft --id {experiment_id} --n_frame 1 --recurrent```, where you can decide ```experiment_id``` on your own
- After training, a result file with the name ```result_fft_{experiment_id}_rnn.txt``` is created. Please run ```visualize.py``` as follows: ```python visualize.py --file result_fft_{experiment_id}_rnn.txt --title FFT```. A plot will be shown and the area under the learning curve will be printed out.
- Before testing the performance of the Blind AI against MctsAI65, please remove all the files under ```log/point``` of DareFightingICE.
- Please revise the ```path``` parameter of the Blind AI in line number 22 of ```trained_ai/PvJ.py``` to your trained model location.
- Run ```trained_ai/PvJ.py``` to begin testing.
- After testing, please run ```analyze_fight_result.py```. Both win ratio and average HP difference will be printed out.
## Tested Environment
- Intel(R) Xeon(R) W-2135 CPU @ 3.70GHz   3.70 GHz
- 16.0 GB RAM
- NVIDIA Quadro P1000 GPU
- Windows 10 Pro
- Python 3.8
- DareFightingICE 5.2 (from the project's Github)

## Deep learning libraries in use:
- pytorch 1.11.0
- torchaudio 0.11.0
- torchvision 0.12.0