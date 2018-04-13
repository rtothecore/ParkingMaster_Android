package kr.co.ezinfotech.parkingmaster;

import android.Manifest;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    boolean isPermissionGranted = false;

    Location myLoc = new Location("myLoc");

    private Geocoder mCoder;
    private GPSTracker gps = null;

    public SearchView searchView = null;

    ParkingMasterDBHelper dbHelper = null;

    PisDataManager pdm = new PisDataManager();
    PisDataManager pdmForSearch = new PisDataManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_scrolling);

        mCoder = new Geocoder(this);

        runPermissionListener(this);

        //drawBarChart();
    }

    private void initPdm() {
        pdm = new PisDataManager();
        pdm.setMode(0);
        pdm.setContext(this);
        pdm.setLoc(myLoc);
        pdm.setDbHelper(this);
        pdm.setLL((LinearLayout)findViewById(R.id.ll_parkinglock));
        pdm.setBC((BarChart)findViewById(R.id.st_bar_chart1));
        pdm.setPisData();
    }

    private void initPdmForSearch(String searchVal) {
        pdmForSearch = new PisDataManager();
        pdmForSearch.setMode(1);
        pdmForSearch.setDbHelper(this);
        pdmForSearch.setSearchStr(searchVal);
        pdmForSearch.setContext(this);
        pdmForSearch.setPisData();
    }

    private void initCsvFiles() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(ParkingZoneDBCtrct.SQL_SELECT, null);
        cursor.moveToFirst();

        // PARKING_ZONE 테이블의 데이터셋이 0인경우 *.csv파일을 읽어서 테이블에 INSERT
        if(0 == cursor.getCount()) {
            loadNInsertCSV("PI_JEJU_20170731.csv");
            loadNInsertCSV("PI_SEO_20160831.csv");
        }
    }

    // http://opencsv.sourceforge.net/
    private void loadNInsertCSV(String fileName) {
        AssetManager am = getResources().getAssets();
        InputStream is = null ;
        try {
            is = am.open(fileName);
            CSVReader csvReader = new CSVReader(new InputStreamReader(is));
            String [] nextLine;
            csvReader.readNext();   // 첫번째 행 건너뛰기
            while ((nextLine = csvReader.readNext()) != null) {
                // 1. csv에 위,경도값이 없을 경우 주소를 지오코딩하여 DB에 Insert
                if( "".equals(nextLine[28]) || "".equals(nextLine[29]) ) {
                    Location tempLoc = runGeoCoding(nextLine[4]);
                    nextLine[28] = Double.toString(tempLoc.getLatitude());
                    nextLine[29] = Double.toString(tempLoc.getLongitude());
                    insertPZTable(nextLine);
                } else {    // 2. csv에 위,경도값이 있는 경우
                    insertPZTable(nextLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("loadNInsertCSV", "IOException!");
        }
    }

    private void insertPZTable(String[] csvData) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sqlInsert = ParkingZoneDBCtrct.SQL_INSERT +
                " (" +
                "'" + csvData[1] + "', " +
                "'" + csvData[4] + "', " +
                "'" + csvData[27] + "', " +
                "'" + csvData[28] + "', " +
                "'" + csvData[29] + "', " +
                "'" + csvData[6] + "', " +
                "'" + csvData[9] + "', " +
                "'" + csvData[10] + "', " +
                "'" + csvData[11] + "', " +
                "'" + csvData[12] + "', " +
                "'" + csvData[13] + "', " +
                "'" + csvData[14] + "', " +
                "'" + csvData[15] + "', " +
                "'" + csvData[16] + "', " +
                "'" + csvData[17] + "', " +
                "'" + csvData[18] + "', " +
                "'" + csvData[19] + "', " +
                "'" + csvData[20] + "', " +
                "'" + csvData[25] +
                "')";
        db.execSQL(sqlInsert);
    }

    private void dropTables() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(ParkingZoneDBCtrct.SQL_DROP_TBL);
        db.execSQL(FavoritesDBCtrct.SQL_DROP_TBL);
    }

    private void deleteTables() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //db.execSQL(ParkingZoneDBCtrct.SQL_DELETE);
        db.execSQL(FavoritesDBCtrct.SQL_DELETE);
    }

    private void initTables() {
        dbHelper = new ParkingMasterDBHelper(this);
    }

    /*
    private void drawBarChart() {
        BarChartManager bcm = new BarChartManager((BarChart)findViewById(R.id.st_bar_chart1), this);
        bcm.draw();
    }
    */

    // Using TedPermission library - https://github.com/ParkSangGwon/TedPermission
    private void runPermissionListener(Context ctx) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(ScrollingActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                InitializeLocationService();

                initTables();   // Initialize DB - http://recipes4dev.tistory.com/124?category=698941

                //deleteTables(); // TEST!!!!

                initCsvFiles();
                initPdm();

                runAirDataManager();

                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(ScrollingActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(ctx)
                .setPermissionListener(permissionlistener)
                .setRationaleTitle("Rational Title")
                .setRationaleTitle(R.string.rationale_title)
                .setRationaleMessage(R.string.rationale_message)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("bla bla")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    }

    private void InitializeLocationService() {

        gps = new GPSTracker(this);
        if(gps.canGetLocation()) {

            myLoc.setLatitude(gps.getLatitude());
            myLoc.setLongitude(gps.getLongitude());

            Log.i("InitializeLocation", myLoc.getLatitude() + "/" + myLoc.getLongitude());

            runReverseGeoCoding(myLoc.getLatitude(), myLoc.getLongitude());

            runWeatherDataManager();
        } else {
            gps.showSettingsAlert();
        }
    }

    private void runWeatherDataManager() {
        WeatherDataManager wdm = new WeatherDataManager(myLoc.getLatitude(), myLoc.getLongitude(), (TextView)findViewById(R.id.wt_ac));
        wdm.setSkyTextView((TextView)findViewById(R.id.wt_title));
        wdm.setT1hTextView((TextView)findViewById(R.id.wt_tp));
        wdm.setSkyImageView((ImageView)findViewById(R.id.wt_pic));
        wdm.setWeatherData();
    }

    private void runAirDataManager() {
        AirDataManager adm = new AirDataManager(myLoc);
        adm.setLocTextView((TextView)findViewById(R.id.air_loc));
        adm.setPm10TextView((TextView)findViewById(R.id.air_pm10));
        adm.setPm25TextView((TextView)findViewById(R.id.air_pm25));
        adm.setO3TextView((TextView)findViewById(R.id.air_o3));
        adm.setAirData();
    }

    public void onResume() {
        super.onResume();

        if(isPermissionGranted) {
            InitializeLocationService();

            initTables();
            initCsvFiles();
            initPdm();
            runAirDataManager();
        }
    }

    public void onPause() {
        super.onPause();

        if(isPermissionGranted && (null != gps)) {
            gps.stopUsingGPS();
        }

        Log.i("onPause", "Get my location END");
    }

    // 실제주소 => 위,경도 변환 - http://bitsoul.tistory.com/135
    private Location runGeoCoding(String addr) {
        Location result = null;
        List<Address> list = null;

        try {
            list = mCoder.getFromLocationName(
                    addr, // 지역 이름
                    10); // 읽을 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("runGeoCoding","입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if (list != null) {
            if (list.size() == 0) {
                Log.e("runGeoCoding",addr + " 해당되는 주소 정보는 없습니다");
                result = new Location("");
                result.setLatitude(0);
                result.setLongitude(0);
            } else {
                result = new Location("");
                result.setLatitude(list.get(0).getLatitude());
                result.setLongitude(list.get(0).getLongitude());
            }
        }

        return result;
    }

    // 위,경도 => 실제주소 변환
    private void runReverseGeoCoding(double latVal, double lonVal) {
        try {
            List<Address> list = mCoder.getFromLocation(latVal, lonVal, 5);
            Log.i("Geocode", list.get(0).toString());
            TextView tb_addr = (TextView) findViewById(R.id.wt_loc);

            if (list != null) {
                if (list.size()==0) {
                    tb_addr.setText("---");
                } else {
                    String cut[] = list.get(0).toString().split(" ");
                    tb_addr.setText(cut[2] + " " + cut[3]); // 내가 원하는 구의 값을 뽑아내 출력
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Geocode", "getFromLocation IO exception");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);

        // For search START - http://iw90.tistory.com/222, http://recipes4dev.tistory.com/141
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.edittext_search_video));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {    // 검색어 완료시
                Log.i("onQueryTextSubmit", "검색어 완료시 : " + s);
                initPdmForSearch(s);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {    // 검색어 입력시
                Log.i("onQueryTextChange", "검색어 입력시");
                return false;
            }
        });
        // For search END

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
}
