package kr.co.ezinfotech.parkingmaster;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
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

        TextView pkName = view.findViewById(R.id.pkName);

        TextView wtTitle2 = view.findViewById(R.id.wt_title2);
        TextView wtLoc2 = view.findViewById(R.id.wt_loc2);
        ImageView img = view.findViewById(R.id.wt_pic2);
        TextView wtTp2 = view.findViewById(R.id.wt_tp2);
        TextView wtAc2 = view.findViewById(R.id.wt_ac2);

        TextView airTitle2 = view.findViewById(R.id.air_title2);
        TextView airLoc2 = view.findViewById(R.id.air_loc2);
        TextView airPm102 = view.findViewById(R.id.air_pm102);
        TextView airPm252 = view.findViewById(R.id.air_pm252);
        TextView airO32 = view.findViewById(R.id.air_o32);

        TextView realTitle = view.findViewById(R.id.real_title);
        TextView realData = view.findViewById(R.id.real_data);

        TextView festList1 = view.findViewById(R.id.festList1);
        TextView festList2 = view.findViewById(R.id.festList2);

        pkName.setText(marker.getTitle());
        wtTitle2.setText(marker.getSnippet());

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

        wtLoc2.setText(infoWindowData.getWtloc2());
        int imageId = context.getResources().getIdentifier(infoWindowData.getWtpic2().toLowerCase(), "drawable", context.getPackageName());
        img.setImageResource(imageId);
        wtTp2.setText(infoWindowData.getWtTp2());
        wtAc2.setText(infoWindowData.getWtAc2());

        airTitle2.setText(infoWindowData.getAirTitle2());
        airLoc2.setText(infoWindowData.getAirLoc2());
        airPm102.setText(infoWindowData.getAirPm102());
        airPm252.setText(infoWindowData.getAirPm252());
        airO32.setText(infoWindowData.getAirO32());

        realTitle.setText(infoWindowData.getRealTitle());
        realData.setText(infoWindowData.getRealData());

        festList1.setText(infoWindowData.getFestList1());
        festList2.setText(infoWindowData.getFestList2());

        return view;
    }
}
