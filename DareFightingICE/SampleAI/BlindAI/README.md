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