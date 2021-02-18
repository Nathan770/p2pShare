package com.example.p2psharelibrary;

import android.net.wifi.p2p.WifiP2pDevice;

public interface ConnectionSatutsCallBack {
    void discoverStatus(boolean connected,int reason);
    void connectionStatus(boolean connected);
    void getPeer(WifiP2pDevice[] wifiP2pDevice);
    void connectedStatus(String statue);
    void messageReciever(byte[] arr,int arg);
}
