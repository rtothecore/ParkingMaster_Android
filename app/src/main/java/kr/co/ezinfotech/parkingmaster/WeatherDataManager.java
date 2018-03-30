package kr.co.ezinfotech.parkingmaster;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hkim on 2018-03-28.
 */

public class WeatherDataManager extends Activity{

    final String serviceKey = "73Jjl5lZRvBRKkGsPnGmZ7EL9JtwsWNi3hhCIN8cpVJzMdRRgyzntwz2lHmTKeR1tp7NWzoihNGGazcDEFgh8w%3D%3D";
    String baseDate;
    String baseTime;
    double nx;
    double ny;

    int skyStatus = 0;
    String t1hStatus = "0";

    JSONObject result = null;
    TextView tvTime = null;
    TextView tvSky = null;
    TextView tvT1h = null;
    ImageView ivSky = null;

    public static int TO_GRID = 0;
    public static int TO_GPS = 1;

    public WeatherDataManager(double lat, double lng, TextView tvTime) {
        this.tvTime = tvTime;
        getTime();

        LatXLngY tmp = convertGRID_GPS(TO_GRID, lat, lng);
        nx = tmp.x;
        ny = tmp.y;
    }

    public void setSkyTextView(TextView tv) {
        tvSky = tv;
    }

    public void setT1hTextView(TextView tv) {
        tvT1h = tv;
    }

    public void setSkyImageView(ImageView iv) { ivSky = iv; }

