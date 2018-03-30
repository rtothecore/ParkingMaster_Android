package kr.co.ezinfotech.parkingmaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    private GoogleMap mMap;

    private static final LatLng JJU = new LatLng(33.451706, 126.557978);
    private static final LatLng JJAP = new LatLng(33.510619, 126.491321);
    private static final LatLng JJVM = new LatLng(33.500468, 126.529820);
    private static final LatLng JJSI = new LatLng(33.499821, 126.531211);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /* ORIGINAL
        Marker jjuM = mMap.addMarker(new MarkerOptions().position(JJU).title("제주대학교").snippet("주차면수:110"));
        jjuM.showInfoWindow();
        float zoomLevel = (float) 16.0;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(JJU, zoomLevel));
        */
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(JJU)
                .title("제주대학교")
                .snippet("주차면수: 110")
                .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE));

        InfoWindowData info = new InfoWindowData();
        info.setPkUse("주차점유: 45");
        info.setPkAvail("주차가능: 65");
        info.setWtStatus("날씨정보: 맑음");
        info.setWtTemp("기온: 22.4º");
        info.setWtHum("습도: 33%");

        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);   // http://www.zoftino.com/google-maps-android-custom-info-window-example
        mMap.setInfoWindowAdapter(customInfoWindow);

        Marker m = mMap.addMarker(markerOptions);
        m.setTag(info);
        m.showInfoWindow();

        float zoomLevel = (float) 16.0;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(JJU, zoomLevel));

        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //Toast.makeText(this, "Info window clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        startActivity(intent);
    }
}
