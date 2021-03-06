/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



/*
* * Copyright (C) 2015 GT Silicon Pvt Ltd
 *
 * Licensed under the Creative Commons Attribution 4.0
 * International Public License (the "CCBY4.0 License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://creativecommons.org/licenses/by/4.0/legalcode
 *
 * Note that the CCBY4.0 license is applicable only for the modifications
made
 * by GT Silicon Pvt Ltd
 *
 * Modifications made by GT Silicon Pvt Ltd are within the following
comments:
 * // BEGIN - Added by GT Silicon - BEGIN //
 * {Code included or modified by GT Silicon}
 * // END - Added by GT Silicon - END //
*
* */


package com.example.android.openxoblu;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.android.openxoblu.Utilities.REQUEST_PERMISSIONS_LOG_STORAGE;


/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private static final String send = "0x34 0x00 0x34";
    private static final String sys_off = "0x32 0x00 0x32";
    private static final String pro_off = "0x22 0x00 0x22";
    private TextView mdis;
    private TextView x;
    private TextView mstepcount;
    private TextView y;
    private TextView z;
    private Button mStartStopBtn;
    long timeSec3 = 0;
    private int counter = 0;
    private long StepD = 0;
    private Calendar t_origin;
    String finaltime;
    long timeSec;
    private int timer;
    double Avgspeed;
    double speednow = 0;
    private long timeprint;

    int bytes, i, j, step_counter, package_number, package_number_1, package_number_2, package_number_old = 0;
    int[] header = {0, 0, 0, 0};
    byte[] ack = new byte[5];

    double sin_phi, cos_phi;
    double headingVectors[];
    double movementVectors[];
    double scalars[];


    float[] payload = new float[14];
    double[] final_data = new double[3];

    double[] dx = new double[4];

    double[] x_sw = new double[4];

    byte[] temp = new byte[4];
    Vibrator vib;
    double[] delta = {0.0, 0.0, 0.0};
    double distance = 0.0;
    double distance1 = 0.0;
    private String TXDATA;
    long timeSec1 = 0;
    long timeSec2 = 0;
    double avg = 0;
    //variables for processing
    long timeSec6 = 0;
    DecimalFormat df1 = new DecimalFormat("0.00");
    Calendar c, filenameDate;
    SimpleDateFormat sdf;
    byte[] received_data;
    private String mDeviceName;
    private String mDeviceAddress;
    // private String mDeviceName_1;
    //private String mDeviceAddress_1;
    private BluetoothLeService mBluetoothLeService;
    private static BluetoothGattService mService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private static BluetoothGattCharacteristic mNotifyCharacteristic;
    private static BluetoothGattCharacteristic mReadCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    BluetoothLeService mtu = new BluetoothLeService();

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        // Recevie
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG, "ACTION_GATT_CONNECTED");
                mConnected = true;
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG, "ACTION_GATT_DISCONNECTED");
                mConnected = false;
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.d(TAG, "ACTION_GATT_SERVICES_DISCOVERED");
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mtu.exchangeGattMtu(512);

                }
                // BEGIN - Added by GT Silicon - BEGIN //
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //  displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                Log.d(TAG, "ACTION_DATA_AVAILABLE");

                received_data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_TX_VALUE);
                if (received_data != null && received_data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(received_data.length);
                    for (byte byteChar : received_data)
                        stringBuilder.append(String.format("%02X ", byteChar));
                    TXDATA = String.valueOf(stringBuilder.toString().trim());
                    System.out.print("swdr " + TXDATA);
                    // displayData(TXDATA);
                }
                byte[] buffer = received_data;
                //STEP WISE DATA HERE Receive in Buffer
                Log.e(TAG, "UART-data- " + TXDATA);
                int i = 0;
                int j;
                // writetofile( bytestring,byte2HexStr(buffer,64)+"\n" );
                Log.e(TAG, "b2h-data- " + byte2HexStr(buffer, buffer.length));
                for (j = 0; j < 4; j++) {
                    header[j] = buffer[i++] & 0xFF;          //HEADER ASSIGNED
                    Log.e(TAG, "h- " + header[j]);
                }
                for (j = 0; j < 4; j++) {
                    for (int k = 0; k < 4; k++)
                        temp[k] = buffer[i++];
                    payload[j] = ByteBuffer.wrap(temp).getFloat();                //PAYLOAD ASSIGNED //
                }
                //Log.i(TAG, ""+ payload[0]+ "  "+ payload[2]);

                // ++i;++i;                                                // FOR SKIPPING CHECKSUM
                package_number_1 = header[1];
                package_number_2 = header[2];
                ack = createAck(ack, package_number_1, package_number_2);
                writeack(ack);
                package_number = package_number_1 * 256 + package_number_2;        //PACKAGE NUMBER ASSIGNED
                if (package_number_old != package_number) {
                    for (j = 0; j < 4; j++)
                        dx[j] = (double) payload[j];
                    stepwise_dr_tu();
                    // Log.e(TAG, "final data sent" + final_data[0] + " " + final_data[1] + " "+final_data[2]);
                    c = Calendar.getInstance();
                    sdf = new SimpleDateFormat("HHmmss");
                    // long timeSec= (c.getTimeInMillis()-filenameDate.getTimeInMillis());
                    if (timeSec != timeSec1) {
                        timeSec1 = timeSec;
                    }
                    if (distance1 >= 0.05) {
                        timeSec3 = timeSec1 - timeSec2;
                        timeSec6 = timeSec6 + timeSec3;
                        timeSec2 = timeSec1;
                        //  long timeSec5= (c.getTimeInMillis()-filenameDate.getTimeInMillis());
                        step_counter++;
                        DecimalFormat df1 = new DecimalFormat("0.00");
                        DecimalFormat df2 = new DecimalFormat("000");
                        avg = distance / step_counter;
                        speednow = (distance1 * 3.6) / (timeSec3 / 1000);
                        Avgspeed = distance * 3.6 / (timeSec6 / 1000);
                        StepD = timeSec6 / step_counter; //stepDuration

                        StepData stepData = new StepData(final_data[0], final_data[1], final_data[2], distance, step_counter);
                        stepData.setHeading(x_sw[3]);
                        Utilities.writeDataToLog(getApplicationContext(), stepData);

                    }
                    package_number_old = package_number;
                }
                mstepcount.setText(" " + step_counter);
                mdis.setText(" " + df1.format(distance));
                x.setText(" " + df1.format(final_data[0]));///////////x
                y.setText(" " + df1.format(final_data[1]));///////////y
                z.setText(" " + df1.format(final_data[2]));/////////Z
                tvdelta_lati.setText("delta_lati: " + delta_lati);
                tvdelta_longi.setText("delta_longi: " + delta_longi);
                tvdr_lati.setText("dr_lati: " + dr_coordinates[0]);
                tvdr_longi.setText("dr_longi : " + dr_coordinates[1]);
            }
            // END - Added by GT Silicon - END //
        }
    };


    /*  location attributes */
    private TextView longiText;
    private TextView latiText;
    private TextView altiText;
    private double longitude;
    private double latitude;
    private double altitude;

    private TextView tvdelta_lati;
    private TextView tvdelta_longi;
    private TextView tvdr_lati;
    private TextView tvdr_longi;
    private double delta_lati;
    private double delta_longi;
    private double dr_lati;
    private double dr_longi;
    private double dr_coordinates[];


    /* Compass */
    private Compass compass;
    private float currentAzimuth;
    private float[] rawAzimuths;
    private float f_Azimuth;
    private int ra_index = 0;
    private int RA_SIZE = 10;
    private boolean full = false;


    private TextView sotwLabel;
    private TextView filteredAzimuth;



    public static final double E_RADIUS = 6378137;  //  WGS 국제표준
    /* 개선점
     * WGS표준의 지구반지름에서 움직인 z 축의 값만큼을 지구 반지름에서 빼서 다음계산에 적용*/
    private double curRadius = E_RADIUS;

    // BEGIN - Added by GT Silicon - BEGIN //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_chat);
        rawAzimuths = new float[RA_SIZE];
        final Intent intent = getIntent();
        checkAndRequestWriteLog();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mstepcount = (TextView) findViewById(R.id.stepcount);
        mdis = (TextView) findViewById(R.id.dis);
        // mDataField = (EditText) findViewById(R.id.connection_state);
        timerValue = (TextView) findViewById(R.id.timer);
        x = (TextView) findViewById(R.id.X);
        y = (TextView) findViewById(R.id.Y);
        z = (TextView) findViewById(R.id.Z);
        mStartStopBtn = (Button) findViewById(R.id.start_stop_btn);

        /* initialize location attributes*/
        longiText = (TextView) findViewById(R.id.longitude_text);
        latiText = (TextView) findViewById(R.id.latitude_text);
        altiText = (TextView) findViewById(R.id.altitude_text);
        tvdelta_lati = (TextView) findViewById(R.id.delta_lati);
        tvdelta_longi = (TextView) findViewById(R.id.delta_longi);
        tvdr_lati = (TextView) findViewById(R.id.dr_lati);
        tvdr_longi = (TextView) findViewById(R.id.dr_long);


        /* Compass init */
        sotwLabel = (TextView) findViewById(R.id.sotw_label);
        filteredAzimuth = (TextView)findViewById(R.id.filteredazimuth);
        setupCompass();

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
        ActivityCompat.requestPermissions(this, permissions, PackageManager.PERMISSION_GRANTED);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    altitude = location.getAltitude();
                    longiText.setText("longitude: " + longitude);
                    latiText.setText("latitude: " + latitude);
                    altiText.setText("altitude: " + altitude);
                    if (dr_lati == 0 || dr_longi == 0) {
                        dr_lati = latitude;
                        dr_longi = longitude;
                        tvdr_lati.setText("tvdr_lati: " + dr_lati);
                        tvdr_longi.setText("tvdr_longi" + dr_longi);
                    }
