# Deep Reinforcement Learning Blind AI

This page contains the source code and model of our deep reinforcement learning blind AI (Blind AI), the details of which are given in this [paper](https://arxiv.org/abs/2205.07444).

## Installation:
- Install miniconda: https://docs.conda.io/en/latest/miniconda.html.
- Clone the repo: `git clone https://github.com/TeamFightingICE/FightingICE`.
- Create and activate conda env:
  
```
cd FightingICE/DareFightingICE/SampleAI/BlindAI
conda env create -f environment.yml
conda activate ice
```

- Boot DareFightingICE with the option `--limithp 400 400 --grpc-auto --non-delay 0`.
- Run the ```train.py``` file to train. e.g ```python train.py --device cuda --p2 MctsAi23i --encoder {conv1d/fft/mel} --recurrent --id rnn_1_frame_mctsai23i --n-frame 1```
- Run the ```test.py``` to test the Blind AI. e.g. ```python test.py --p2 MctsAi23i --encoder {conv1d/fft/mel} --recurrent --id rnn_1_frame_mctsai23i --checkpoint 59```

## Model:
- [Click here.](https://drive.google.com/file/d/1Kz_qzUmcJOAj0B9JfFbTJ1FzRFu8fg0B/view?usp=share_link)<br>

## Performance against MctsAi23i
- Winning ratio: 0.54
- Average HP difference: 18.87

## File Description
- ```train.py``` is a file used to train Blind AI. Please run ```python train.py -h``` for our explanation of the parameters.
- ```test.py``` is a file used for running the trained AI which is used as a sample AI for the AI track.
- ```visualize.py``` is used to visualize the learning curve and calculate the area under the learning curve.

## Get sound design evaluation metrics
- After finishing your sound design, please run the following command to train Blind AI:
  ```python train.py --p2 MctsAi23i --device cuda --encoder {conv1d/fft/mel} --recurrent --id {experiment_id} --n-frame 1```, where you can decide ```experiment_id``` on your own.
- After training, a result file with the name ```result_{encoder}_{experiment_id}_rnn.txt``` is created. Please run the following command to visualize the training result:
  ```visualize.py``` as follows: ```python visualize.py --file result_{encoder}_{experiment_id}_rnn.txt --title {title}```.
- Run ```python test.py --p2 MctsAi23i --encoder {conv1d/fft/mel} --recurrent --id {experiment_id} --checkpoint 59``` to begin testing.
- After the game is completed, both the win ratio and the average difference in HP will be displayed.

## Tested Environment
- Intel(R) Xeon(R) W-2135 CPU @ 3.70GHz
- 16.0 GB RAM
- NVIDIA Quadro P1000 GPU
- Windows 10 Pro for Workstation
- Python 3.10
- DareFightingICE 6.0

## Deep learning libraries in use:
- pytorch-cuda 11.8
- pytorch 2.0.0
- torchaudio 2.0.0
- torchvision 0.15.0
