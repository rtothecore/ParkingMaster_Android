package kr.co.ezinfotech.parkingmaster;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

/**
 * Created by hkim on 2018-04-17.
 */

public class PZDataManager extends Activity {

    private int mode = 0;   // 0: just parse & insert, 1: check datadate, delete & insert PZ table
    Handler mHandler = null;

    JSONArray result = null;
    List<PZData> pzData = new ArrayList<>();

    public PZDataManager() {
    }

    public void setMode(int modeVal) {
        mode = modeVal;
    }

    public void setHandler(Handler handlerVal) {
        mHandler = handlerVal;
    }

    public void setPZData() {
        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                //android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);
                getPZData();

                Message message = Message.obtain();
                message.arg1 = 777;
                mHandler.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private void getPZData() {
        Log.i("getPZData()-0", "Get PZ data");

        StringBuilder urlBuilder = new StringBuilder("http://192.168.66.3:8080/pz/all"); /*URL*/

        Log.i("getPZData()-1", urlBuilder.toString());

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

        try {
            result = new JSONArray(sb.toString());
        } catch (Throwable t) {
            Log.e("getPZData-3", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        switch (mode) {
            case 0 :
                parseNInsertPZ();
                break;
            case 1 :
                checkDataDateNInsertPZ();
                break;
            default :
                break;
        }
    }

    private void checkDataDateNInsertPZ() {
        String dataDateVal = "";
        try{
            JSONObject jsonTemp = (JSONObject)result.get(0);
            dataDateVal = jsonTemp.getString("data_date");
            if(isExistDataWithDataDate(dataDateVal)) {
                // dateDataVal과 일치하는 데이터셋이 존재하므로 테이블 업데이트 안함
            } else {
                // dateDataVal과 데이터셋이 일치하지 않으므로 테이블 업데이트(delete & insert)
                deletePZTable();
                parseNInsertPZ();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            Log.e("checkDataDateNInsertPZ", "Could not parse malformed JSON");
        }
    }

    private void deletePZTable() {
        SQLiteDatabase db = UtilManager.dbHelper.getWritableDatabase();
        db.execSQL(ParkingZoneDBCtrct.SQL_DELETE);
    }

    private boolean isExistDataWithDataDate(String dataDateVal) {
        SQLiteDatabase db = UtilManager.dbHelper.getReadableDatabase();
        String sqlSelect = ParkingZoneDBCtrct.SQL_SELECT_WITH_DATADATE + dataDateVal + "' LIMIT 1";
        Log.i("isExistDataWithDataDate", sqlSelect);
        Cursor cursor = db.rawQuery(sqlSelect, null);
        cursor.moveToFirst();
        if(0 == cursor.getCount()) {
            return false;
        } else {
            return true;
        }
    }

    private void parseNInsertPZ() {
        ////////////////////////////// Parsing JSON ////////////////////////////////////////
        try {
            for(int i = 0; i < result.length(); i++) {
                JSONObject jsonTemp = (JSONObject)result.get(i);

                PZData tempPz = new PZData();
                tempPz.no = jsonTemp.getString("no");
                tempPz.name = jsonTemp.getString("name");
                tempPz.addr = jsonTemp.getString("addr_road");
                tempPz.tel = jsonTemp.getString("tel");
                tempPz.loc = new Location("");
                tempPz.loc.setLatitude(ParseDouble(jsonTemp.getString("lat")));
                tempPz.loc.setLongitude(ParseDouble(jsonTemp.getString("lng")));
                tempPz.totalP = jsonTemp.getString("total_p");
                tempPz.opDate = jsonTemp.getString("op_date");

                JSONObject jsonTemp2 = (JSONObject)jsonTemp.get("w_op");
                tempPz.w_op = new PZTermData();
                tempPz.w_op.start_date = jsonTemp2.getString("start_date");
                tempPz.w_op.end_date = jsonTemp2.getString("end_date");
                tempPz.s_op = new PZTermData();
                jsonTemp2 = (JSONObject)jsonTemp.get("s_op");
                tempPz.s_op.start_date = jsonTemp2.getString("start_date");
                tempPz.s_op.end_date = jsonTemp2.getString("end_date");
                tempPz.h_op = new PZTermData();
                jsonTemp2 = (JSONObject)jsonTemp.get("h_op");
                tempPz.h_op.start_date = jsonTemp2.getString("start_date");
                tempPz.h_op.end_date = jsonTemp2.getString("end_date");

                tempPz.feeInfo = jsonTemp.getString("fee_info");

                tempPz.park_base = new PZTFData();
                jsonTemp2 = (JSONObject)jsonTemp.get("park_base");
                tempPz.park_base.time = jsonTemp2.getString("time");
                tempPz.park_base.fee = jsonTemp2.getString("fee");
                tempPz.add_term = new PZTFData();
                jsonTemp2 = (JSONObject)jsonTemp.get("add_term");
                tempPz.add_term.time = jsonTemp2.getString("time");
                tempPz.add_term.fee = jsonTemp2.getString("fee");
                tempPz.one_day_park = new PZTFData();
                jsonTemp2 = (JSONObject)jsonTemp.get("one_day_park");
                tempPz.one_day_park.time = jsonTemp2.getString("time");
                tempPz.one_day_park.fee = jsonTemp2.getString("fee");

                tempPz.remarks = jsonTemp.getString("remarks");
                tempPz.dataDate = jsonTemp.getString("data_date");
                pzData.add(tempPz);
            }
        } catch (Throwable t) {
            Log.e("parseNInsertPZ()-0", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        ///////////////////////////////// INSERT DB //////////////////////////////////////////
        for(int i = 0; i < pzData.size(); i++) {
            PZData tempPzData = pzData.get(i);
            // 1. PZData에 위,경도값이 없을 경우 주소를 지오코딩하여 DB에 Insert
            if("-1".equals(tempPzData.loc.getLatitude()) || "-1".equals(tempPzData.loc.getLongitude())
                    || "0".equals(tempPzData.loc.getLatitude()) || "0".equals(tempPzData.loc.getLongitude())
                    || "0.0".equals(tempPzData.loc.getLatitude()) || "0.0".equals(tempPzData.loc.getLongitude())
                    || 0 == tempPzData.loc.getLatitude() || 0 == tempPzData.loc.getLongitude()
                    ) {
                Location tempLoc = UtilManager.runGeoCoding(tempPzData.addr);
                tempPzData.loc.setLatitude(tempLoc.getLatitude());
                tempPzData.loc.setLongitude(tempLoc.getLongitude());
                insertPZTableWithPzData(tempPzData);
                Log.e("parseNInsertPZ()-1", "Could not parse malformed JSON");
            } else {    // 2. PZData에 위,경도값이 있는 경우
                insertPZTableWithPzData(tempPzData);
                Log.e("parseNInsertPZ()-2", "Could not parse malformed JSON");
            }
        }
    }

    private double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch(Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return 0;
    }

    private void insertPZTableWithPzData(PZData pzData) {
        SQLiteDatabase db = UtilManager.dbHelper.getWritableDatabase();

        String sqlInsert = ParkingZoneDBCtrct.SQL_INSERT +
                " (" +
                "'" + pzData.no + "', " +
                "'" + pzData.name + "', " +
                "'" + pzData.addr + "', " +
                "'" + pzData.tel + "', " +
                "'" + pzData.loc.getLatitude() + "', " +
                "'" + pzData.loc.getLongitude() + "', " +
                "'" + pzData.totalP + "', " +
                "'" + pzData.opDate + "', " +
                "'" + pzData.w_op.start_date + "', " +
                "'" + pzData.w_op.end_date + "', " +
                "'" + pzData.s_op.start_date + "', " +
                "'" + pzData.s_op.end_date + "', " +
                "'" + pzData.h_op.start_date + "', " +
                "'" + pzData.h_op.end_date + "', " +
                "'" + pzData.feeInfo + "', " +
                "'" + pzData.park_base.time + "', " +
                "'" + pzData.park_base.fee + "', " +
                "'" + pzData.add_term.time + "', " +
                "'" + pzData.add_term.fee + "', " +
                "'" + pzData.remarks + "', " +
                "'" + pzData.dataDate +
                "')";
        db.execSQL(sqlInsert);
        Log.i("insertPZTableWithPzData", sqlInsert);
    }
}
