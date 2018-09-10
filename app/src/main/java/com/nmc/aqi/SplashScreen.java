package com.nmc.aqi;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

public class SplashScreen extends AppCompatActivity {

    /*private Service mService; //Service Object from your class that extends service.

    public Service getService(){
        return mService;
    }

    public void setService(Service service){
        mService = service;
    }*/

    // GUI Components
    //Bundle bundle;
    private TextView mBluetoothStatus;
    private TextView mReadBuffer;

    private Button showData;

    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;

    String data,
            temp_data,
            hum_data,
            heatIndex_data,
            co_data, co2_data,
            pm_data,
            aqi_data;

    //Intent intentBundle;

    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier


    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status


    //String readMessage;

    /*Context context;
    public SplashScreen(Context context){
        this.context=context;
    }

    public void Update(){
        TextView txtView = (TextView) ((MainActivity)context).findViewById(R.id.humidity);
        txtView.setText("Hello");
    }*/

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mBluetoothStatus = findViewById(R.id.bluetooth_states);
        mReadBuffer = findViewById(R.id.readBuffer);
        mScanBtn = findViewById(R.id.on);
        mOffBtn = findViewById(R.id.off);
        mDiscoverBtn = findViewById(R.id.discover);
        mListPairedDevicesBtn = findViewById(R.id.PairedBtn);
        showData = findViewById(R.id.showData);


        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = findViewById(R.id.paired_devices_List_View);

        // assign model to view
        mDevicesListView.setAdapter(mBTArrayAdapter);
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        

