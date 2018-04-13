package kr.co.ezinfotech.parkingmaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

/**
 * Created by hkim on 2018-04-03.
 */

public class PisDataManager extends Activity {

    final String serviceKey = "73Jjl5lZRvBRKkGsPnGmZ7EL9JtwsWNi3hhCIN8cpVJzMdRRgyzntwz2lHmTKeR1tp7NWzoihNGGazcDEFgh8w%3D%3D";
    JSONObject result = null;
    List<PisData> pisData = new ArrayList<>();

    private Geocoder mCoder = new Geocoder(this);

    ParkingMasterDBHelper dbHelper = null;
    final static float RADIUS_DISTANCE = 1000; // meter
    Location myLoc = new Location("myLoc");

    int runMode = 0;    // 0: Show favorites, 1: Search parking info
    String searchStr = null;
    ArrayList<PZData> searchedPZData = new ArrayList<>();
    ArrayList<PisData> searchedPisData = new ArrayList<>();
    Context ctx = null;
    LinearLayout ll = null;
    BarChart bc = null;

    int selectedPZNo = 0;

    public void PisDataManager() {
    }

    public void setLL(LinearLayout llVal) {
        ll = llVal;
    }

    public void setBC(BarChart bcVal) {
        bc = bcVal;
    }

    public void setClickedPZNo(int pzNoVal) {
        selectedPZNo = pzNoVal;
    }

    public void setContext(Context ctxVal) {
        ctx = ctxVal;
    }

    public void setSearchStr(String searchStrVal) {
        searchStr = searchStrVal;
    }

    public void setMode(int modeVal) {
        runMode = modeVal;
    }

    public void setDbHelper(Context ctxVal) {
        dbHelper = new ParkingMasterDBHelper(ctxVal);
    }

    public void setLoc(Location myLocVal) {
        this.myLoc = myLocVal;
    }

