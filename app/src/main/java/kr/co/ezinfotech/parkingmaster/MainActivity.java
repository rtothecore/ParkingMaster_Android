package kr.co.ezinfotech.parkingmaster;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler() {
            @Override public void handleMessage(Message msg) {
                if(777 == msg.arg1) {
                    Log.i("onCreate", "Thread job ended!");
                    Intent intent = new Intent(getApplicationContext(), ScrollingActivity.class);
                    getApplicationContext().startActivity(intent);
                    finish();   // Destroy MainActivity
                }
            }
        };
        runPermissionListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("onDestroy", "called onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
        if(UtilManager.isPermissionGranted) {
            gps.stopUsingGPS();
        }
        */
        Log.i("onPause", "Get my location END");
    }

    // Using TedPermission library - https://github.com/ParkSangGwon/TedPermission
    private void runPermissionListener(Context ctx) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                InitializeLocationService();
                //dropTables();   // TEST!!!!
                //deleteTables(); // TEST!!!!
                initPzDB();
                UtilManager.isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(ctx)
                .setPermissionListener(permissionlistener)
                .setRationaleTitle("Rational Title")
                .setRationaleTitle(R.string.rationale_title)
                .setRationaleMessage(R.string.rationale_message)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("bla bla")
                //.setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void InitializeLocationService() {
        GPSTracker gps = new GPSTracker(this);

        if(gps.canGetLocation()) {
            UtilManager.setContext(this);
            UtilManager.myLoc.setLatitude(gps.getLatitude());
            UtilManager.myLoc.setLongitude(gps.getLongitude());
            Log.i("InitializeLocation", UtilManager.myLoc.getLatitude() + "," + UtilManager.myLoc.getLongitude());
        } else {
            gps.showSettingsAlert();
        }
    }

    private void dropTables() {
        SQLiteDatabase db = UtilManager.dbHelper.getWritableDatabase();
        db.execSQL(ParkingZoneDBCtrct.SQL_DROP_TBL);
        db.execSQL(FavoritesDBCtrct.SQL_DROP_TBL);
    }

    private void deleteTables() {
        SQLiteDatabase db = UtilManager.dbHelper.getWritableDatabase();
        db.execSQL(ParkingZoneDBCtrct.SQL_DELETE);
        db.execSQL(FavoritesDBCtrct.SQL_DELETE);
    }

    private void initPzDB() {
        SQLiteDatabase db = UtilManager.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(ParkingZoneDBCtrct.SQL_SELECT, null);
        cursor.moveToFirst();

        // PARKING_ZONE 테이블의 데이터셋이 0인경우 pz서버에 접속하여 json파일을 참조하여 테이블에 INSERT
        // PARKKIG_ZONE 테이블에 데이터셋이 있는 경우 pz서버에 접속하여 json파일의 DATADATE로 테이블 SELECT하여 데이터날짜를 비교
        if(0 == cursor.getCount()) {
            loadNInsertJSON();
        } else {
            checkDataDateNInsert();
        }
    }

    private void loadNInsertJSON() {
        PZDataManager pzdm = new PZDataManager();
        pzdm.setHandler(mHandler);
        pzdm.setPZData();
    }

    private void checkDataDateNInsert() {
        PZDataManager pzdm = new PZDataManager();
        pzdm.setMode(1);
        pzdm.setHandler(mHandler);
        pzdm.setPZData();
    }

}
