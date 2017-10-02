package com.adneom.testplanpie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by gtshilombowanticale on 02-08-16.
 */
public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo infp = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

        if(infp.getState() == NetworkInfo.State.CONNECTING){
            Log.i("Adneom"," is connecting ");
            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            Log.i("Adneom","informations about wifi : "+wInfo.getBSSID()+","+wInfo.getMacAddress()+","+wInfo.getSSID()+" --- ");
        }

        if(infp.getState() == NetworkInfo.State.CONNECTED){
            Log.i("Adneom"," is connected");
            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            Log.i("Adneom","informations about wifi : "+wInfo.getMacAddress()+" --- ");
        }

        if(infp.getState() == NetworkInfo.State.DISCONNECTED) {
            Log.i("Adneom"," Wi-fi's state is "+infp.isConnected());
        }

    }
}