//                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        mStartStopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                String buttonText = btn.getText().toString();
                String startText = getResources().getString(
                        R.string.UART_START);
                String stopText = getResources().getString(
                        R.string.UART_STOP);

                if (buttonText.equalsIgnoreCase(startText)) {   //  start중일 때 누르면
                    btn.setText(stopText);
                    byte[] convertedBytes = convertingTobyteArray(send);
                    BluetoothLeService.writeCharacteristicNoresponse(mReadCharacteristic, convertedBytes);
                    startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread, 0);
                        if (mNotifyCharacteristic != null) {
                            prepareBroadcastDataNotify(mNotifyCharacteristic);
                    }

                } else {    //  stop 일때 누르면
                    btn.setText(startText);
                    byte[] pro = convertingTobyteArray(pro_off);
                    byte[] sys = convertingTobyteArray(sys_off);
                    BluetoothLeService.writeCharacteristicNoresponse(mReadCharacteristic, pro);
                    BluetoothLeService.writeCharacteristicNoresponse(mReadCharacteristic, sys);
                    stopBroadcastDataNotify(mReadCharacteristic);
                    timeSwapBuff = 0L;
                    customHandler.removeCallbacks(updateTimerThread);
                }

            }
        });
        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    private void setupCompass() {
        compass = new Compass(this);
        Compass.CompassListener cl = getCompassListener();
        compass.setListener(cl);
    }


    private Compass.CompassListener getCompassListener() {
        return new Compass.CompassListener() {
            @Override
            public void onNewAzimuth(final float azimuth) {
                // UI updates only in UI thread
                // https://stackoverflow.com/q/11140285/444966
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentAzimuth = azimuth;
                        saveAzimuth(azimuth);
                        sotwLabel.setText("azimuth: " + azimuth);
                        f_Azimuth = getFilteredAzimuth();
                        filteredAzimuth.setText("filtered azimuth: " +f_Azimuth);

                    }
                });
            }
        };
    }


    private void saveAzimuth(float raw_azimuth){

        for(int i = 0;i<RA_SIZE;i++){
            rawAzimuths[i++] = raw_azimuth;
        }
        if(RA_SIZE <= ra_index){
            ra_index = 0;
            full = true;
        }
    }

    private float getFilteredAzimuth() {
        if (full) {
            int i;
            float min = 1000, max = -1000;
            int mini = -1, maxi = -1, count = 0;
            float sum = 0.0f;
            float arr[] = new float[RA_SIZE];


            for (i = 0; i < RA_SIZE; i++) {
                arr[i] = rawAzimuths[i];
            }

            for (i = 0; i < RA_SIZE; i++) {
                if (arr[i] < min) {
                    min = arr[i];
                    mini = i;
                }
                if (arr[i] > max) {
                    max = arr[i];
                    maxi = i;
                }
            }

            for (i = 0; i < RA_SIZE; i++) {
                if (mini == i || maxi == i) continue;
                count++;
                sum += arr[i];
            }
            float dir = sum / count;
            return dir;
        }else return rawAzimuths[ra_index];
    }

    @Override
    protected void onStart() {
        super.onStart();
        compass.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "UART RESUME");
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        compass.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.e(TAG, "UART PAUSE");
//        unregisterReceiver(mGattUpdateReceiver);
        compass.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "UART DESTROY");
        unbindService(mServiceConnection);
        stopBroadcastDataNotify(mReadCharacteristic);
        try {
            if (mGattUpdateReceiver != null)
                unregisterReceiver(mGattUpdateReceiver);
        } catch (Exception e) {

        }
        mBluetoothLeService = null;
    }

    // END - Added by GT Silicon - END //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // BEGIN - Added by GT Silicon - BEGIN //

    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataNotify(    //  onCreate
                                        BluetoothGattCharacteristic gattCharacteristic) {
        if ((gattCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            BluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
        }
    }

    void stopBroadcastDataNotify(
            BluetoothGattCharacteristic gattCharacteristic) {
        final int charaProp = gattCharacteristic.getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            if (gattCharacteristic != null) {
                Log.d(TAG, "Stopped notification");
                BluetoothLeService.setCharacteristicNotification(
                        gattCharacteristic, false);
                mNotifyCharacteristic = null;
            }

        }

    }

    // END - Added by GT Silicon - END //


    //  GATT DATA Receive here
    //  initiate GATT Characterists that UUID equals to OBLU
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();
            // BEGIN - Added by GT Silicon - BEGIN //
            if (uuid.equals(SampleGattAttributes.SERVER_UART)) {
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    String uuidchara = gattCharacteristic.getUuid().toString();
                    mReadCharacteristic = gattCharacteristic;
                    if (uuidchara.equalsIgnoreCase(SampleGattAttributes.SERVER_UART_tx)) {
                        Log.e(TAG, "gatt- " + gattCharacteristic);
                        mNotifyCharacteristic = gattCharacteristic;
                        prepareBroadcastDataNotify(mNotifyCharacteristic);
                    }
                }
            }

            // END - Added by GT Silicon - END //

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);

            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }

    static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
