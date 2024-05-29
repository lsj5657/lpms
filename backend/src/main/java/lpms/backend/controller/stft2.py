import pandas as pd
import numpy as np
from scipy import signal
import matplotlib.pyplot as plt
import matplotlib
import matplotlib.cm as cm
import sys

def stft():
    v101 = list(map(float, sys.stdin.readline().strip().split(',')))
    N = len(v101)
    dt = 0.000005
    fs = N*10
    nFFT = 128

    maxval = max(v101, key=abs)
    central_ind = v101.index(maxval)

    l = max(0, central_ind-500)
    r = min(l+1000, N)
    FrameSize = r-l
    v101 = np.array(v101[l:r])

    # FFT
    win = signal.windows.hann(nFFT)
    SFT = signal.ShortTimeFFT(win=win, hop=1, fs=fs, scale_to='magnitude', phase_shift=0)
    zData = SFT.stft(v101, p0=0, p1=FrameSize)
    absZ = np.abs(zData)
    xData = np.arange(1, FrameSize+1)*SFT.delta_t
    yData = SFT.f

    colorMax, colorMin = absZ.max(), absZ.min()
    # Black #000000
    # magenta #c20078
    # Blue #0343df
    # Cyan #00ffff
    # Green #15b01a
    # yellow #ffff14
    # OrangeRed #fe420f
    # Red #e50000
    # White #ffffff
    colors = ['#000000', '#c20078', '#0343df', '#00ffff', '#15b01a', '#ffff14', '#fe420f', '#e50000', '#ffffff']

    norm = plt.Normalize(colorMin, colorMax)
    cmap = matplotlib.colors.LinearSegmentedColormap.from_list("", colors)

    levels = [0, colorMin]
    for i in range(1, 8): levels.append(colorMin + i * (colorMax - colorMin) / 9)
    levels.append(colorMax)

    colormapping = cm.ScalarMappable(norm=norm, cmap=cmap)

    margin = 0.09
    sz = 9
    fig, ax = plt.subplots(
        2, 3, gridspec_kw={"width_ratios": [0.9, 9, 0.1], "height_ratios": [9, 1]}
    )

    xD = absZ.sum(axis=1)
    xD.resize(nFFT)
    yD = np.arange(1, nFFT+1)*SFT.delta_f
    ax[0,0].plot(xD, yD, color='green')
    ax[0,0].set_ylim([0, 25000])

    contour = ax[0,1].contourf(xData, yData, absZ, levels=levels, cmap=cmap, norm=norm)
    ax[0,1].axis("off")
    ax[0,1].set_ylim([0, 25000])

    t = np.arange(1, FrameSize+1)*SFT.delta_t
    ax[1,1].plot(t, v101, color='green')

    fig.delaxes(ax[1,0])
    fig.delaxes(ax[1,2])
    fig.set_figheight(sz)
    fig.set_figwidth(sz)

    cbar = fig.colorbar(contour, cax=ax[0, 2], orientation='vertical', spacing='proportional')
    plt.subplots_adjust(wspace=margin, hspace=margin)
    plt.savefig('hi.png', dpi=400)

# file_name = 'LPMS_E_0_0.00_20230215142031666_RAW.csv'
# df = pd.read_csv(file_name)
# v101 = df['V-101']
# v101 = list(v101)
stft()