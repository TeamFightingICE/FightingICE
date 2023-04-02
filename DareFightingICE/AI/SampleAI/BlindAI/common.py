BASE_CHECKPOINT_PATH = 'ppo_pytorch\checkpoints'
STATE_DIM = {
    1: {
        'conv1d': 160,
        'fft': 512,
        'mel': 2560
    },
    4: {
        'conv1d': 64,
        'fft': 512,
        'mel': 1280
    }
}