    public void setPisData() {
        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                getPisData();
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private void getPisData() {
        Log.i("getPisData()-0", "Get PIS data");

        StringBuilder urlBuilder = new StringBuilder("http://openapi.jejuits.go.kr/OPEN_API/pisInfo/getPisInfo"); /*URL*/
        try {
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + serviceKey); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i("getPisData()-1", urlBuilder.toString());

        URL url = null;
        try {
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setRequestProperty("Content-type", "application/json");
        try {
            System.out.println("Response code: " + conn.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader rd = null;
        try {
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.disconnect();
        Log.i("getPisData()-2", sb.toString());

        // convert XML to JSON - https://github.com/smart-fun/XmlToJson
        XmlToJson xmlToJson = new XmlToJson.Builder(sb.toString()).build();
        result = xmlToJson.toJson();

        Log.i("PisDataManager", result.toString());

        JSONObject jsonTemp = null;
        JSONArray jsonaTemp = null;

        try {
            jsonTemp = (JSONObject) result.get("ServiceResult");
            jsonTemp = (JSONObject) jsonTemp.get("msgBody");
            jsonaTemp = (JSONArray) jsonTemp.get("itemList");

            for(int i = 0; i < jsonaTemp.length(); i++) {
                jsonTemp = (JSONObject)jsonaTemp.get(i);
                //Log.i("getPisData()-3", jsonTemp.getString("ISTL_LCTN_ADDR"));
                PisData tempPd = new PisData();
                tempPd.emvhNum = jsonTemp.getInt("EMVH_RMND_PRZN_NUM");
                tempPd.etcNum = jsonTemp.getInt("ETC_RMND_PRZN_NUM");
                tempPd.gnrlNum = jsonTemp.getInt("GNRL_RMND_PRZN_NUM");
                tempPd.hndcNum = jsonTemp.getInt("HNDC_RMND_PRZN_NUM");
                tempPd.hvvhNum = jsonTemp.getInt("HVVH_RMND_PRZN_NUM");
                tempPd.addr = jsonTemp.getString("ISTL_LCTN_ADDR");
                tempPd.lgvhNum = jsonTemp.getInt("LGVH_RMND_PRZN_NUM");
                tempPd.wholNum = jsonTemp.getInt("WHOL_NPLS");
                tempPd.wmonNum = jsonTemp.getInt("WMON_RMND_PRZN_NUM");

                pisData.add(tempPd);
            }
        } catch (Throwable t){
            Log.e("getPisData", "Could not parse malformed JSON");
        }

        switch(runMode) {
            case 0 :
                initFavoritesTable();
                break;
            case 1 :
                searchParkingzone();
                break;
            case 2 :
                showOnlyOnePZ();
                break;
            default :
                break;
        }
    }

    private void showOnlyOnePZ() {
        searchedPZData = new ArrayList<>();
        searchedPisData = new ArrayList<>();

        searchedPZData.add(selectWithNo(selectedPZNo));

        PisData tempPD = getSameAddrPisData(searchedPZData.get(0).name);
        if(null != tempPD) {
            searchedPisData.add(tempPD);
        }

        // Transfer data to MapsActivity
        Intent intent = new Intent(ctx, MapsActivity.class);
        intent.putParcelableArrayListExtra("searchedPZData", searchedPZData);
        intent.putParcelableArrayListExtra("searchedPisData", searchedPisData);
        ctx.startActivity(intent);
    }

    private void searchParkingzone() {
        Location searchLoc = runGeoCoding(searchStr);   // 1. 검색어를 위, 경도로 변환

        // 2. PARKING_ZONE 테이블에서 모든 데이터셋의 위,경도를 SELECT
        PZData[] pzData = selectNoLatLngName();

        // 3. 2번에서 얻은 모든 데이터의 위,경도가 1번의 검색어 위,경도의 반경 1km 안에 있는지 검색
        for(int i = 0; i < pzData.length; i++) {
            float dist = searchLoc.distanceTo(pzData[i].loc);   // meter
            if(RADIUS_DISTANCE >= dist) {   // 4. 검색한 결과를 보여줌
                Log.i("searchParkingzone-0", "RESULT:" + pzData[i].no + "/" + pzData[i].name);
                searchedPZData.add(selectWithNo(pzData[i].no));
                PisData tempPD = getSameAddrPisData(pzData[i].name);
                if(null != tempPD) {
                    searchedPisData.add(tempPD);
                }
                Log.i("searchParkingzone-1", "RESULT:" + searchedPZData.size() + "/" + searchedPisData.size());
            }
        }

        // Transfer data to MapsActivity
        Intent intent = new Intent(ctx, MapsActivity.class);
        intent.putParcelableArrayListExtra("searchedPZData", searchedPZData);
        intent.putParcelableArrayListExtra("searchedPisData", searchedPisData);
        ctx.startActivity(intent);
    }

    private void initFavoritesTable() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(FavoritesDBCtrct.SQL_SELECT, null);
        cursor.moveToFirst();

        // FAVORITES 테이블에서 SELECT 한 데이터셋이 0이면 현재 사용자 위치를 기준으로 반경 10km 이내에 있는 주차장 정보를 INSERT
        if(0 == cursor.getCount()) {
            // 1. 공공데이터서버로 주차장정보 요청
            // 2. 모든 주차장 정보를 받아 주소를 위,경도로 변환(지오코딩)
            for(int i = 0; i < pisData.size(); i++) {
                pisData.get(i).loc =  runGeoCoding(pisData.get(i).addr);
                // 2-1. 지오코딩한 결과가 0,0 일 경우 PARKING_ZONE에서 Addr로 검색하여 Lat, Lng 값을 SELECT
                if(0 == pisData.get(i).loc.getLatitude() || 0 == pisData.get(i).loc.getLongitude()) {
                    pisData.get(i).loc = selectLatNLngWithName(pisData.get(i).addr);
                }
                // 2-2. 현재위치를 기준으로 반경 1km 이내에 있는 2번을 검색
                float distance = myLoc.distanceTo(pisData.get(i).loc);
                if(RADIUS_DISTANCE >= distance) {
                    // 2-3. 2-2번에서 검색한 내용 중 1번째 주차장을 테이블에 INSERT
                    int selectedNo = selectNoWithName(pisData.get(i).addr);
                    insertFVTable(selectedNo);
                    Log.i("initFavoritesTable-0", "insertFVTable(" + selectedNo + ")");
                }
            }
        }

        // FAVORITE 테이블에 추가된 데이터셋이 없을 경우 공공데이터서버로부터 받은 4개의 주차장정보를 INSERT
        cursor = db.rawQuery(FavoritesDBCtrct.SQL_SELECT, null);
        cursor.moveToFirst();
        if(0 == cursor.getCount()) {
            for(int i = 0; i < pisData.size(); i++) {
                insertFVTable(selectNoWithName(pisData.get(i).addr));
                Log.i("initFavoritesTable-1", "insertFVTable(" + String.valueOf(selectNoWithName(pisData.get(i).addr)) + ")");
            }
        }

        // FAVORITES 테이블에서 SELECT한 데이터셋이 0이 아니면 해당 데이터셋들을 보여줌
        showFavorites();
    }

    private PisData getSameAddrPisData(String NameVal) {
        PisData tempPD = null;
        for(int i = 0; i < pisData.size(); i++) {
            if(NameVal.equals(pisData.get(i).addr)) {
                tempPD = new PisData();
                tempPD = pisData.get(i);
            }
        }
        return tempPD;
    }

    private PZData selectWithNo(int noVal) {
        SQLiteDatabase db= dbHelper.getReadableDatabase();
        String sqlSelect = ParkingZoneDBCtrct.SQL_SELECT_WITH_NO + noVal;
        Cursor cursor = db.rawQuery(sqlSelect, null);

        PZData tempPZData = new PZData();

        if(cursor.moveToFirst()) {
            tempPZData.no = cursor.getInt(0);
            tempPZData.name = cursor.getString(1);
            tempPZData.addr = cursor.getString(2);
            tempPZData.tel = cursor.getString(3);
            tempPZData.loc = new Location("");
            tempPZData.loc.setLatitude(Double.parseDouble(cursor.getString(4)));
            tempPZData.loc.setLongitude(Double.parseDouble(cursor.getString(5)));
            tempPZData.totalP = cursor.getString(6);
            tempPZData.opDate = cursor.getString(7);
            tempPZData.wOpStart = cursor.getString(8);
            tempPZData.wOpEnd = cursor.getString(9);
            tempPZData.sOpStart = cursor.getString(10);
            tempPZData.sOpEnd = cursor.getString(11);
            tempPZData.hOpStart = cursor.getString(12);
            tempPZData.hOpEnd = cursor.getString(13);
            tempPZData.feeInfo = cursor.getString(14);
            tempPZData.baseTime = cursor.getString(15);
            tempPZData.baseFee = cursor.getString(16);
            tempPZData.addTermTime = cursor.getString(17);
            tempPZData.addTermFee = cursor.getString(18);
            tempPZData.remarks = cursor.getString(19);
        }
        return tempPZData;
    }

    private PZData[] selectNoLatLngName() {
        SQLiteDatabase db= dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(ParkingZoneDBCtrct.SQL_SELECT_NO_LAT_LNG_NAME, null);

        PZData[] tempPZData = new PZData[cursor.getCount()];

        if(cursor.moveToFirst()) {
            for(int i = 0; i < cursor.getCount(); i++) {
                //Log.i("selectLatLng", "LAT:" + cursor.getString(0) + " LNG:" + cursor.getString(1) );
                tempPZData[i] = new PZData();
                tempPZData[i].no = cursor.getInt(0);
                tempPZData[i].loc = new Location("");
                tempPZData[i].loc.setLatitude(Double.parseDouble(cursor.getString(1)));
                tempPZData[i].loc.setLongitude(Double.parseDouble(cursor.getString(2)));
                tempPZData[i].name = cursor.getString(3);
                cursor.moveToNext();
            }
        }

        return tempPZData;
    }

    private void showFavorites() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(FavoritesDBCtrct.SQL_SELECT, null);
        cursor.moveToFirst();
        final BarChartManager bcm = new BarChartManager(bc, ctx);

        for(int i = 0; i < cursor.getCount(); i++) {
            Log.i("showFavorites-0", "cursor.genInt(0):" + cursor.getInt(0));
            final PZData tempPZD = selectWithNo(cursor.getInt(0));
            final PisDataManager pdmThis = this;
            final int iVal = i;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //tvPksTitles.get(intVal).setText(tempPZD.name);
                            if(null == ctx) {
                                Log.i("showFavorites", "ctx is null");
                            }
                            PieChartManager pcm = new PieChartManager(pdmThis, tempPZD.no, ctx, ll, tempPZD.name);
                            Log.i("showFavorites-1", "tempPZD.name:" + tempPZD.name);
                            if(0 == iVal) {
                                pcm.removeLayout();
                            }
                            pcm.createLayout();
                            pcm.draw();
                        }
                    });
                }
            }).start();

            if(0 == i) {
                bcm.clearLabelStr();
            }
            bcm.setLabelStr(tempPZD.name);
            cursor.moveToNext();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        bcm.draw();
                    }
                });
            }
        }).start();
    }

    private void insertFVTable(int no) {
        SQLiteDatabase db= dbHelper.getWritableDatabase();

        String sqlInsert = FavoritesDBCtrct.SQL_INSERT +
                " (" +
                "'" + no +
                "')";
        db.execSQL(sqlInsert);
    }

    private int selectNoWithName(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sqlSelect = ParkingZoneDBCtrct.SQL_SELECT_NO_WITH_NAME + name + "'";
        Cursor cursor = db.rawQuery(sqlSelect, null);
        int result = 0;

        if(cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    private Location selectLatNLngWithName(String name) {

        Location tempLoc = new Location("");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sqlSelect = ParkingZoneDBCtrct.SQL_SELECT_LAT_LNG_WITH_NAME + name + "'";
        Cursor cursor = db.rawQuery(sqlSelect, null);

        if (cursor.moveToFirst()) {
            tempLoc.setLatitude(Double.parseDouble(cursor.getString(0)));
            tempLoc.setLongitude(Double.parseDouble(cursor.getString(1)));
            Log.i("selectLatNLngWithName", "LAT: " + tempLoc.getLatitude() + " LNG: " + tempLoc.getLongitude());
            return tempLoc;
        }
        return null;
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
}
