package com.example.t_tazhan.roomposition;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.t_tazhan.roomposition.util.Constant.*;
import static com.example.t_tazhan.roomposition.util.FileSave.saveFile;

public class MainActivity2 extends AppCompatActivity {

    public Handler handler;
    private static final String TAG = MainActivity2.class.getSimpleName();
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Button buttonStart;
    Button buttonEnd;
    EditText textX;
    EditText textY;
    EditText textTimer;
    TextView textView;
    ProgressBar progressBar;
    ListView b_listView;
    ArrayAdapter<String> adt_Devices;
    int countTime = 0;
    int mark;
    int num[];
    List<String> lst_Devices = new ArrayList<>();
    BluetoothGatt mBluetoothGatt1;
    BluetoothGatt mBluetoothGatt2;
    StringBuilder sb1 = new StringBuilder();
    String X = null,Y = null;
    int timerDuration = 5;
    int n = 0;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public  void onReceive(Context context, Intent intent) {
//            lst_Devices.clear();
            // TODO Auto-generated method stub
            String action = intent.getAction();
            BluetoothDevice bluetooth_Device ;
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                bluetooth_Device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (bluetooth_Device.getBondState() == BluetoothDevice.BOND_NONE) {
                    String str =
//                            bluetooth_Device.getName() + "|"
                                    getBeacon(bluetooth_Device.getAddress()) + " "
                                    + intent.getExtras().getShort(bluetooth_Device.EXTRA_RSSI)
                                    + " ";
//                                    + n + " ";
                    if (lst_Devices.indexOf(str) == -1 &&
                            getBeacon(bluetooth_Device.getAddress()) != "mac") {// 防止地址被重复添加
                        lst_Devices.add(str);
                    }
//                    adt_Devices.notifyDataSetChanged();
                } else if(bluetooth_Device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    String str =
//                            bluetooth_Device.getName() + "|"
                                    getBeacon(bluetooth_Device.getAddress()) + " "
                                    + intent.getExtras().getShort(bluetooth_Device.EXTRA_RSSI)
                                    + " ";
//                                    + n + " ";
                    if (lst_Devices.indexOf(str) == -1 &&
                            getBeacon(bluetooth_Device.getAddress()) != "mac") {// 防止地址被重复添加
                        lst_Devices.add(str);
                    }
//                    adt_Devices.notifyDataSetChanged();
                }
            }
//            bluetoothAdapter.cancelDiscovery();
            progressBar.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        if (!bluetoothAdapter.isEnabled()) {
            int REQUEST_ENABLE_BT = 1;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Toast toast = Toast.makeText(MainActivity2.this, "已经打开了蓝牙，可以正常使用APP", Toast.LENGTH_LONG);
            toast.show();
        }

        verifyStoragePermissions(this );

        textX = findViewById(R.id.x);
        textX.addTextChangedListener(textWatcher1);
        textY = findViewById(R.id.y);
        textY.addTextChangedListener(textWatcher2);
        textTimer = findViewById(R.id.inputTimer);
        textTimer.addTextChangedListener(textWatcher3);
        progressBar = findViewById(R.id.progressBar);
        buttonStart = findViewById(R.id.button);
        buttonEnd = findViewById(R.id.buttonEnd);
        textView = findViewById(R.id.countTimer);
        textView.setText(String.valueOf(0));
        b_listView = this.findViewById(R.id.lvDevices);
        adt_Devices = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, lst_Devices);
        b_listView.setAdapter(adt_Devices);
        b_listView.setOnItemClickListener(new ItemClickEvent());

        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, intent);
        num=new int[3000];
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast toast = Toast.makeText(MainActivity2.this, "已经打开了蓝牙，可以正常使用APP", Toast.LENGTH_LONG);
            toast.show();
        } else {
            Toast toast1 = Toast.makeText(MainActivity2.this, "还没打开蓝牙，不能使用APP", Toast.LENGTH_LONG);
            toast1.show();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
        unregisterReceiver(mReceiver);
    }

    public void onClick_Search(View v) {
        n = 0;
        sb2 = new StringBuilder();
        startTimer();
    }
    public void onClick_End(View view) {
        sb1.append("此时信标位置为" + "[" + X + " "+ Y + "]").append("\r");
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        timer.cancel();
        saveFile(sb1.append(sb2).toString(),X,Y);
        textX.getText().clear();
        textY.getText().clear();
        textView.setText(String.valueOf(0));
    }
    class ItemClickEvent implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            if (bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();
            String str = lst_Devices.get(arg2);
            String[] values = str.split("\\|");
            String address = values[2];
            BluetoothDevice btDev = bluetoothAdapter.getRemoteDevice(address);
            if(arg2==0){
                mBluetoothGatt1 = btDev.connectGatt(MainActivity2.this, false, gattCallback);}
            else { mBluetoothGatt2 = btDev.connectGatt(MainActivity2.this, false, gattCallback);}
//            rssiThread=new RSSI(mBluetoothGatt1);
//            rssiThread.start();
        }
    }
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt mBluetoothGatt, int status, int newState) {
            Log.v(TAG, "回调函数已经调用");
            System.out.print("回调函数已经调用");
            super.onConnectionStateChange(mBluetoothGatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                Toast.makeText(MainActivity2.this, "已经连接", Toast.LENGTH_LONG).show();
                Log.v(TAG, "回调函数已经调用");
            }
        }
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if(rssi!=0) {
                num[mark]=rssi;
                mark++;
            }
        }
    };
    private TextWatcher textWatcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            X = textX.getText().toString();
            System.out.println(textX.getText().toString());

        }
    };
    private TextWatcher textWatcher2 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            Y = textY.getText().toString();
            System.out.println(textY.getText().toString());

        }
    };
    private TextWatcher textWatcher3 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            timerDuration = Integer.valueOf(textTimer.getText().toString());
            System.out.println(textTimer.getText().toString());
        }
    };

    Timer timer;
    TimerTask timerTask;
    StringBuilder sb2 = new StringBuilder();
    int m;

    public void startTimer() {
        int timeFlag = timerDuration*1000;
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 0, timeFlag);

    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                updateCount();
                n++;
                m = n-1;
                sb2.append(m + " ");
                for(int i=0; i< lst_Devices.size();i++) {
                    sb2 = sb2.append(lst_Devices.get(i));
                }
                sb2.append("\r");
                System.out.println(sb2.toString());
                bluetoothAdapter.cancelDiscovery();
                lst_Devices.clear();
                bluetoothAdapter.startDiscovery();
            }
        };
    }
    Handler handlerCountTimer = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = msg.obj.toString();
            textView.setText(message);
        }
    };
    private void updateCount() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.obj = ++countTime;
                handlerCountTimer.sendMessage(msg);
            }
        }).start();
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}