// BEGIN - Added by GT Silicon - BEGIN //

    /**
     * Method to convert hex to byteArray
     */
    private byte[] convertingTobyteArray(String result) {
        String[] splited = result.split("\\s+");
        byte[] valueByte = new byte[splited.length];
        for (int i = 0; i < splited.length; i++) {
            if (splited[i].length() > 2) {
                String trimmedByte = splited[i].split("x")[1];
                valueByte[i] = (byte) convertstringtobyte(trimmedByte);
            }

        }
        return valueByte;

    }

    /**
     * Convert the string to byte
     *
     * @param string
     * @return
     */

    private int convertstringtobyte(String string) {
        return Integer.parseInt(string, 16);
    }

    public byte[] createAck(byte[] ack, int package_number_1, int package_number_2) {
        ack[0] = 0x01;
        ack[1] = (byte) package_number_1;
        ack[2] = (byte) package_number_2;
        ack[3] = (byte) ((1 + package_number_1 + package_number_2 - (1 + package_number_1 + package_number_2) % 256) / 256);
        ack[4] = (byte) ((1 + package_number_1 + package_number_2) % 256);
        return ack;
    }

    /* theta: azimuth
     *  */
    public void stepwise_dr_tu() {
        sin_phi = (float) Math.sin(Math.toRadians(currentAzimuth));
        cos_phi = (float) Math.cos(Math.toRadians(currentAzimuth));
        //sin_phi=(float) Math.sin(x_sw[3]);
        //cos_phi=(float) Math.cos(x_sw[3]);

        headingVectors = new double[2];
        movementVectors = new double [2];
        scalars = new double[2];
        double[] delta_coor = new double[2];
        dr_coordinates = new double[2];


        for(int i =0;i<2;i++){
            scalars[i] = dx[i];
        }

        //1. 현재 방향을 알아온다
        headingVectors = MapCalculator.getHeadingVectors(currentAzimuth); //sensor로 받은 raw Azimuth
        //headingVectors = MapCalculator.getHeadingVectors(f_Azimuth); //필터된 Azimuth 기준으로 방향 확인

        //2. 나의 움직임의 스칼라*방향
        movementVectors = MapCalculator.getMovementVectors(headingVectors, scalars);

        //3. 움직임벡터 -> 위도/경도 좌표로 변환 (델타 좌표)
        // delta_Coordinates = MapCalculator.TranslateCoordinates();
        delta_coor = MapCalculator.TranslateCoordinates(movementVectors, latitude);


        //4. 나중위치 = 델타 좌표 + 현재위치
        dr_coordinates = MapCalculator.CalculateMovement(delta_coor, latitude, longitude);


        Log.d(TAG, "stepwise_dr_tu: " + currentAzimuth);
        //Log.i(TAG, "Sin_phi and cos_phi created");

//
//        delta[0] = cos_phi * dx[0] - sin_phi * dx[1];
//        delta[1] = sin_phi * dx[0] + cos_phi * dx[1];
//        delta[2] = dx[2];
//        x_sw[0] += delta[0];
//        x_sw[1] += delta[1];
//        x_sw[2] += delta[2];
//        x_sw[3] += dx[3];
//        final_data[0] = x_sw[0];
//        final_data[1] = x_sw[1];
//
//        final_data[2] = x_sw[2];
//        distance1 = Math.sqrt((delta[0] * delta[0] + delta[1] * delta[1] + delta[2] * delta[2]));
//        distance += Math.sqrt((delta[0] * delta[0] + delta[1] * delta[1]));
//        Log.d(TAG, "stepwise_dr_tu: delta[0]: " + delta[0] + "   delta[1]: " + delta[1]);
//
//        /* longitude, latitude */
//
//        //        curRadius -= delta[2];
//        //  E_RADIUS를 curRadius로 변경
//
//        delta_lati = (360 * delta[0]) / (2 * Math.PI * E_RADIUS);
//        delta_longi = (360 * delta[1]) / (2 * Math.PI * Math.sin(Math.toRadians(90 - Math.abs(latitude))) * E_RADIUS);
//
//
//
//        dr_longi -= delta_longi;
//        dr_lati -= delta_lati;
    }


    ///WRITE ACK to uc
    void writeack(byte[] byteArray) {
        Log.e(TAG, "ackdata " + byte2HexStr(byteArray, 4));
        BluetoothLeService.writeCharacteristicNoresponse(mReadCharacteristic, byteArray);
    }

    //STOP watch Running
    long timeInMilliseconds = 0L;
    private long startTime = 0L;
    long updatedTime = 0L;
    long timeSwapBuff = 0L;
    private Handler customHandler = new Handler();
    private TextView timerValue;
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hr = mins / 60;
            secs = secs % 60;
            // int milliseconds = (int) (updatedTime % 1000);
            timerValue.setText(" " + hr + ":" + mins + ":" + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);

        }
    };

    public static String byte2HexStr(byte[] paramArrayOfByte, int paramInt) {
        StringBuilder localStringBuilder1 = new StringBuilder("");
        int i = 0;
        for (; ; ) {
            if (i >= paramInt) {
                String str1 = localStringBuilder1.toString().trim();
                Locale localLocale = Locale.US;
                return str1.toUpperCase(localLocale);
            }
            String str2 = Integer.toHexString(paramArrayOfByte[i] & 0xFF);
            if (str2.length() == 1) {
                str2 = "0" + str2;
            }
            StringBuilder localStringBuilder2 = localStringBuilder1.append(str2);
            StringBuilder localStringBuilder3 = localStringBuilder1.append(" ");
            i += 1;
        }
    }

    private void checkAndRequestWriteLog() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(DeviceControlActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Utilities.writeHeaderToLog(getApplicationContext());
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_LOG_STORAGE);
            }
        } else {
            Utilities.writeHeaderToLog(getApplication());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_LOG_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Utilities.writeHeaderToLog(getApplicationContext());
                } else {
                    Toast.makeText(DeviceControlActivity.this, "Required storage permission  are disable.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
// END - Added by GT Silicon - END //