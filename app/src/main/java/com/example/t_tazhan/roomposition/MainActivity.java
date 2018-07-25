package com.example.t_tazhan.roomposition;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.t_tazhan.roomposition.util.Constant.*;
import static com.example.t_tazhan.roomposition.util.FileSave.saveFile;

public class MainActivity extends AppCompatActivity {

    public Handler handler;
    BluetoothAdapter bluetoothAdapter1;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Button buttonStart;
    Button buttonEnd;
    EditText textX;
    EditText textY;
    EditText textTimer;
    TextView textView;
    int countTime = 0;
    List<String> lst_Devices = new ArrayList<>();
    StringBuilder sb1 = new StringBuilder();
    String X = null,Y = null;
    String timerDuration = "8";
    int n = 0;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public  void onReceive(Context context, Intent intent) {
//            lst_Devices.clear();
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
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        final BluetoothManager manager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        if (manager != null) {
            bluetoothAdapter1 = manager.getAdapter();
        }
        if (!bluetoothAdapter.isEnabled()) {
            int REQUEST_ENABLE_BT = 1;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Toast toast = Toast.makeText(MainActivity.this, "已经打开了蓝牙，可以正常使用APP", Toast.LENGTH_LONG);
            toast.show();
        }
        verifyStoragePermissions(this );
        textX = findViewById(R.id.x);
        textX.addTextChangedListener(textWatcher1);
        textY = findViewById(R.id.y);
        textY.addTextChangedListener(textWatcher2);
        textTimer = findViewById(R.id.inputTimer);
        textTimer.addTextChangedListener(textWatcher3);
        buttonStart = findViewById(R.id.button);
        buttonEnd = findViewById(R.id.buttonEnd);
        textView = findViewById(R.id.countTimer);
        textView.setText(String.valueOf(0));
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, intent);


//        handler2 = new Handler();
//        map = new HashMap<String,List<iBeacon>>();
//        sb3 = new StringBuilder("\n");

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast toast = Toast.makeText(MainActivity.this, "已经打开了蓝牙，可以正常使用APP", Toast.LENGTH_LONG);
            toast.show();
        } else {
            Toast toast1 = Toast.makeText(MainActivity.this, "还没打开蓝牙，不能使用APP", Toast.LENGTH_LONG);
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
    int timeFlag;
    boolean endFlag = false;
    public void onClick_Search(View v) {
//        initParam();
//        System.out.println("扫描结果如下： " + sb3);
        n = 0;
        countTime = 0;
        timeFlag= Integer.valueOf(timerDuration)*1000;
        thread = new Thread(runnable1);
        thread.start();
//        startTimer();
    }
    public void onClick_End(View view) {
        endFlag = true;
        sb1.append("此时信标位置为" + "[" + X + " "+ Y + "]").append("\r");
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
//        timer.cancel();
        saveFile(sb1.append(sb2).toString(),X,Y);
        textX.getText().clear();
        textY.getText().clear();
        textTimer.getText().clear();
        textView.setText(String.valueOf(0));
        timerDuration = "8";
    }
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
            timerDuration = textTimer.getText().toString();
            System.out.println(textTimer.getText().toString());
        }
    };

    Timer timer;
    TimerTask timerTask;
    StringBuilder sb2 = new StringBuilder();

    public void startTimer() {
        int timeFlag = Integer.valueOf(timerDuration)*1000;
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 0, timeFlag);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                updateCount();
                n++;
                for(int i=0; i< lst_Devices.size();i++) {
                    sb2 = sb2.append(lst_Devices.get(i));
                }
                sb2.append("\r");
                System.out.println("数据显示" + sb2.toString());
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


    //add Thread and Runnable
//    private StringBuilder stringBuilder = new StringBuilder();
    private Thread thread;
    private Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < 500; i++) {
                if (endFlag) {
                    break;
                }
                updateCount();
                n++;
                bluetoothAdapter.startDiscovery();
                try {
                    Thread.sleep(timeFlag);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bluetoothAdapter.cancelDiscovery();
                if (n < 10) {
                    sb2.append("00").append(n).append(" ");
                } else if (n > 9 && n < 100) {
                    sb2.append("0").append(n).append(" ");
                } else {
                    sb2.append(n).append(" ");
                }
                for (int j = 0; j < lst_Devices.size(); j++) {
                    sb2 = sb2.append(lst_Devices.get(j));
                }
                sb2.append("\r");
                System.out.println("数据显示" + sb2.toString());
                lst_Devices.clear();
            }
        }
    };
}