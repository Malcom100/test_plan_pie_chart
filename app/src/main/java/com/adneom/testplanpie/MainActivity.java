package com.adneom.testplanpie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.adneom.testplanpie.database.MyBaseSQLite;
import com.adneom.testplanpie.database.UserBDD;
import com.adneom.testplanpie.gps.FDGPSTracker;
import com.adneom.testplanpie.models.MySensorsTrigger;
import com.adneom.testplanpie.models.TypesTrigger;
import com.adneom.testplanpie.models.User;
import com.txusballesteros.widgets.FitChart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private MyBaseSQLite baseSQLite;
    private static final int VERSION_BDD = 1;

    //
    private FitChart fitChart1;
    private FitChart fitChart2;
    private  TextView score1;
    private TextView score2;
    private Button timeOne;
    private Button timeTwo;

    //sensor:
    float[] acceleromterVector=new float[3];
    float[] magneticVector=new float[3];
    float[] resultMatrix=new float[9];
    float[] values=new float[3];
    private SensorManager sensorManager;
    private Sensor gyroscope,accelerometer,magnetic;
    Handler handler;
    int interval= 1000;
    boolean flag = false;
    boolean firstTimeAxisX = false, firstTimeAxisZ = false, firstTimeAaxisY = false;

    private final Runnable processSensors = new Runnable() {
        @Override
        public void run() {
            // Do work with the sensor values.

            flag = true;
            // The Runnable is posted to run again here:
            handler.postDelayed(this, interval);
        }
    };

    //timer:
    Chronometer testChronometer;
    int timeTotal;

    //list to save values :
    List<MySensorsTrigger> listSensors= new ArrayList<MySensorsTrigger>();

    //Locaion :
    FDGPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        UtilsTest.isUnlock = false;

        //chronometer:
        testChronometer = new Chronometer(this);
        testChronometer.setBase(SystemClock.elapsedRealtime());
        testChronometer.start();
        //reset to zero :
        timeTotal = 0;
        UtilsTest.lastDate = new Date();

        //sensor:
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // you can initialize this wherever you want...
        handler = new Handler();


        fitChart1 = (FitChart)findViewById(R.id.fitchartone);
        fitChart2 = (FitChart)findViewById(R.id.fitcharttwo);
        score1 = (TextView)findViewById(R.id.score_1);
        score2 = (TextView)findViewById(R.id.score_2);
        timeOne = (Button)findViewById(R.id.button_time_one);
        timeTwo = (Button)findViewById(R.id.button_time_two);

        //min and max:
        fitChart1.setMinValue(0f);
        fitChart1.setMaxValue(100f);
        //by default :
        fitChart1.setValue(82f);
        //min and max:
        fitChart2.setMinValue(0f);
        fitChart2.setMaxValue(100f);
        //by default :
        fitChart2.setValue(78f);

        Date dzte = new Date();
        Date dzte2 = new Date(2016,15,02,24,15);
        Date dzte3 = new Date(2016,15,02,00,15);
        Date dzte4 = new Date(2016,15,02,22,15);
        Log.i("Adneom", " Hour is " + dzte.getHours() + " and " + dzte2.getHours() + " and " + dzte3.getHours() + " then " + dzte4.getHours() + " **");


        //Database :
        testDB();

        //screen :
        registerBroadcastReceiver();

        //location :
        manageLocation();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float y,x,z;
        //fi 1sec and screen is unlock :
        if(flag && UtilsTest.isUnlock){
            MySensorsTrigger mySensorsTrigger = null;

            // pdate the accelerometer's value and the magnetic's value
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                acceleromterVector=event.values.clone();
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticVector=event.values.clone();
            }

            // azimuth
            x =event.values[0];
            // pitch
            y = event.values[1];
            // roll
            z = event.values[2];

            //Log.i("Adneom","("+x+","+y+","+z+")");

            //X:
            if(!firstTimeAxisX && Math.abs(x) > 5f && Math.abs(y) > 6f && Math.abs(z) > 0f){
                //Log.i("Adneom"," --- gauche/droite is "+x+" seconde is "+testFormattedTimer()+" --- ");
                // vibration for 1 sec.
                ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(1000);
                mySensorsTrigger = new MySensorsTrigger(testFormattedTimer(), TypesTrigger.SENSOR,1);
                firstTimeAxisX = true;
            }else
                //boolean becomes FALSE when device comes back to the value 0
            if(Math.abs(x) > 0f && Math.abs(x) < 1f){
                //Log.i("Adneom","value is "+x);
                firstTimeAxisX = false;
            }

            //Z:
            if(!firstTimeAxisZ && Math.abs(x) > 0f && Math.abs(y) > 3f && Math.abs(z) > 6f){
                //Log.i("Adneom"," --- avant/arrière is "+z+" seconde is "+testFormattedTimer()+" --- ");
                // vibration for 1 sec.
                ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(1000);
                mySensorsTrigger = new MySensorsTrigger(testFormattedTimer(), TypesTrigger.SENSOR,1);
                firstTimeAxisZ = true;
            }else
            //boolean becomes FALSE when device comes back to the value 0
            if(Math.abs(z) > 0f && Math.abs(z) < 1f) {
                //Log.i("Adneom","value is "+z);
                firstTimeAxisZ = false;
            }

            //Y:
            if(!firstTimeAaxisY && Math.abs(x) > 0f && Math.abs(y) > 4f && Math.abs(z) > 6f){
                // vibration for 1 sec.
                ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(1000);
                //Log.i("Adneom", " position couché (azimut) is " + y);
                mySensorsTrigger = new MySensorsTrigger(testFormattedTimer(), TypesTrigger.SENSOR,1);
                firstTimeAaxisY = true;
            }else
            if(Math.abs(y) > 0f && Math.abs(y) < 1f){
                firstTimeAaxisY = false;
            }

            //save sensor in list :
            if(mySensorsTrigger != null && listSensors != null){
                listSensors.add(mySensorsTrigger);
            }
        }
        flag = false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private int testFormattedTimer () {
        long time  = SystemClock.elapsedRealtime() - testChronometer.getBase();
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateFormatted = formatter.format(date);
        testChronometer.setText(dateFormatted);
        String spString[] = dateFormatted.split(":");
        Log.i("Adneom"," The current time is "+dateFormatted);

        return (Integer.parseInt(spString[2]));
    }

    private int totalTimeJourney(){
        int hoursTotal  = 0;
        int minutesTotal  = 0;
        //reset to zero :
        timeTotal = 0;

        long time  = SystemClock.elapsedRealtime() - testChronometer.getBase();
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateFormatted = formatter.format(date);
        testChronometer.setText(dateFormatted);
        String spString[] = dateFormatted.split(":");


        int hour = Integer.parseInt(spString[0]);
        //becaus hour begins 1 and not 0 :
        hour = (hour == 1) ? 0 : --hour;
        if(hour > 0 ){
            hoursTotal = (hour *3600);
            //Log.i("Adneom","hoursTotal is "+hoursTotal);
            timeTotal += hoursTotal;
        }

        int minute = Integer.parseInt(spString[1]);
        if(minute > 0 ){
            minutesTotal =(minute* 60);
            //Log.i("Adneom","minutesTtotal is "+minutesTotal);
            timeTotal += minutesTotal;
        }

        //secondes:
        timeTotal += Integer.parseInt(spString[2]);

        //button :
        StringBuilder str = new StringBuilder();
        boolean hourExist = false, minutesExist = false, secondesExists = false;
        if(hour > 0){
            hourExist = true;
            str.append(String.valueOf(hour)+":");
        }else{
            if(minute > 0){
                minutesExist = true;
                str.append(String.valueOf(minute));
            }else {
                if (Integer.parseInt(spString[2]) > 0) {
                    str.append(Integer.parseInt(spString[2]));
                    secondesExists = true;
                }
            }
        }
        if(hourExist){
            str.append(" hour(s)");
        }else{
            if(minutesExist){
                str.append(" min.");
            }else {
                if(secondesExists){
                    str.append(" sec.");
                }
            }
        }
        timeOne.setText(str);

        Log.i("Adneom","The total time is is "+timeTotal);
        return timeTotal;
    }

    @Override
    protected void onPause() {
        Log.i("Adneom", "onPause");
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(processSensors);
        super.onPause();

        testChronometer.stop();
        //Log.i("Adneom", " test my list : " + listSensors + " *** ");
        //if list empty = good behavior so 0% else bad behavior
        double average = (listSensors == null || listSensors.size() == 0) ? 0 : ( (1- ((double)listSensors.size()/totalTimeJourney()) )*100);
        if(average == 0.0) timeOne.setText(" 0 ");
        float newAverage = (float)average;
        int averageText = (int)average;
        Log.i("Adneom", " The average is " + average);
        fitChart1.setValue(newAverage);
        score1.setText(String.valueOf(averageText));
    }



    @Override
    protected void onResume() {
        Log.i("Adneom", "onResume");
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
        testChronometer.setBase(SystemClock.elapsedRealtime());
        handler.post(processSensors);

        //connectivity :
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if( netInfo != null && netInfo.isConnected()){
            Log.i("Adneom","there is a connection");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void testDB() {
        UserBDD userBdd = new UserBDD(this);
        Log.i("Adneom"," create userBDD "+userBdd.toString()+" *** ");

        User user = new User("testthree@gmail.com",new Date());
        Log.i("Adneom", " create user *** ");

        userBdd.open();
        Log.i("Adneom", " create open db ");
        userBdd.insertLivre(user);
        Log.i("Adneom", " insert user in db *** ");

        User userFromBdd = userBdd.getLivreWitheMAIL(user.getEmail());
        Log.i("Adneom", " get user from  " + user.getEmail()+" "+user.getId());
        if(userFromBdd != null){
            Log.i("Adneom"," user from is ok **** ");
            Toast.makeText(this, userFromBdd.toString(), Toast.LENGTH_LONG).show();
        }

        Log.i("Adneom","size is "+userBdd.getUsersCount()+" // ");
        userBdd.getAllUsers();

        userBdd.close();
        Log.i("Adneom", " clo-ose DB *** ");
    }

    //event screen :
    private void registerBroadcastReceiver() {
        final IntentFilter theFilter = new IntentFilter();
        /** System Defined Broadcast */
        theFilter.addAction(Intent.ACTION_SCREEN_ON);
        theFilter.addAction(Intent.ACTION_SCREEN_OFF);

        BroadcastReceiver screenOnOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String strAction = intent.getAction();

                //KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if (strAction.equals(Intent.ACTION_SCREEN_OFF))
                {
                    Log.i("Adneom"," screen off ");
                    UtilsTest.isUnlock = false;
                    Location loc = gpsTracker.getCurrentLocation();
                    Log.i("Adneom","FD GPS Tracker is ("+loc.getLatitude()+","+loc.getLongitude()+") ** ");
                }

            }
        };

        getApplicationContext().registerReceiver(screenOnOffReceiver, theFilter);
    }

    private void manageLocation(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED){

            // parameters : context, the permission(s), and the value of request code if user accepts the permission(s)
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    3);
        }
        gpsTracker = new FDGPSTracker(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i("Adneom","request code is "+requestCode);
    }

}
