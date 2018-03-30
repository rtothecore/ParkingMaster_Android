package kr.co.ezinfotech.parkingmaster;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by hkim on 2018-03-27.
 * http://www.zoftino.com/google-maps-android-custom-info-window-example
 */

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {
    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.infowindow, null);

        TextView pkLocName = view.findViewById(R.id.pkLocName);
        TextView pkTotal = view.findViewById(R.id.pkTotal);
        TextView pkUse = view.findViewById(R.id.pkUse);
        TextView pkAvail = view.findViewById(R.id.pkAvail);

        TextView wtStatus = view.findViewById(R.id.wtStatus);
        TextView wtTemp = view.findViewById(R.id.wtTemp);
        TextView wtHum = view.findViewById(R.id.wtHum);

        pkLocName.setText(marker.getTitle());
        pkTotal.setText(marker.getSnippet());

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

        pkUse.setText(infoWindowData.getPkUse());
        pkAvail.setText(infoWindowData.getPkAvail());
        wtStatus.setText(infoWindowData.getWtStatus());
        wtTemp.setText(infoWindowData.getWtTemp());
        wtHum.setText(infoWindowData.getWtHum());

        return view;
    }
}
