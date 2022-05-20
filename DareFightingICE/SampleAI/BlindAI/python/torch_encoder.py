from abc import ABC, abstractmethod
import torch
from torch import nn
import torchaudio
import torch.nn.functional as F


class BaseEncoder(nn.Module, ABC):
    def __init__(self, sampling_rate=48000, fps=60, frame_skip=4):
        super(BaseEncoder, self).__init__()
        self.sampling_rate = sampling_rate
        self.FPS = fps
        self.frame_skip = frame_skip

    def forward(self, x):
        # left side
        left = x[:, :, 0]
        left = self.encode_single_channel(left)
        # right side
        right = x[:, :, 1]
        right = self.encode_single_channel(right)
        print('single', right.shape)
        return torch.cat((left, right), dim=1)

    @abstractmethod
    def encode_single_channel(self, data):
        pass

class SampleEncoder(BaseEncoder):
    def encode_single_channel(self, data):
        return data

class RawEncoder(BaseEncoder):
    def __init__(self, sampling_rate=48000, fps=60, frame_skip=4):
        super(RawEncoder, self).__init__(sampling_rate, fps, frame_skip)
        self.num_to_subsample = 8
        self.num_samples = (self.sampling_rate / self.FPS) * self.frame_skip
        assert int(self.num_samples) == self.num_samples

        # Encoder (small 1D conv)
        self.pool = torch.nn.MaxPool1d(2)
        self.conv1 = torch.nn.Conv1d(1, 16, kernel_size=16, stride=8)
        self.conv2 = torch.nn.Conv1d(16, 32, kernel_size=16, stride=8)

    def encode_single_channel(self, data):
        """Shape of x: [batch_size, num_samples]"""
        # Subsample
        x = data[:, ::self.num_to_subsample]

        # Add channel dimension
        x = x[:, None, :]
        x = F.relu(self.conv1(x))
        x = self.pool(x)
        if x.shape[2] >= 24:
            x = self.conv2(x)
            x = self.pool(x)
        return x


class MelSpecEncoder(BaseEncoder):
    def __init__(self, sampling_rate=48000, fps=60, frame_skip=4):
        super(MelSpecEncoder, self).__init__(sampling_rate, fps, frame_skip)
        self.window_size = int(self.sampling_rate * 0.025)
        self.hop_size = int(self.sampling_rate * 0.01)
        self.n_fft = int(self.sampling_rate * 0.025)
        self.n_mels = 80

        self.mel_spectrogram = torchaudio.transforms.MelSpectrogram(
            sample_rate=self.sampling_rate,
            n_mels=80,
            n_fft=self.n_fft,
            win_length=self.window_size,
            hop_length=self.hop_size,
            f_min=20,
            f_max=7600,
        )

        # Encoder
        self.conv1 = torch.nn.Conv2d(1, 16, 3, padding=1)
        self.conv2 = torch.nn.Conv2d(16, 32, 3, padding=1)
        self.pool = torch.nn.MaxPool2d(2, 2)

    def encode_single_channel(self, data):
        x = torch.log(self.mel_spectrogram(data) + 1e-5)
        x = torch.reshape(x, (x.shape[0], 1, x.shape[1], x.shape[2]))
        x = F.relu(self.conv1(x))
        x = self.pool(x)
        x = F.relu(self.conv2(x))
        if x.shape[-1] >= 2:
            x = self.pool(x)
        return x


class FFTEncoder(BaseEncoder):
    def __init__(self, sampling_rate=48000, fps=60, frame_skip=4):
        super(FFTEncoder, self).__init__(sampling_rate, fps, frame_skip)
        self.num_to_subsample = 8
        # ViZDoom runs at 35 fps, but we will get frameskip number of
        # frames in total (concatenated)
        self.num_samples = (self.sampling_rate / self.FPS) * self.frame_skip
        self.num_frequencies = self.num_samples / 2
        assert int(self.num_samples) == self.num_samples
        self.num_samples = int(self.num_samples)
        self.num_frequencies = int(self.num_frequencies)

        self.hamming_window = torch.hamming_window(self.num_samples)

        # Subsampler
        self.pool = torch.nn.MaxPool1d(self.num_to_subsample)

        # Encoder (small MLP)
        self.linear1 = torch.nn.Linear(int(self.num_frequencies / self.num_to_subsample), 256)
        self.linear2 = torch.nn.Linear(256, 256)

    def _torch_1d_fft_magnitude(self, x):
        """Perform 1D FFT on x with shape (batch_size, num_samples), and return magnitudes"""
        # Apply hamming window
        if x.device != self.hamming_window.device:
            self.hamming_window = self.hamming_window.to(x.device)
        x = x * self.hamming_window
        # Add zero imaginery parts
        x = torch.stack((x, torch.zeros_like(x)), dim=-1)
        c = torch.view_as_complex(x)
        ffts = torch.fft.fft(c)
        ffts = torch.view_as_real(ffts)
        # print(ffts)
        # Remove mirrored part
        ffts = ffts[:, :(ffts.shape[1] // 2), :]
        # To magnitudes
        mags = torch.sqrt(ffts[..., 0] ** 2 + ffts[..., 1] ** 2)
        return mags

    def encode_single_channel(self, data):
        """Shape of x: [batch_size, num_samples]"""
        # TODO Torch 1.8 has "torch.fft.fft"
        mags = self._torch_1d_fft_magnitude(data)
        mags = torch.log(mags + 1e-5)

        # Add and remove "channel" dim...
        x = self.pool(mags[:, None, :])[:, 0, :]
        x = F.relu(self.linear1(x))
        x = F.relu(self.linear2(x))
        return x


if __name__ == '__main__':
    # TODO: compare data value vs pytorch version
    encoder = FFTEncoder()
    import numpy as np
    # torch.Tensor()
    np.random.seed(0)
    data = np.random.randn(1, 3200, 2)
    # print(data)
    # print(encoder(torch.Tensor(data)))
    # print(encoder(torch.Tensor(data)))

