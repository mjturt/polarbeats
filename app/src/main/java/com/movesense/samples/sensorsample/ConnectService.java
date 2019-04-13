package com.movesense.samples.sensorsample;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsConnectionListener;
import com.movesense.mds.MdsException;
import com.movesense.mds.MdsNotificationListener;
import com.movesense.mds.MdsSubscription;
import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.RxBleDevice;

import java.util.ArrayList;

public class ConnectService extends Service {
    private static final String LOG_TAG = ConnectActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    // MDS
    private Mds mMds;
    public static final String URI_CONNECTEDDEVICES = "suunto://MDS/ConnectedDevices";
    public static final String URI_EVENTLISTENER = "suunto://MDS/EventListener";
    public static final String SCHEME_PREFIX = "suunto://";

    // BleClient singleton
    static private RxBleClient mBleClient;

    // Sensor subscription
    static private String URI_MEAS_ACC_13 = "/Meas/Acc/13";
    private MdsSubscription mdsSubscription;
    private String subscribedDeviceSerial;
    private String MAC;

    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);


    public ConnectService() {
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        ConnectService getService() {
            return ConnectService.this;
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        requestNeededPermissions();

        // Initialize Movesense MDS library
        initMds();

        MAC = intent.getStringExtra("macAddress");
        if (MAC != null && MAC != "") {
            connectToDrum(MAC);

        }
        intent.setAction( "com.example.Broadcast");
        intent.putExtra(MAC, 1);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        localBroadcastManager.sendBroadcast(intent);

        //sendBroadcast(intent);
        return START_NOT_STICKY;
    }


    private RxBleClient getBleClient() {
        // Init RxAndroidBle (Ble helper library) if not yet initialized
        if (mBleClient == null)
        {
            mBleClient = RxBleClient.create(this);
        }

        return mBleClient;
    }

    private void initMds() {
        mMds = Mds.builder().build(this);
    }

    void requestNeededPermissions()
    {
        // Here, thisActivity is the current activity

    }

    private void subscribeToSensor(String connectedSerial) {
        SoundPool snare = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        int snareSoundId = snare.load(this, R.raw.snare, 1);

        SoundPool bass = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        int bassSoundId = bass.load(this, R.raw.bass, 1);

        SoundPool hihat = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        int hihatSoundId = hihat.load(this, R.raw.hihat, 1);

        // Clean up existing subscription (if there is one)
        if (mdsSubscription != null) {
            unsubscribe();
        }

        // Build JSON doc that describes what resource and device to subscribe
        // Here we subscribe to 13 hertz accelerometer data
        StringBuilder sb = new StringBuilder();
        String strContract = sb.append("{\"Uri\": \"").append(connectedSerial).append(URI_MEAS_ACC_13).append("\"}").toString();
        Log.d(LOG_TAG, strContract);

        subscribedDeviceSerial = connectedSerial;

        mdsSubscription = mMds.builder().build(this).subscribe(URI_EVENTLISTENER,
                strContract, new MdsNotificationListener() {
                    @Override
                    public void onNotification(String data) {

                        Intent i = new Intent();
                        i.setAction( "com.example.Broadcast");
                        i.putExtra(MAC, 0);
                        i.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

                        localBroadcastManager.sendBroadcast(i);

                        Log.d(LOG_TAG, "onNotification(): " + data);

                        AccDataResponse accResponse = new Gson().fromJson(data, AccDataResponse.class);
                        if (accResponse != null && accResponse.body.array.length > 0) {
                            if (Math.abs(accResponse.body.array[0].x) > 0.8 ) {
                                if (connectedSerial.equals("191130000253")) {
                                    snare.play(snareSoundId, 1, 1, 0, 0, 1);
                                } else if (connectedSerial.equals("191130000271")) {
                                    bass.play(bassSoundId, 1, 1, 0, 0, 1);
                                } else if (connectedSerial.equals("190430000084")) {
                                    hihat.play(hihatSoundId, 1, 1, 0, 0, 1);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(MdsException error) {
                        Log.e(LOG_TAG, "subscription onError(): ", error);
                        Intent i = new Intent();
                        i.setAction( "com.example.Broadcast");
                        i.putExtra(MAC, -1);
                        i.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

                        localBroadcastManager.sendBroadcast(i);
                    }
                });

    }

    private void connectToDrum(String macAddress) {

        RxBleDevice bleDevice = getBleClient().getBleDevice(macAddress);

        Log.i(LOG_TAG, "Connecting to BLE device: " + bleDevice.getMacAddress());
        mMds.connect(bleDevice.getMacAddress(), new MdsConnectionListener() {

            @Override
            public void onConnect(String s) {
                Log.d(LOG_TAG, "onConnect:" + s);
            }

            @Override
            public void onConnectionComplete(String macAddress, String serial) {
                Log.d(LOG_TAG, "SUCCESS");
                subscribeToSensor(serial);
            }

            @Override
            public void onError(MdsException e) {
                Log.e(LOG_TAG, "onError:" + e);
                Intent i = new Intent();
                i.setAction( "com.example.Broadcast");
                i.putExtra(MAC, -1);
                i.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

                localBroadcastManager.sendBroadcast(i);
                showConnectionError(e);
            }

            @Override
            public void onDisconnect(String bleAddress) {
                Intent i = new Intent();
                i.setAction( "com.example.Broadcast");
                i.putExtra(MAC, -1);
                i.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

                localBroadcastManager.sendBroadcast(i);
                Log.d(LOG_TAG, "onDisconnect: " + bleAddress);
                unsubscribe();
            }
        });
    }

    private void showConnectionError(MdsException e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Connection Error:")
                .setMessage(e.getMessage());

        builder.create().show();
    }

    private void unsubscribe() {
        if (mdsSubscription != null) {
            mdsSubscription.unsubscribe();
            mdsSubscription = null;
        }
        Intent i = new Intent();
        i.setAction( "com.example.Broadcast");
        i.putExtra(MAC, -1);
        i.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        localBroadcastManager.sendBroadcast(i);
        subscribedDeviceSerial = null;

    }





















    @Override
    public void onDestroy() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

}
