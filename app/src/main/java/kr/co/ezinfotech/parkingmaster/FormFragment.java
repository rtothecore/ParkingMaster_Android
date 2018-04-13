package kr.co.ezinfotech.parkingmaster;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FormFragment extends Fragment {

    InfoWindowData infoWData = new InfoWindowData();
    ParkingMasterDBHelper dbHelper = null;

    public void setDbHelper(Context ctxVal) {
        dbHelper = new ParkingMasterDBHelper(ctxVal);
    }

    public void setinfoData(InfoWindowData iwdVal) {
        infoWData = iwdVal;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.infowindow, container, false);

        //////////////////////////////////////////////////////////////////////////////////////////////////
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

        Button btnMeasure = (Button)view.findViewById(R.id.btn_measure);
        Button btnFavorites = (Button)view.findViewById(R.id.btn_favorites);

        pkName.setText(infoWData.getPkName());
        wtTitle2.setText(infoWData.getWtTitle2());
        wtLoc2.setText(infoWData.getWtloc2());
        int imageId = getActivity().getResources().getIdentifier(infoWData.getWtpic2().toLowerCase(), "drawable", getActivity().getPackageName());
        img.setImageResource(imageId);
        wtTp2.setText(infoWData.getWtTp2());
        wtAc2.setText(infoWData.getWtAc2());

        airTitle2.setText(infoWData.getAirTitle2());
        airLoc2.setText(infoWData.getAirLoc2());
        airPm102.setText(infoWData.getAirPm102());
        airPm252.setText(infoWData.getAirPm252());
        airO32.setText(infoWData.getAirO32());

        realTitle.setText(infoWData.getRealTitle());
        realData.setText(infoWData.getRealData());

        festList1.setText(infoWData.getFestList1());
        festList2.setText(infoWData.getFestList2());

        btnMeasure.setText(infoWData.getBtnMeasure());
        btnFavorites.setText(infoWData.getBtnFavorites());
        //////////////////////////////////////////////////////////////////////////////////////////////////

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final View.OnClickListener onMesClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), DetailActivity.class);
                intent.putExtra("pzNo", infoWData.getNo());
                startActivity(intent);
            }
        };

        final View.OnClickListener onFvClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = ((Button)v.findViewById(R.id.btn_favorites)).getText().toString();

                if("즐겨찾기 제거".equals(currentText)) {
                    // 즐겨찾기 테이블에 1개의 데이터셋만 있을 경우 즐겨찾기 제거가 작동하지 않도록 함.
                    if(selectFromFVTable()) {
                        Toast.makeText(getContext(), "즐겨찾기를 삭제할 수 없습니다-" + infoWData.getNo(), Toast.LENGTH_SHORT).show();
                    } else {
                        deleteFromFVTable(infoWData.getNo());
                        ((Button)v.findViewById(R.id.btn_favorites)).setText("즐겨찾기");
                        Toast.makeText(getContext(), "즐겨찾기를 삭제했습니다-" + infoWData.getNo(), Toast.LENGTH_SHORT).show();
                    }
                } else if("즐겨찾기".equals(currentText)) {
                    addedToFVTable(infoWData.getNo());
                    ((Button)v.findViewById(R.id.btn_favorites)).setText("즐겨찾기 제거");
                    Toast.makeText(getContext(), "즐겨찾기를 추가했습니다-" + infoWData.getNo(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        view.findViewById(R.id.btn_measure).setOnClickListener(onMesClickListener);
        view.findViewById(R.id.btn_favorites).setOnClickListener(onFvClickListener);
    }

    private void deleteFromFVTable(int noVal) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sqlDelete = FavoritesDBCtrct.SQL_DELETE_WITH_NO + noVal;
        db.execSQL(sqlDelete);
    }

    private void addedToFVTable(int noVal) {
        SQLiteDatabase db= dbHelper.getWritableDatabase();
        String sqlInsert = FavoritesDBCtrct.SQL_INSERT +
                " (" +
                "'" + noVal +
                "')";
        db.execSQL(sqlInsert);
    }

    private boolean selectFromFVTable() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(FavoritesDBCtrct.SQL_SELECT, null);
        cursor.moveToFirst();

        if(1 == cursor.getCount()) {
            return true;
        } else {
            return false;
        }
    }
}
