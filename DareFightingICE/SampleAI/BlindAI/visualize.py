import argparse
import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns
from sklearn.linear_model import LinearRegression
def draw_curve(file, title):
    with open(file, 'r') as f:
        data_raw_mean = f.readlines()
    # with open('std_fft_0.txt', 'r') as f:
    #     data_raw_std = f.readlines()
    data_raw_mean = np.array([float(u.replace('tensor(', '').replace(')', '').strip()) for u in data_raw_mean])
    x = [i for i in range(len(data_raw_mean))]
    model = LinearRegression()
    model.fit(np.expand_dims(x, 1), data_raw_mean)
    plt.plot(data_raw_mean)
    plt.plot(x, model.predict(np.expand_dims(x,1)), color='k')
    plt.title(title + ' ({})'.format(int(data_raw_mean[-1] - data_raw_mean[0])))
    plt.xlabel('Epoch')
    plt.ylabel('HP Difference')
    plt.show()
    print()
def draw_smooth(file, title):
    with open(file, 'r') as f:
        data_raw_mean = f.readlines()
    data_raw_mean = np.array([float(u.replace('tensor(', '').replace(')', '').strip()) for u in data_raw_mean])
    data = []
    for i in range(len(data_raw_mean) - 5):
        data.append(sum(data_raw_mean[i: i+5]))
    x = [i for i in range(len(data))]
    model = LinearRegression()
    model.fit(np.expand_dims(x, 1), data)
    plt.plot(data)
    plt.plot(x, model.predict(np.expand_dims(x,1)), color='k')
    y1 = model.predict(np.expand_dims(x[-1],1))
    y2 = model.predict(np.expand_dims(x[0],1))
    plt.title(title + ' ({})'.format((y1 - y2)[0]))
    plt.xlabel('Epoch')
    plt.ylabel('HP Difference')
    plt.show()
    print()
def draw_polynomial(file, title, degree=4):
    from sklearn.preprocessing import PolynomialFeatures
    with open(file, 'r') as f:
        data_raw_mean = f.readlines()
    data_raw_mean = np.array([float(u.replace('tensor(', '').replace(')', '').strip()) for u in data_raw_mean])
    data = data_raw_mean
    x = [i for i in range(len(data))]
    # poly
    poly_features = PolynomialFeatures(degree=degree)
    X_train_poly = poly_features.fit_transform(np.expand_dims(x,1))
    poly_model = LinearRegression()
    # print(np.expand_dims(X_train_poly[0,:],1))
    poly_model.fit(X_train_poly, data)
    plt.plot(x, data, label='Learning curve')
    plt.plot(x, poly_model.predict(X_train_poly), label='Polynomial Regression')
    y1 = poly_model.predict(np.expand_dims(X_train_poly[-1,:],0))
    y2 = poly_model.predict(np.expand_dims(X_train_poly[0,:],0))
    i = np.poly1d(poly_model.predict(X_train_poly)).integ()
    print('Area under the learning curve is:', i(1) - i(0))
    # plt.title(title + ' ({})'.format((y1 - y2)[0]))
    plt.legend()
    plt.title(title)
    plt.xlabel('Epoch')
    plt.ylabel('HP Difference')
    plt.show()
    
# draw_polynomial("reward_fft_rnn_1_frame_256_mctsai65_rnn.txt", 'FFT', 4)

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--file', type=str, required=True, help='The result file')
    parser.add_argument('--title', type=str, required=True, help='Title of the plot')
    parser.add_argument('--degree', type=int, default=4, help='Polynomial degree')
    args = parser.parse_args()
    print('Input parameters:')
    print(' '.join(f'{k}={v}' for k, v in vars(args).items()))
    draw_polynomial(args.file, args.title, args.degree)
