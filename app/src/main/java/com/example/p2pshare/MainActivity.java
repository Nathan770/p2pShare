package com.example.p2pshare;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;

import com.example.p2psharelibrary.ConnectionSatutsCallBack;
import com.example.p2psharelibrary.p2pClass;

public class MainActivity extends AppCompatActivity implements ConnectionSatutsCallBack {
    private static final String TAG = "nathan";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        p2pClass p2pClass = new p2pClass(this);
        p2pClass.run();
        p2pClass.searchClient();

    }

    @Override
    public void connectionStatus(boolean connected) {
        if (connected){
            Log.d(TAG, "connectionStatus: connected");
        }else
            Log.d(TAG, "connectionStatus: no connected");
    }

    @Override
    public void getPeer(WifiP2pDevice[] wifiP2pDevice) {
        Log.d(TAG, "getPeer: ");
        if (wifiP2pDevice != null){
            for (int i = 0; i < wifiP2pDevice.length; i++) {
                Log.d(TAG, "getPeer: "+wifiP2pDevice[i].deviceName);
            }
        }

    }
}