# p2pShare
p2p library

[![](https://jitpack.io/v/Nathan770/p2pShare.svg)](https://jitpack.io/#Nathan770/p2pShare)

A library for p2p , you can send a bytes array.


<img src="https://github.com/Nathan770/p2pShare/blob/master/p2pShareExplication.gif"  width="512">


## Setup 

Step 1. Add the JitPack repository to your build.gradle file
```
allprojects {
		repositories {
		
			maven { url 'https://jitpack.io' }
		}
	}
```

Step 2. Add the dependency :
```
dependencies {
 	        implementation 'com.github.Nathan770:p2pShare:1.00.00'
	}
```

## Usage

###### StepProgress Constructor:
```java

public class MainActivity extends AppCompatActivity implements ConnectionSatutsCallBack {
    private static final String TAG = "EasyTextApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	      
        //init
	      p2pClass = new p2pClass(this,getIntent());
        p2pClass.run();
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
        // The list of all device at your wifi
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

    }
     
}     		
```
## License

    Copyright 2020 Nathan Amiel & Vadim Kandaurov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## Credits

Icon made by Flat Icons (www.flat-icons.com) from www.flaticon.com
