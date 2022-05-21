import argparse
import pandas as pd
import os

def calculate(path):
    files = [path + '\\' + f for f in os.listdir(path)]
    dfs = []
    for f in files:
        df = pd.read_csv(f, header=None)
        dfs.append(df)
    data_all = pd.concat(dfs, axis=0, ignore_index=True)
    data_all.columns = ['Round', 'P1', 'P2', 'Time']
    win_ratio = sum(data_all.P1 > data_all.P2) / data_all.shape[0]
    hp_diff = sum(data_all.P1 - data_all.P2) / data_all.shape[0]
    return win_ratio, hp_diff

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--path', type=str, required=True, help='The directory containing result log')
    args = parser.parse_args()
    win_ratio, hp_diff = calculate(args.path)
    print('The winning ratio is:', win_ratio)
    print('The average HP difference is:', hp_diff)
