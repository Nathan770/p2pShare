package com.example.p2psharelibrary;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static android.os.Looper.getMainLooper;


public class p2pClass {


    private static final String TAG = "p2pClass";
    static final int MESSAGE_READ = 1;


    private final Context context;
    private final Intent intent;

    private WifiManager wifiManager;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

    private String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    private ServerClass serverClass;
    private ClientClass clientClass;
    private SendReceive sendReceive;

    private ConnectionSatutsCallBack connectionSatutsCallBack;

    public p2pClass(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    public void run() {

            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mManager.initialize(context, getMainLooper(), null);

            mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

            context.registerReceiver(mReceiver, mIntentFilter);

            connectionSatutsCallBack = (ConnectionSatutsCallBack) context;
    }

    public void searchClient() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: Discovery Started");
                connectionSatutsCallBack.discoverStatus(true,770);
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "onSuccess: Discovery Started failled "+reason);
                connectionSatutsCallBack.discoverStatus(false,reason);
            }
        });

    }

    public void connectToClient(WifiP2pDevice client){
        final WifiP2pDevice device = client;
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();

                connectionSatutsCallBack.connectionStatus(true);
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(context, "Not Connected reason = " + reason, Toast.LENGTH_SHORT).show();
                connectionSatutsCallBack.connectionStatus(false);
            }
        });
    }

    public void sendToClient(byte[] bytes ){
        sendReceive.write(bytes);
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            Log.d(TAG, "onPeersAvailable: ");
            if (!peerList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];

                int index = 0;
                for (WifiP2pDevice device : peerList.getDeviceList()) {
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }
                connectionSatutsCallBack.getPeer(deviceArray);
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress groupOwnerAddress = info.groupOwnerAddress;
            if (info.groupFormed && info.isGroupOwner) {
                Log.d(TAG, "onConnectionInfoAvailable: Host");
                serverClass = new ServerClass();
                serverClass.start();
                connectionSatutsCallBack.connectedStatus("Host");
            } else if (info.groupFormed) {
                Log.d(TAG, "onConnectionInfoAvailable: Client");
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
                connectionSatutsCallBack.connectedStatus("Client");
            }


        }
    };

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Log.d(TAG, "handleMessage: "+msg);
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    connectionSatutsCallBack.messageReciever(readBuff,msg.arg1);
                    break;
            }
            return true;
        }
    });

    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;


        @Override
        public void run() {
            try {
                Log.d(TAG, "Server class run: ");
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
                Log.d(TAG, "Server class run: finish");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientClass extends Thread {
        Socket socket;
        String hostAdd;

        public ClientClass(InetAddress hostAddress) {
            hostAdd = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                Log.d(TAG, "ClientClass run: start");
                socket.connect(new InetSocketAddress(hostAdd, 8888), 500);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
                Log.d(TAG, "ClientClass run: finish");
            } catch (IOException e) {
                Log.d("pttt", "run: timeout");
                e.printStackTrace();
            }
        }
    }

    private class SendReceive extends Thread {
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt) {
            socket = skt;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            Log.d(TAG, "SendReceive run: Start , socket = " + socket.toString());
            byte[] buffer = new byte[6000000];
            int bytes;

            while (socket != null) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "SendReceiver run: finish");
        }

        public void write(byte[] bytes) {

            Log.d(TAG, " SendReceiver write: start");

            Thread send = new Thread( () -> {
                try {
                    outputStream.write(bytes);
                    Log.d(TAG, "SendReceiver loadInBackground: success");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "SendReceiver loadInBackground: unSuccess "+e.getMessage());
                }
            });

            send.start();
        }
    }

}
