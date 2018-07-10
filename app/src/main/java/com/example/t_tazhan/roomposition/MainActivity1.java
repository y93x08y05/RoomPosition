package com.example.t_tazhan.roomposition;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity1 extends Activity{
//    private static final SKYRegion ALL_SEEKCY_BEACONS_REGION = new SKYRegion("rid_all", null, null, null, null);
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        SKYBeaconManager.getInstance().init(this);
//        SKYBeaconManager.getInstance().setCacheTimeMillisecond(3000);//可选，不设置默认为5秒缓存
//        SKYBeaconManager.getInstance().setScanTimeIntervalMillisecond(2000);//可选，不设置默认为2秒返回一次数据
////        SKYBeaconManager.getInstance().setBroadcastKey("AB11221498756731BCD7D8E239E765AD52B7139DE87654DAB27394BCD7D792A");
//        SKYBeaconManager.getInstance().setRangingBeaconsListener(new RangingBeaconsListener() {
//            @Override
//            public void onRangedBeaconsMultiIDs(SKYRegion arg0, List<SKYBeaconMultiIDs> arg1) {
//            }
//            @Override
//            public void onRangedBeacons(SKYRegion arg0, List<SKYBeacon> arg1) {
//            }
//        });
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        super.onStart();
//        startRanging();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        super.onStop();
//        stopRanging();
//    }
//
//    private void startRanging() {
//        SKYBeaconManager.getInstance().startScanService(new ScanServiceStateCallback() {
//            @Override
//            public void onServiceDisconnected() {
//            }
//            @Override
//            public void onServiceConnected() {
//                SKYBeaconManager.getInstance().startRangingBeacons(ALL_SEEKCY_BEACONS_REGION);
//            }
//        });
//    }
//
//    private void stopRanging() {
//        SKYBeaconManager.getInstance().stopScanService();
//        SKYBeaconManager.getInstance().stopRangingBeasons(ALL_SEEKCY_BEACONS_REGION);
//    }
}
