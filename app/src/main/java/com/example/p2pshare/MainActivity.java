package com.example.p2pshare;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p2psharelibrary.ConnectionSatutsCallBack;
import com.example.p2psharelibrary.p2pClass;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements ConnectionSatutsCallBack{
    private static final String TAG = "nathan";

    //Activity1
    private ImageView      main_IMG_back;
    private MaterialButton main_BTN_discover;
    private ListView       main_LST_device;

    //Activity2
    private TextView       main2_TXT_message;
    private TextView       main2_TXT_conected;
    private MaterialButton main2_BTN_send;
    private EditText       main2_EDT_send;
    private TextView main2_TXT_messageSend;

    private p2pClass p2pClass;
    private WifiP2pDevice[] mWifiP2pDevice;
    private Boolean connect = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_IMG_back = findViewById(R.id.main_IMG_back);
        main_BTN_discover = findViewById(R.id.main_BTN_discover);
        main_LST_device = findViewById(R.id.main_LST_device);

        main2_TXT_message = findViewById(R.id.main2_TXT_message);
        main2_BTN_send = findViewById(R.id.main2_BTN_send);
        main2_TXT_conected = findViewById(R.id.main2_TXT_conected);
        main2_EDT_send = findViewById(R.id.main2_EDT_send);
        main2_TXT_messageSend = findViewById(R.id.main2_TXT_messageSend);

        p2pClass = new p2pClass(this,getIntent());
        p2pClass.run();

        main_BTN_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p2pClass.searchClient();
            }
        });


       main_LST_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               final WifiP2pDevice device = mWifiP2pDevice[position];
               p2pClass.connectToClient(device);
               main2_TXT_conected.setText(device.deviceName);
           }
       });


       main2_BTN_send.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

              String msg = main2_EDT_send.getText().toString();
               main2_TXT_messageSend.setVisibility(View.VISIBLE);
               main2_TXT_messageSend.setText(msg);
              p2pClass.sendToClient(msg.getBytes());
              main2_EDT_send.getText().clear();
           }
       });

    }

    @Override
    public void discoverStatus(boolean connected , int reason) {
        if (connected == true){
            Log.d(TAG, "discoverStatus: start");
            Toast.makeText(this,"Discover start",Toast.LENGTH_SHORT).show();
        }else {
            Log.d(TAG, "discoverStatus: failled " + reason);
            Toast.makeText(this,"Discover failled " + reason,Toast.LENGTH_SHORT).show();
        }
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
        mWifiP2pDevice = wifiP2pDevice;
        Log.d(TAG, "getPeer: before" +wifiP2pDevice.length);
        String[] deviceNameArray = new String[wifiP2pDevice.length];
        if (wifiP2pDevice.length != 0){
            for (int i = 0; i < wifiP2pDevice.length; i++) {
                Log.d(TAG, "getPeer: "+wifiP2pDevice[i].deviceName);
                deviceNameArray[i] = wifiP2pDevice[i].deviceName;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
            main_LST_device.setAdapter(adapter);

        }

    }

    @Override
    public void connectedStatus(String statue) {
        Log.d(TAG, "can send connected: ");

        main_IMG_back.setVisibility(View.INVISIBLE);
        main_BTN_discover.setVisibility(View.INVISIBLE);
        main_LST_device.setVisibility(View.INVISIBLE);

        main2_TXT_conected.setVisibility(View.VISIBLE);
        main2_BTN_send.setVisibility(View.VISIBLE);
        main2_EDT_send.setVisibility(View.VISIBLE);

        if (statue.equals("Client")){
            connect = true;
            Log.d(TAG, "connected: ");
        }
        if (statue.equals("Host")){
            connect = true;
            Log.d(TAG, "connected: ");
        }
    }

    @Override
    public void messageReciever(byte[] arr,int arg) {
        String msg = new String(arr, 0, arg);
        main2_TXT_message.setText(msg);
        main2_TXT_message.setVisibility(View.VISIBLE);
    }

}