        if (mBTAdapter.isEnabled()) {
            mBluetoothStatus.setText("enabled");
        }
        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[])msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }

                    if(msg.what == CONNECTING_STATUS){
                        if(msg.arg1 == 1)
                            mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
                        else
                            mBluetoothStatus.setText("Connection Failed");
                    }

                    //if(readMessage.split(",")[0].length() < 4)
                    //    return;
                    mReadBuffer.setText(readMessage);

                    data = readMessage;

                    Intent intentBundle = new Intent(SplashScreen.this,MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("mainData",data);

                    intentBundle.putExtras(bundle);
                    startActivity(intentBundle);


                    /*showData.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intentBundle = new Intent(SplashScreen.this,MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("mainData",data);


                            bundle.putString("temp_data",temp_data);
                            bundle.putString("humidity_data",hum_data);
                            bundle.putString("heatIndex_data",heatIndex_data);
                            bundle.putString("co_data",co_data);
                            bundle.putString("co2_data",co2_data);
                            bundle.putString("pm_data",pm_data);
                            bundle.putString("aqi_data",aqi_data);
                            intentBundle.putExtras(bundle);
                            startActivity(intentBundle);
                        }
                    });*/

                }
            }
        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth Not Found");
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }
        else {
                mScanBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bluetoothOn(v);
                    }
                });

                mOffBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        bluetoothOff(v);
                        mReadBuffer.setText("Read Buffer");
                    }
                });

                mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        listPairedDevices(v);
                    }
                });

                mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        discover(v);
                    }
                });
        }
    }

    private void bluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("Enabling Bluetooth...");

            //Delay
            try {
                //set time in milli seconds
                Thread.sleep(7000);

            }catch (Exception e){
                e.printStackTrace();
            }
            if (mBTAdapter.isEnabled())
                Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data){
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                mBluetoothStatus.setText("enabled");
            }
            else
                mBluetoothStatus.setText("disabled");
            mReadBuffer.setText("Read Buffer");
        }
    }

    private void bluetoothOff(View view){

        // turn off
        mBTAdapter.disable();

        //clear the paired device list when adapter is turn off
        mBTArrayAdapter.clear();
        mReadBuffer.setText("Read Buffer");
        mBluetoothStatus.setText("disabled");
        Toast.makeText(getApplicationContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    private void discover(View view){
        // Check if the device is already discovering
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            mBluetoothStatus.setText("discoveryStopped");
            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                mBluetoothStatus.setText("discovering");
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    //Query paired devices
    private void listPairedDevices(View view){
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            if (mDevicesListView != null) {

                /**Overwrite the pairedDevice list
                each time the Adapter is updated*/
                mBTArrayAdapter.clear();
            }
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText(R.string.connecting);
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one

            /**Connect paired device from list*/
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(!fail) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

         //Call this from the main activity to send data to the remote device
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

         //Call this from the main activity to shutdown the connection
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    /*------------------**Services**----------------

    public class DataService extends Service {

        //public static final int JOB_ID = 101;
        /*private Looper mServiceLooper;
        private ServiceHandler mServiceHandler;

        private SplashScreen mApp;
        private Context mContext;*/

        /*// Handler that receives messages from the thread
        private final class ServiceHandler extends Handler {
            public ServiceHandler(Looper looper) {
                super(looper);
            }

            @Override
            public void handleMessage(Message msg) {
                // Normally we would do some work here, like download a file.
                // For our sample, we just sleep for 5 seconds.
                try {

                    showData.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {*/

        /*private final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                intent = new Intent(DataService.this,MainActivity.class);
                bundle = new Bundle();
                bundle.putString("mainData",data);


            *//*bundle.putString("temp_data",temp_data);
            bundle.putString("humidity_data",hum_data);
            bundle.putString("heatIndex_data",hindex_data);
            bundle.putString("co_data",co_data);
            bundle.putString("co2_data",co2_data);
            bundle.putString("pm_data",pm_data);
            bundle.putString("aqi_data",aqi_data);*//*
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };*/


                        /*}
                    });

                    Thread.sleep(5000);
                    Toast.makeText(SplashScreen.this,"Service Working!",Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    // Restore interrupt status.
                    Thread.currentThread().interrupt();
                }
                // Stop the service using the startId, so that we don't stop
                // the service in the middle of handling another job
                stopSelf(msg.arg1);
            }*/
}
        /*
         * A constructor is required, and must call the super IntentService(String)
         * constructor with a name for the worker thread.
         *
        SplashScreen() {
            super("SplashScreen");
        }*/



        /*@Override
        public void onCreate() {
            // Start up the thread running the service. Note that we create a
            // separate thread because the service normally runs in the process's
            // main thread, which we don't want to block. We also make it
            // background priority so CPU-intensive work doesn't disrupt our UI.

            *//*HandlerThread thread = new HandlerThread("ServiceStartArguments",
                    Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();

            // Get the HandlerThread's Looper and use it for our Handler
            mServiceLooper = thread.getLooper();
            mServiceHandler = new ServiceHandler(mServiceLooper);*//*

            super.onCreate(bundle);
            *//*mContext = getApplicationContext();

            mApp = (Common) getApplicationContext();
            mApp.setService(this);*//*

            //super.onCreate(); // if you override onCreate(), make sure to call super().
            // If a Context object is needed, call getApplicationContext() here.



            //Intent intent = new Intent(SplashScreen.this,MainActivity.class);
        }

        *//*@Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
            return super.onStartCommand(intent,flags,startId);
        }*//*

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

            // For each start request, send a message to start a job and deliver the
            // start ID so we know which request we're stopping when we finish the job
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);

            // If we get killed, after returning from here, restart
            return START_REDELIVER_INTENT;
        }

        @Override
        public IBinder onBind(Intent intent) {
            // We don't provide binding, so return null
            return null;
        }

        @Override
        public void onDestroy() {
            unregisterReceiver(blReceiver);
            //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        }*/

        /**
         * The IntentService calls this method from the default worker thread with
         * the intent that started the service. When this method returns, IntentService
         * stops the service, as appropriate.
         *
         @Override
         protected void onHandleIntent(Intent intent) {
         // This describes what will happen when service is triggered
         // Normally we would do some work here, like download a file.
         // For our sample, we just sleep for 5 seconds.
         try {
         Thread.sleep(5000);
         } catch (InterruptedException e) {
         // Restore interrupt status.
         Thread.currentThread().interrupt();
         }
         }*/


        /*Stop the service as soon as the task is removed from the recent apps*/
        /*@Override
        public void onTaskRemoved(Intent rootIntent) {
            mService.onTaskRemoved(rootIntent);
        }*/
    //}


    /*_______________________Services________________________*/

//}

