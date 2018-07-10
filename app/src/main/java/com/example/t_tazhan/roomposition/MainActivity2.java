package com.example.t_tazhan.roomposition;

import android.os.Handler;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.t_tazhan.roomposition.Constant.*;
import static com.example.t_tazhan.roomposition.FileSave.saveFile;
import static java.lang.Thread.sleep;


public class MainActivity2 extends AppCompatActivity {

    public Thread thread;
    public Handler handler;

    //声明变量
    private static final String TAG = MainActivity2.class.getSimpleName();
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Button bt1;
    Button btrssi1;
    Button btrssi2;
    Button Btrssi3;
    EditText textX;
    EditText textY;
    ProgressBar progressBar;
    ListView b_listView;
    ArrayAdapter<String> adt_Devices;
    int mark;
    int num[];
    List<String> lst_Devices = new ArrayList<>();
    BluetoothGatt mBluetoothGatt1;
    BluetoothGatt mBluetoothGatt2;
    RSSI rssiThread;

    StringBuilder sb = new StringBuilder();
    String X = null,Y = null;
    int l = 0;
    //广播接收器
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public  void onReceive(Context context, Intent intent) {
            System.out.println("第" + (++l) + "次进入onReceive的时间是" + System.currentTimeMillis());
            lst_Devices.clear();
            // TODO Auto-generated method stub
            String action = intent.getAction();
            // 显示所有收到的消息及其细节
            BluetoothDevice bluetooth_Device ;
            // 搜索设备时，取得设备的MAC地址
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                bluetooth_Device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (bluetooth_Device.getBondState() == BluetoothDevice.BOND_NONE) {
                    String str ="信标"
//                            + bluetooth_Device.getName() + "|"
                            + getBeacon(bluetooth_Device.getAddress()) + " "
                            + intent.getExtras().getShort(bluetooth_Device.EXTRA_RSSI);
                    if (lst_Devices.indexOf(str) == -1) {// 防止地址被重复添加
                        lst_Devices.add(str); // 获取设备名称和mac地址
                        System.out.println("第" + l + "次进入add String");
//                        saveFile(str);
                    }
                    adt_Devices.notifyDataSetChanged();
                } else if(bluetooth_Device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    String str ="信标"
//                            + bluetooth_Device.getName() + "|"
                            + getBeacon(bluetooth_Device.getAddress()) + " "
                            + intent.getExtras().getShort(bluetooth_Device.EXTRA_RSSI);
                    if (lst_Devices.indexOf(str) == -1) {// 防止地址被重复添加
                        lst_Devices.add(str); // 获取设备名称和mac地址
                        System.out.println("第" + l + "次进入add String");
//                        saveFile(str);
                    }
                    adt_Devices.notifyDataSetChanged();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                sb.append("分界线");
            }

            //此行代码表示取消继续搜索蓝牙信号
//            bluetoothAdapter.cancelDiscovery();
            progressBar.setVisibility(View.INVISIBLE);

//            sb.append("此时信标位置为" + "[" + X + " "+ Y + "]").append("\r");
            for (int j=0;j<lst_Devices.size();j++) {
                sb.append(lst_Devices.get(j)).append("\r");
                System.out.println(j+ " "+lst_Devices.get(j));
            }
            saveFile(sb.toString(),X,Y);
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

        textX = (EditText)findViewById(R.id.x);
        textX.addTextChangedListener(textWatcher1);
        textY = (EditText)findViewById(R.id.y);
        textY.addTextChangedListener(textWatcher2);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        bt1 = (Button) findViewById(R.id.button);
        Btrssi3= (Button) findViewById(R.id.RssiThread);
        btrssi1=(Button)findViewById(R.id.RSSIbutton1);
        btrssi2=(Button)findViewById(R.id.RSSIbutton2);
        b_listView = (ListView) this.findViewById(R.id.lvDevices);
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

        //添加两个按钮的单击事件
        btrssi1.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.v(TAG, "点击了读取");
                for(mark=0;mark<3000;) {
                    mBluetoothGatt1.readRemoteRssi();
                   /* try {
                        Thread.currentThread().sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    //Log.v(TAG,"线程已经暂停一秒");
                }
                for(mark=0;mark<3000;mark++){
                    Log.v(TAG,""+num[mark]+" "+mark);
                }
                Toast.makeText(MainActivity2.this,"读取完毕",Toast.LENGTH_SHORT).show();
            }
        });
        btrssi2.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.v(TAG, "点击了读取");
                for(mark=0;mark<3000;) {
                    mBluetoothGatt2.readRemoteRssi();
                   /* try {
                        Thread.currentThread().sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    //Log.v(TAG,"线程已经暂停一秒");
                }
                for(mark=0;mark<3000;mark++){
                    Log.v(TAG,""+num[mark]+" "+mark);
                }
                Toast.makeText(MainActivity2.this,"读取完毕",Toast.LENGTH_SHORT).show();
            }
        });
        //该单击时间创建了新的线程并且启动了，发送了一个消息给新线程
        Btrssi3.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.v(TAG, "点击了发送消息");
                Message msg=new Message();
                msg.what=0x123;
                Bundle bundle=new Bundle();
                bundle.putString("inf","主线程发送过来的消息");
                msg.setData(bundle);
                rssiThread.rssiHandler.sendMessage(msg);
            }
        });
    }
    //检查蓝牙是否打开
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
        //解除注册
        unregisterReceiver(mReceiver);
    }

    public void onClick_Search(View v) {
        sb.append("此时信标位置为" + "[" + X + " "+ Y + "]").append("\r");
        l = 0;
//        new Thread() {
//            public void run() {
//                handler.post(runnable);
//            }
//        }.start();
//        for (int i=0;i<500;i++) {
//            System.out.println("搜索" + i);
//            try {
//                if (bluetoothAdapter.isDiscovering()) {
//                    bluetoothAdapter.cancelDiscovery();
//                    progressBar.setVisibility(View.INVISIBLE);
//                    bluetoothAdapter.startDiscovery();
//                    progressBar.setVisibility(View.VISIBLE);
//                    Thread.sleep(500);
//                    bluetoothAdapter.cancelDiscovery();
//                    return;
//                } else {
//                    bluetoothAdapter.startDiscovery();
//                    progressBar.setVisibility(View.VISIBLE);
//                    Thread.sleep(500);
//                    bluetoothAdapter.cancelDiscovery();
//                    progressBar.setVisibility(View.INVISIBLE);
//                    return;
//                }
//                if (bluetoothAdapter.isDiscovering()) {
//                    bluetoothAdapter.cancelDiscovery();
//                    progressBar.setVisibility(View.INVISIBLE);
//                } else {
//                    progressBar.setVisibility(View.VISIBLE);
//                    bluetoothAdapter.startDiscovery();
//                }
//                Thread.sleep(500);
//                CaptureRssi( );
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        startTimer();
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
            rssiThread=new RSSI(mBluetoothGatt1);
            rssiThread.start();
        }
    }

    //实现gattCallback
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt mBluetoothGatt, int status, int newState) {
            //设备连接状态改变会回调这个函数
            Log.v(TAG, "回调函数已经调用");
            System.out.print("回调函数已经调用");
            super.onConnectionStateChange(mBluetoothGatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                //连接成功, 可以把这个gatt 保存起来, 需要读rssi的时候就
                Toast.makeText(MainActivity2.this, "已经连接", Toast.LENGTH_LONG).show();
                Log.v(TAG, "回调函数已经调用");
            }
        }
        @Override
        //底层获取RSSI后会回调这个函数
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            //判断是否读取成功
            if(rssi!=0)
            {
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

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            final int scanRepeatValue = 500;
            final int scanTimeValue = 5;

            for (int i = 0; i < scanRepeatValue; i++) {
                System.out.println("搜索" + i);
                progressBar.setVisibility(View.VISIBLE);
                bluetoothAdapter.startDiscovery();
                for (int j = 0; j < scanTimeValue; j++) {
                    try {
                        sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                bluetoothAdapter.cancelDiscovery();
                progressBar.setVisibility(View.INVISIBLE);
//                CaptureRssi();
                break;
            }
        }
    };


    public  void CaptureRssi( ) {
     Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() > 0) {
        // There are paired devices. Get the name and address of each paired device.
            for(BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();// MAC address
//                short RSSI = intent.getExtras().getShort(device.EXTRA_RSSI);
                System.out.println(deviceName + " " + deviceHardwareAddress);

            }
        }
    }


    Timer timer;
    TimerTask timerTask;

    public void startTimer() {

        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms

        timer.schedule(timerTask, 0, 5000); //

    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {
                l++;
                StringBuilder sb = new StringBuilder();
                for(int i=0; i< lst_Devices.size();i++) {
                    sb = sb.append(lst_Devices.get(i)).append(" ");
                }

                saveFile(sb.toString(),X,Y);

                lst_Devices.clear();
                //use a handler to run a toast that shows the current timestamp
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
//                    progressBar.setVisibility(View.INVISIBLE);
                } else {
//                    progressBar.setVisibility(View.VISIBLE);
                    bluetoothAdapter.startDiscovery();
                }
//                if (bluetoothAdapter.isDiscovering()) {
//                    bluetoothAdapter.cancelDiscovery();
//                }
//                progressBar.setVisibility(View.VISIBLE);
//                bluetoothAdapter.startDiscovery();
            }
        };
    }

}
