package com.example.t_tazhan.roomposition.util;

import android.bluetooth.BluetoothGatt;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class RSSI extends Thread{
    public static Handler rssiHandler;
    BluetoothGatt bluetoothGatt;
    public RSSI(BluetoothGatt bluetoothGatt){
        this.bluetoothGatt=bluetoothGatt;
    }
    @Override
    public void run() {
        Looper.prepare();
        System.out.println(1);
        Log.v("MainActivity", "获取rssi的线程已经启用");
        Log.v("MainActivity",bluetoothGatt.toString());
        rssiHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0x123){
                    Log.v("MainActivity",msg.getData().getString("inf"));
                }
            }
        };
        Looper.loop();
    }

}
