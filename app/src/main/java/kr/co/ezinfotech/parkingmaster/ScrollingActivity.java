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
    public SearchView searchView = null;

    PisDataManager pdm = new PisDataManager();
    PisDataManager pdmForSearch = new PisDataManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_scrolling);

        /*
        initLoc();
        initPdm();
        runWeatherDataManager();
        runAirDataManager();
        */
    }

    private void initLoc() {
        String tempAddr = UtilManager.runReverseGeoCoding(UtilManager.myLoc.getLatitude(), UtilManager.myLoc.getLongitude());
        String[] cut = tempAddr.split(" ");
        tempAddr = cut[2] + " " + cut[3];
        ((TextView)findViewById(R.id.wt_loc)).setText(tempAddr);
    }

    private void initPdm() {
        pdm = new PisDataManager();
        pdm.setMode(0);
        pdm.setContext(this);
        pdm.setLoc(UtilManager.myLoc);
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

    private void runWeatherDataManager() {
        WeatherDataManager wdm = new WeatherDataManager(UtilManager.myLoc.getLatitude(), UtilManager.myLoc.getLongitude(), (TextView)findViewById(R.id.wt_ac));
        wdm.setSkyTextView((TextView)findViewById(R.id.wt_title));
        wdm.setT1hTextView((TextView)findViewById(R.id.wt_tp));
        wdm.setSkyImageView((ImageView)findViewById(R.id.wt_pic));
        wdm.setWeatherData();
    }

    private void runAirDataManager() {
        AirDataManager adm = new AirDataManager(UtilManager.myLoc);
        adm.setLocTextView((TextView)findViewById(R.id.air_loc));
        adm.setPm10TextView((TextView)findViewById(R.id.air_pm10));
        adm.setPm25TextView((TextView)findViewById(R.id.air_pm25));
        adm.setO3TextView((TextView)findViewById(R.id.air_o3));
        adm.setAirData();
    }

    public void onResume() {
        super.onResume();

        initLoc();
        initPdm();
        runWeatherDataManager();
        runAirDataManager();
        /*
        if(UtilManager.isPermissionGranted) {
            initLoc();
            initPdm();
            runWeatherDataManager();
            runAirDataManager();
        }
        */
    }

    public void onPause() {
        super.onPause();
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
