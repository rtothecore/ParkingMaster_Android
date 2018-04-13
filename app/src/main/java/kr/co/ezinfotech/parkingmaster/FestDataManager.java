package kr.co.ezinfotech.parkingmaster;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.gun0912.tedpermission.util.ObjectUtils;

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
 * Created by hkim on 2018-04-02.
 */

public class FestDataManager extends Activity {

    final String serviceKey = "73Jjl5lZRvBRKkGsPnGmZ7EL9JtwsWNi3hhCIN8cpVJzMdRRgyzntwz2lHmTKeR1tp7NWzoihNGGazcDEFgh8w%3D%3D";

    JSONObject result = null;

    ArrayList<FestData> fdList = new ArrayList<>();
    ArrayList<FestData> nearFdList = new ArrayList<>();

    private Geocoder mCoder;

    final static float RADIUS_DISTANCE = 1000; // meter
    private Location pointLoc;

    InfoWindowData info;

    public FestDataManager(InfoWindowData infoVal) {
        mCoder = new Geocoder(this);
        info = infoVal;
    }

    public void setFestData() {
        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                getFestData();
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    public void setNearPoint(Location pointLocVal) {
        pointLoc = pointLocVal;
    }

    private void getFestData() {
        Log.i("getFestData()-0", "Get fest data");

        StringBuilder urlBuilder = new StringBuilder("http://210.99.248.79/rest/FestivalInquiryService/getFestivalList"); /*URL*/
        try {
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + this.serviceKey);
            urlBuilder.append("&" + URLEncoder.encode("startPage","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*시작 페이지*/
            urlBuilder.append("&" + URLEncoder.encode("pageSize","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*페이지 사이즈*/
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } /*Service Key*/

        Log.i("getFestData()-1", urlBuilder.toString());

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
        BufferedReader rd = null;
        StringBuilder sb = null;
        try {
            System.out.println("Response code: " + conn.getResponseCode());

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            sb = new StringBuilder();
            String line = null;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        conn.disconnect();
        Log.i("getFestData()-2", sb.toString());

        // convert XML to JSON - https://github.com/smart-fun/XmlToJson
        XmlToJson xmlToJson = new XmlToJson.Builder(sb.toString()).build();
        result = xmlToJson.toJson();

        Log.i("FestDataManager", result.toString());

        //setFestTitleStatus();
        //setFestLocStatus();
        ///////////////////////////////// Parsing JSON ///////////////////////////////////
        JSONObject jsonTemp = null;
        JSONArray jsonaTemp = null;

        try {
            jsonTemp = (JSONObject) result.get("rfcOpenApi");
            jsonTemp = (JSONObject) jsonTemp.get("body");
            jsonTemp = (JSONObject) jsonTemp.get("data");
            jsonaTemp = (JSONArray) jsonTemp.get("list");

            for(int i = 0; i < jsonaTemp.length(); i++) {
                jsonTemp = (JSONObject)jsonaTemp.get(i);
                FestData tempFd = new FestData();
                tempFd.title = jsonTemp.getString("title");
                tempFd.sDate = jsonTemp.getString("sdate");
                tempFd.eDate = jsonTemp.getString("edate");
                tempFd.location = jsonTemp.getString("location");
                tempFd.loc = runGeoCoding(tempFd.location);
                fdList.add(tempFd);
            }
        } catch (Throwable t){
            Log.e("getFestData", "Could not parse malformed JSON");
        }
        ///////////////////////////////// Parsing JSON ///////////////////////////////////

        setNearFestData();
    }

    // pointLoc과 RADIUS_DISTANCE 거리 이하에 속하는 축제데이터를 조회해서 nearFdList에 add
    private void setNearFestData() {
        for(int i = 0; i < fdList.size(); i++) {
            if(null != fdList.get(i).loc) {
                float dist = pointLoc.distanceTo(fdList.get(i).loc);
                if (RADIUS_DISTANCE >= dist) {
                    nearFdList.add(fdList.get(i));
                }
            }
        }

        if( nearFdList.size() >= 1 &&
                !ObjectUtils.isEmpty(nearFdList.get(0))) {
            info.setFestList1(nearFdList.get(0).title);
        }

        if( nearFdList.size() >= 2 &&
                !ObjectUtils.isEmpty(nearFdList.get(1))) {
            info.setFestList2(nearFdList.get(1).title);
        }
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
            Log.e("runGeoCoding","입출력 오류 - 서버에서 주소변환시 에러발생-" + addr);
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
