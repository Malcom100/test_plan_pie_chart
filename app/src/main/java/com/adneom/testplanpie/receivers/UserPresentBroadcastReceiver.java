package com.adneom.testplanpie.receivers;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adneom.testplanpie.UtilsTest;

/**
 * Created by gtshilombowanticale on 05-08-16.
 */
public class UserPresentBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        /*Sent when the user is present after
         * device wakes up (e.g when the keyguard is gone)
         * */
        if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            Log.i("Adneom"," user is present after device wakes up *** ");
            UtilsTest.isUnlock = true;

            UtilsTest.testDifference();
        }
    }
}
