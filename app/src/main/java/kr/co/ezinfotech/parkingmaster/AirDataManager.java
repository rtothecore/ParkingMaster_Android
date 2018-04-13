package kr.co.ezinfotech.parkingmaster;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;

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

/**
 * Created by hkim on 2018-04-02.
 */

public class AirDataManager extends Activity {

    final String serviceKey = "73Jjl5lZRvBRKkGsPnGmZ7EL9JtwsWNi3hhCIN8cpVJzMdRRgyzntwz2lHmTKeR1tp7NWzoihNGGazcDEFgh8w%3D%3D";
    Location myLoc = null;

    String stationName = null;
    String pm10Val = null;
    String pm25Val = null;
    String o3Val = null;

    JSONObject result = null;

    TextView tvLoc = null;
    TextView tvPm10 = null;
    TextView tvPm25 = null;
    TextView tvO3 = null;

    int mode = 0;   // 0 : For main activity, 1 : For MapsActivity
    InfoWindowData info;

    public AirDataManager(Location locVal) {
        myLoc = locVal;
    }

    public AirDataManager(Location locVal, InfoWindowData infoVal) {
        mode = 1;
        myLoc = locVal;
        info = infoVal;
    }

    public void setLocTextView(TextView tv) {
        tvLoc = tv;
    }

    public void setPm10TextView(TextView tv) {
        tvPm10 = tv;
    }

    public void setPm25TextView(TextView tv) {
        tvPm25 = tv;
    }

    public void setO3TextView(TextView tv) {
        tvO3 = tv;
    }

    public void setAirData() {
        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                getAirData();
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private void getAirData() {
        //1. 위도,경도 => tmX, tmY로 변환
        GeoPoint gp = new GeoPoint(myLoc.getLongitude(), myLoc.getLatitude());
        GeoPoint tmGp = GeoTrans.convert(GeoTrans.GEO, GeoTrans.TM, gp);
        Location tmLoc = new Location("");
        tmLoc.setLatitude(tmGp.getX());
        tmLoc.setLongitude(tmGp.getY());

        // 2. tmX, tmY로 가까운 stationName 얻기
        getStationName(tmLoc);
    }

    private void getStationName(Location tmLoc) {
        Log.i("getStationName-0", tmLoc.getLatitude() + ", " + tmLoc.getLongitude());

        StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList"); /*URL*/
        try {
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + serviceKey); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("tmX","UTF-8") + "=" + URLEncoder.encode(String.valueOf(tmLoc.getLatitude()), "UTF-8")); /*TM측정방식 X좌표*/
            urlBuilder.append("&" + URLEncoder.encode("tmY","UTF-8") + "=" + URLEncoder.encode(String.valueOf(tmLoc.getLongitude()), "UTF-8")); /*TM측정방식 Y좌표*/
            urlBuilder.append("&" + URLEncoder.encode("_returnType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i("getStationName-1", urlBuilder.toString());

        URL url = null;
        try {
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        conn.setRequestProperty("Content-type", "application/json");
        try {
            System.out.println("Response code: " + conn.getResponseCode());
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            rd.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        conn.disconnect();
        Log.i("getStationName-2", sb.toString());

        try {
            result = new JSONObject(sb.toString());
        } catch (Throwable t) {
            Log.e("getStationName-3", "Could not parse malformed JSON");
            return;
        }

        Log.i("getStationName-4", result.toString());

        /////////////////////////////// Parsing JSON ////////////////////////////////////////////
        JSONObject jsonTemp = null;
        JSONArray jsonaTemp = null;

        try {
            jsonaTemp = (JSONArray)result.get("list");
            jsonTemp = (JSONObject)jsonaTemp.get(0);
            stationName = jsonTemp.getString("stationName");
        } catch (Throwable t){
            Log.e("getStationName-5", "Could not parse malformed JSON");
        }
        Log.i("getStationName-6", stationName);
        /////////////////////////////// Parsing JSON ////////////////////////////////////////////

        getRltmMeasuerData();
    }

    private void getRltmMeasuerData() {
        Log.i("getRltmMeasuerData-0", "Get air data");

        StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"); /*URL*/
        try {
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
            urlBuilder.append("&" + URLEncoder.encode("stationName", "UTF-8") + "=" + URLEncoder.encode(stationName, "UTF-8")); /*측정소 이름*/
            urlBuilder.append("&" + URLEncoder.encode("dataTerm", "UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8")); /*요청 데이터기간 (하루 : DAILY, 한달 : MONTH, 3달 : 3MONTH)*/
            urlBuilder.append("&" + URLEncoder.encode("ver", "UTF-8") + "=" + URLEncoder.encode("1.3", "UTF-8")); /*버전별 상세 결과 참고문서 참조*/
            urlBuilder.append("&" + URLEncoder.encode("_returnType", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i("getRltmMeasuerData-1", urlBuilder.toString());

        URL url = null;
        try {
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        conn.setRequestProperty("Content-type", "application/json");
        try {
            System.out.println("Response code: " + conn.getResponseCode());
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            rd.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        conn.disconnect();
        Log.i("getRltmMeasuerData-2", sb.toString());

        try {
            result = new JSONObject(sb.toString());
        } catch (Throwable t) {
            Log.e("getRltmMeasuerData-3", "Could not parse malformed JSON");
        }

        Log.i("getRltmMeasuerData-4", result.toString());

        /////////////////////////////// Parsing JSON ////////////////////////////////////////////
        JSONObject jsonTemp = null;
        JSONArray jsonaTemp = null;

        try {
            jsonaTemp = (JSONArray)result.get("list");
            jsonTemp = (JSONObject)jsonaTemp.get(0);
            pm10Val = jsonTemp.getString("pm10Value");
            pm25Val = jsonTemp.getString("pm25Value");
            o3Val = jsonTemp.getString("o3Value");
        } catch (Throwable t){
            Log.e("getStationName-5", "Could not parse malformed JSON");
        }
        Log.i("getStationName-6", "pm10Value:" + pm10Val + ", pm25Val:" + pm25Val + ", o3Val:" + o3Val);
        /////////////////////////////// Parsing JSON ////////////////////////////////////////////

        if(0 == mode) {
            setAirLocStatus();
            setAirPm10Status();
            setAirPm25Status();
            setAirO3Status();
        } else if (1 == mode) {
            info.setAirTitle2("대기정보");
            info.setAirLoc2(stationName);
            info.setAirPm102("미세먼지: " + pm10Val + "㎍/㎥");
            info.setAirPm252("초미세먼지: " + pm25Val + "㎍/㎥");
            info.setAirO32("오존지수: " + o3Val + "ppm");
        }
    }

    private void setAirLocStatus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        tvLoc.setText(stationName);
                    }
                });
            }
        }).start();
    }

    private void setAirPm10Status() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        tvPm10.setText("미세먼지 : " + pm10Val + "㎍/㎥");
                    }
                });
            }
        }).start();
    }

    private void setAirPm25Status() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        tvPm25.setText("초미세먼지 : " + pm25Val + "㎍/㎥");
                    }
                });
            }
        }).start();
    }

    private void setAirO3Status() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        tvO3.setText("오존지수 : " + o3Val + "ppm");
                    }
                });
            }
        }).start();
    }

}
