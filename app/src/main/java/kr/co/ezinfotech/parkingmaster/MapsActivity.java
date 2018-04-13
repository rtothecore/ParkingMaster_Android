package kr.co.ezinfotech.parkingmaster;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity
        extends FragmentActivity
        implements GoogleMap.OnMarkerClickListener,
                     InfoWindowManager.WindowShowListener {

    private GoogleMap mMap;

    private InfoWindowManager infoWindowManager;
    private InfoWindow[] formWindows;

    ArrayList<PZData> searchedPZData = new ArrayList<>();
    ArrayList<PisData> searchedPisData = new ArrayList<>();

    private Geocoder mCoder;
    private ParkingMasterDBHelper dbHelper = new ParkingMasterDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /* ORIGINAL
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        */
        mCoder = new Geocoder(this);

        // Get parcel data
        searchedPZData = getIntent().getParcelableArrayListExtra("searchedPZData");
        searchedPisData = getIntent().getParcelableArrayListExtra("searchedPisData");
        Log.i("MapsActivity_onCreate", searchedPZData.size() + "/" + searchedPisData.size());

        final MapInfoWindowFragment mapInfoWindowFragment = (MapInfoWindowFragment) getSupportFragmentManager().findFragmentById(R.id.infoWindowMap);

        infoWindowManager = mapInfoWindowFragment.infoWindowManager();
        infoWindowManager.setHideOnFling(true);

        mapInfoWindowFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                final Marker[] marker = new Marker[searchedPZData.size()];
                for(int i = 0; i < marker.length; i++) {
                    marker[i] = mMap.addMarker(new MarkerOptions().position(new LatLng(searchedPZData.get(i).loc.getLatitude(), searchedPZData.get(i).loc.getLongitude())).snippet(Integer.toString(i)));

                    /*
                    if(1 == marker.length) {
                        marker[i].showInfoWindow();
                    }
                    */
                }

                final int offsetX = (int) getResources().getDimension(R.dimen.marker_offset_x);
                final int offsetY = (int) getResources().getDimension(R.dimen.marker_offset_y);

                final InfoWindow.MarkerSpecification markerSpec = new InfoWindow.MarkerSpecification(offsetX, offsetY);

                formWindows = new InfoWindow[searchedPZData.size()];
                //////////////////////////////SET DATA////////////////////////////////////////////////////
                for(int i = 0; i < searchedPZData.size(); i++) {
                    FormFragment ff = new FormFragment();
                    ff.setDbHelper(MapsActivity.this);
                    InfoWindowData info = new InfoWindowData();

                    info.setNo(searchedPZData.get(i).no);
                    info.setPkName(searchedPZData.get(i).name);

                    ////////////////////////// Weather data //////////////////////////
                    WeatherDataManager wdm = new WeatherDataManager(searchedPZData.get(i).loc.getLatitude(), searchedPZData.get(i).loc.getLongitude(), info);
                    wdm.setWeatherData();
                    info.setWtloc2(runReverseGeoCoding(searchedPZData.get(i).loc.getLatitude(), searchedPZData.get(i).loc.getLongitude()));
                    info.setWtAc2("기상청발표-" + wdm.baseTime.substring(0, 2) + "시 기준");
                    ////////////////////////// Weather data //////////////////////////

                    ////////////////////////// Air data //////////////////////////
                    AirDataManager adm = new AirDataManager(searchedPZData.get(i).loc, info);
                    adm.setAirData();
                    ////////////////////////// Air data //////////////////////////

                    ////////////////////////// Real data /////////////////////////////
                    for(int j = 0; j < searchedPisData.size(); j++) {
                        if(searchedPisData.get(j).addr.equals(searchedPZData.get(i).name)) {
                            info.setRealTitle("실시간 주차잔여대수");
                            info.setRealData("일반:" + searchedPisData.get(j).gnrlNum + " 장애:" + searchedPisData.get(j).hndcNum + " 여성:" + searchedPisData.get(j).wmonNum + " 경차:" + searchedPisData.get(j).lgvhNum);
                        } else {
                            info.setRealTitle("실시간 주차잔여대수");
                            info.setRealData("실시간 정보 준비 중입니다.");
                        }
                    }
                    ////////////////////////// Real data /////////////////////////////

                    ////////////////////////// Fest data /////////////////////////////
                    FestDataManager fdm = new FestDataManager(info);
                    fdm.setNearPoint(searchedPZData.get(i).loc);
                    fdm.setFestData();
                    ////////////////////////// Fest data /////////////////////////////

                    ////////////////////////// Set buttons //////////////////
                    info.setBtnMeasure("주차점유율 예측");

                    if(0 == selectWithNo(searchedPZData.get(i).no)) {
                        info.setBtnFavorites("즐겨찾기");
                    } else {
                        info.setBtnFavorites("즐겨찾기 제거");
                    }
                    ////////////////////////// Set favorites button //////////////////

                    ff.setinfoData(info);
                    formWindows[i] = new InfoWindow(marker[i], markerSpec, ff);
                }
                //////////////////////////////SET DATA////////////////////////////////////////////////////
                mMap.setOnMarkerClickListener(MapsActivity.this);

                //float zoomLevel = (float) 16.0;
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(JJU, zoomLevel));

                /////////////////////////FOR SHOW ALL MARKERS/////////////////////////////////////
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        if(1 == marker.length) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker[0].getPosition(),16));
                            infoWindowManager.show(formWindows[0]);
                        } else {
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for(int i = 0; i < marker.length; i++) {
                                builder.include(marker[i].getPosition());
                            }
                            LatLngBounds bounds = builder.build();
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                            mMap.moveCamera(cu);
                        }
                    }
                });
                /////////////////////////FOR SHOW ALL MARKERS/////////////////////////////////////
            }
        });
        infoWindowManager.setWindowShowListener(MapsActivity.this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        InfoWindow infoWindow = formWindows[0];

        infoWindow = formWindows[Integer.parseInt(marker.getSnippet())];

        if (infoWindow != null) {
            infoWindowManager.toggle(infoWindow, true);
        }
        return true;
    }

    @Override
    public void onWindowShowStarted(@NonNull InfoWindow infoWindow) {
//        Log.d("debug", "onWindowShowStarted: " + infoWindow);
    }

    @Override
    public void onWindowShown(@NonNull InfoWindow infoWindow) {
//        Log.d("debug", "onWindowShown: " + infoWindow);
    }

    @Override
    public void onWindowHideStarted(@NonNull InfoWindow infoWindow) {
//        Log.d("debug", "onWindowHideStarted: " + infoWindow);
    }

    @Override
    public void onWindowHidden(@NonNull InfoWindow infoWindow) {
//        Log.d("debug", "onWindowHidden: " + infoWindow);
    }

    // 위,경도 => 실제주소 변환
    private String runReverseGeoCoding(double latVal, double lonVal) {

        String resultStr = null;

        try {
            List<Address> list = mCoder.getFromLocation(latVal, lonVal, 5);
            Log.i("Geocode", list.get(0).toString());
            //TextView tb_addr = (TextView) findViewById(R.id.wt_loc);

            if (list != null) {
                if (list.size()==0) {
                    resultStr = "---";
                } else {
                    String cut[] = list.get(0).toString().split(" ");
                    resultStr = cut[2] + " " + cut[3];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Geocode", "getFromLocation IO exception");
        }
        return resultStr;
    }

    private int selectWithNo(int noVal) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sqlSelect = FavoritesDBCtrct.SQL_SELECT_WITH_NO + noVal;
        Cursor cursor = db.rawQuery(sqlSelect, null);
        cursor.moveToFirst();
        return cursor.getCount();
    }
}
