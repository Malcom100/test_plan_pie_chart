package com.adneom.testplanpie;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by gtshilombowanticale on 10-08-16.
 */
public class UtilsTest {

    public static Boolean isUnlock;
    public static Date lastDate;

    public static void testDifference(){
        Date currentDate = new Date();
        Log.i("Adneom","Util's date is "+UtilsTest.lastDate+" and current date is "+currentDate+" --- ");
        if(currentDate != null && lastDate != null){
            long diffInMillies = ( currentDate.getTime() - lastDate.getTime() );
            long diffSeconds = diffInMillies / 1000 % 60;
            long diffMinutes = diffInMillies / (60 * 1000) % 60;
            long diffHours = diffMinutes / (60 * 60 * 1000);
            int diffInDays = (int) ((currentDate.getTime() - lastDate.getTime()) / (1000 * 60 * 60 * 24));
            Log.i("Adneom"," *** "+diffHours+":"+diffMinutes+":"+diffSeconds+" and number of day(s) is "+diffInDays);
        }
    }
}