    public void setWeatherData() {
        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                getWeatherData();
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private void getTime() {

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy");
        String nowYear = sdf6.format(date);

        SimpleDateFormat sdf4 = new SimpleDateFormat("MM");
        String nowMonth = sdf4.format(date);
        int inowMonth = Integer.parseInt(nowMonth);

        SimpleDateFormat sdf5 = new SimpleDateFormat("dd");
        String nowDay = sdf5.format(date);
        int inowDay = Integer.parseInt(nowDay);

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH");
        String nowHours = sdf2.format(date);
        int inowHours = Integer.parseInt(nowHours);

        SimpleDateFormat sdf3 = new SimpleDateFormat("mm");
        String nowMinutes = sdf3.format(date);
        int inowMinutes = Integer.parseInt(nowMinutes);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        if(inowMinutes < 30){
            // 30분보다 작으면 한시간 전 값
            inowHours = inowHours - 1;
            if(inowHours < 0){
                // 자정 이전은 전날로 계산
                cal.add(Calendar.DATE, -1);

                nowDay = sdf5.format(cal.getTime());
                inowDay = Integer.parseInt(nowDay);

                nowMonth = sdf4.format(cal.getTime());
                inowMonth = Integer.parseInt(nowMonth);

                nowYear = sdf6.format(cal.getTime());

                nowHours = "23";
                inowHours = Integer.parseInt(nowHours);
            }
        }
        /*
        if(inowHours < 10) {
            nowHours = '0' + nowHours;
        }
        if(inowMonth < 10) {
            nowMonth = '0' + nowMonth;
        }
        if(inowDay < 10) {
            nowDay = '0' + nowDay;
        }
        */

        baseDate = nowYear + "" + nowMonth + "" + nowDay;
        baseTime = nowHours + "00";

        tvTime.setText("기상청 발표 - " + nowHours + "시 기준");

        Log.i("getTime()", baseDate + "/" + baseTime);
    }

    // https://gist.github.com/fronteer-kr/14d7f779d52a21ac2f16
    private LatXLngY convertGRID_GPS(int mode, double lat_X, double lng_Y )
    {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)

        //
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        //
        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        LatXLngY rs = new LatXLngY();

        if (mode == TO_GRID) {
            rs.lat = lat_X;
            rs.lng = lng_Y;
            double ra = Math.tan(Math.PI * 0.25 + (lat_X) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = lng_Y * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        }
        else {
            rs.x = lat_X;
            rs.y = lng_Y;
            double xn = lat_X - XO;
            double yn = ro - lng_Y + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) {
                ra = -ra;
            }
            double alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

            double theta = 0.0;
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            }
            else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) {
                        theta = -theta;
                    }
                }
                else theta = Math.atan2(xn, yn);
            }
            double alon = theta / sn + olon;
            rs.lat = alat * RADDEG;
            rs.lng = alon * RADDEG;
        }
        return rs;
    }

    class LatXLngY
    {
        public double lat;
        public double lng;

        public double x;
        public double y;
    }

    private void getWeatherData() {
        //Log.i("getWeatherData()-0", "Get weather data with date, time, nx, ny info");

        StringBuilder urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastGrib"); /*URL*/
        try {
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + this.serviceKey);
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(this.baseDate, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(this.baseTime, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(Integer.toString((int)this.nx), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(Integer.toString((int)this.ny), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } /*Service Key*/

        Log.i("getWeatherData()-1", urlBuilder.toString());

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
        Log.i("getWeatherData()-2", sb.toString());

        try {
            result = new JSONObject(sb.toString());
        } catch (Throwable t) {
            Log.e("WeatherDataManager", "Could not parse malformed JSON");
        }

        Log.i("WeatherDataManager", result.toString());

        setWeatherSkyStatus();
        setWeatherT1hStatus();
        setWeatherSkyImage();
    }

    // 하늘상태(SKY) 코드 : 맑음(1), 구름조금(2), 구름많음(3), 흐림(4)
    private String convertSkyCodeToStr(int codeVal) {
        String returnVal = null;
        switch(codeVal) {
            case 1 :
                returnVal = "맑음";
                break;
            case 2 :
                returnVal = "구름조금";
                break;
            case 3 :
                returnVal = "구름많음";
                break;
            case 4 :
                returnVal = "흐림";
                break;
            default :
                    returnVal = "--";
                    break;
        }
        return returnVal;
    }

    private void setWeatherSkyStatus() {
        JSONObject jsonTemp = null;
        JSONArray jsonaTemp = null;

        try {
            jsonTemp = (JSONObject)result.get("response");
            jsonTemp = (JSONObject)jsonTemp.get("body");

            // {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},"body":{"items":"","numOfRows":10,"pageNo":1,"totalCount":0}}} 일 경우
            if(0 == jsonTemp.getInt("totalCount")) {

                /////////////////////////////////////////////
                // http://itmining.tistory.com/6
                // http://codetravel.tistory.com/12
                // http://nocomet.tistory.com/10
                ////////////////////////////////////////////
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                tvSky.setText("날씨정보 : --");
                            }
                        });
                    }
                }).start();
                Log.e("setWeatherSkyStatus", "JSON totalCount 0");
                return;
            }

            jsonTemp = (JSONObject)jsonTemp.get("items");
            jsonaTemp = (JSONArray)jsonTemp.get("item");
            jsonTemp = (JSONObject)jsonaTemp.get(4);
            skyStatus = jsonTemp.getInt("obsrValue");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            tvSky.setText("날씨정보 : " + convertSkyCodeToStr(skyStatus));
                        }
                    });
                }
            }).start();
        } catch (Throwable t){
            Log.e("setWeatherSkyStatus", "Could not parse malformed JSON");
        }
    }

    private void setWeatherT1hStatus() {
        JSONObject jsonTemp = null;
        JSONArray jsonaTemp = null;

        try {
            jsonTemp = (JSONObject)result.get("response");
            jsonTemp = (JSONObject)jsonTemp.get("body");

            // {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},"body":{"items":"","numOfRows":10,"pageNo":1,"totalCount":0}}} 일 경우
            if(0 == jsonTemp.getInt("totalCount")) {
                /////////////////////////////////////////////
                // http://itmining.tistory.com/6
                // http://codetravel.tistory.com/12
                // http://nocomet.tistory.com/10
                ////////////////////////////////////////////
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                tvT1h.setText("--℃");
                            }
                        });
                    }
                }).start();
                Log.e("setWeatherT1hStatus", "JSON totalCount 0");
                return;
            }

            jsonTemp = (JSONObject)jsonTemp.get("items");
            jsonaTemp = (JSONArray)jsonTemp.get("item");
            jsonTemp = (JSONObject)jsonaTemp.get(5);
            //t1hStatus = jsonTemp.getInt("obsrValue");
            t1hStatus = jsonTemp.getString("obsrValue");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            tvT1h.setText(t1hStatus + "℃");
                        }
                    });
                }
            }).start();
        } catch (Throwable t){
            Log.e("setWeatherT1hStatus", "Could not parse malformed JSON");
        }
    }

    private void setSkyImage(int ptyVal, int skyVal) {
        if(0 == ptyVal) {
            switch(skyVal) {
                case 1 :
                    /////////////////////////////////////////////
                    // http://itmining.tistory.com/6
                    // http://codetravel.tistory.com/12
                    // http://nocomet.tistory.com/10
                    ////////////////////////////////////////////
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ivSky.setImageResource(R.drawable.sun);
                                }
                            });
                        }
                    }).start();
                    break;
                case 2 :
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ivSky.setImageResource(R.drawable.cloud1);
                                }
                            });
                        }
                    }).start();
                    break;
                case 3 :
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ivSky.setImageResource(R.drawable.cloud2);
                                }
                            });
                        }
                    }).start();
                    break;
                case 4 :
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ivSky.setImageResource(R.drawable.heu);
                                }
                            });
                        }
                    }).start();
                    break;
                default :
                    break;
            }
        } else {
            switch(ptyVal) {
                case 1 :
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ivSky.setImageResource(R.drawable.rain);
                                }
                            });
                        }
                    }).start();
                    break;
                case 2 :
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ivSky.setImageResource(R.drawable.rain);
                                }
                            });
                        }
                    }).start();
                    break;
                case 3 :
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ivSky.setImageResource(R.drawable.snow);
                                }
                            });
                        }
                    }).start();
                    break;
                default :
                    break;
            }
        }
    }

    private void setWeatherSkyImage() {
        JSONObject jsonTemp = null;
        JSONArray jsonaTemp = null;

        int ptyStatus = 0;
        int skyStatus = 0;

        try {
            jsonTemp = (JSONObject)result.get("response");
            jsonTemp = (JSONObject)jsonTemp.get("body");

            // {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},"body":{"items":"","numOfRows":10,"pageNo":1,"totalCount":0}}} 일 경우
            if(0 == jsonTemp.getInt("totalCount")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ivSky.setImageResource(R.mipmap.ic_launcher);
                            }
                        });
                    }
                }).start();
                Log.e("setWeatherSkyImage", "JSON totalCount 0");
                return;
            }

            jsonTemp = (JSONObject)jsonTemp.get("items");
            jsonaTemp = (JSONArray)jsonTemp.get("item");
            jsonTemp = (JSONObject)jsonaTemp.get(1);
            ptyStatus = jsonTemp.getInt("obsrValue");

            jsonTemp = (JSONObject)jsonaTemp.get(4);
            skyStatus = jsonTemp.getInt("obsrValue");

            setSkyImage(ptyStatus, skyStatus);
        } catch (Throwable t){
            Log.e("setWeatherSkyImage", "Could not parse malformed JSON");
        }
    }
}
