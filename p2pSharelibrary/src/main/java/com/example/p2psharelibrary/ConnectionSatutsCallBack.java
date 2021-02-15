package com.example.p2psharelibrary;

import android.net.wifi.p2p.WifiP2pDevice;

public interface ConnectionSatutsCallBack {
    void connectionStatus(boolean connected);
    void getPeer(WifiP2pDevice[] wifiP2pDevice);
}
