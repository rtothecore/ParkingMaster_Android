package kr.co.ezinfotech.parkingmaster;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends ActivityBase implements OnChartGestureListener {

    LocationManager mLocMan = null;
    String mProvider = null;
    boolean isPermissionGranted = false;

    double myLat = 0;
    double myLon = 0;

    private Geocoder mCoder;

    private PieChart mChart;
    private BarChart mChart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_scrolling);

        mCoder = new Geocoder(this);

        runPermissionListener(this);

        drawPieChart();
        drawBarChart();
    }

    private void drawBarChart() {
        mChart2 = (BarChart) findViewById(R.id.st_bar_chart1);

        mChart2.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart2.setMaxVisibleValueCount(10);

        // scaling can now only be done on x- and y-axis separately
        mChart2.setPinchZoom(false);

        mChart2.setDrawGridBackground(false);
        mChart2.setDrawBarShadow(false);

        mChart2.setDrawValueAboveBar(false);
        mChart2.setHighlightFullBarEnabled(false);

        // change the position of the y-labels
        YAxis leftAxis = mChart2.getAxisLeft();
        leftAxis.setValueFormatter(new MyAxisValueFormatter());
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        mChart2.getAxisRight().setEnabled(false);

        XAxis xLabels = mChart2.getXAxis();
        xLabels.setGranularity(1f);
        xLabels.setCenterAxisLabels(true);
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);
        String xLabelsStr[] = {"제주대학교", "제주공항", "벤처마루", "제주시청", "", ""};
        xLabels.setValueFormatter(new MyAxisValueFormatter2(xLabelsStr));

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        yVals1.add(new BarEntry(1, new float[]{20, 80}, getResources().getDrawable(R.drawable.star)));
        yVals1.add(new BarEntry(2, new float[]{30, 70}, getResources().getDrawable(R.drawable.star)));
        yVals1.add(new BarEntry(3, new float[]{15, 85}, getResources().getDrawable(R.drawable.star)));
        yVals1.add(new BarEntry(4, new float[]{34, 66}, getResources().getDrawable(R.drawable.star)));

        BarDataSet set1 = new BarDataSet(yVals1, "주차 점유대수");
        set1.setDrawIcons(false);
        set1.setColors(getColors());
        set1.setStackLabels(new String[]{"주차점유", "주차면수"});

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueFormatter(new MyValueFormatter());
        data.setValueTextColor(Color.WHITE);

        mChart2.setData(data);
        mChart2.setFitBars(true);
        mChart2.invalidate();

        Legend l2 = mChart2.getLegend();
        l2.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l2.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l2.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l2.setDrawInside(false);
        l2.setFormSize(8f);
        l2.setFormToTextSpace(4f);
        l2.setXEntrySpace(6f);
    }

    private void drawPieChart() {
        mChart = (PieChart) findViewById(R.id.pie_chart1);

        ////////////////////////////////////////////////////////////////////// For click event
        mChart.setOnChartGestureListener(this);
        mChart.setTouchEnabled(true);
        //////////////////////////////////////////////////////////////////////

        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setCenterTextTypeface(mTfLight);
        mChart.setCenterText(generateCenterSpannableText("40"));

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.parseColor("#7c88d2"));

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        //mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // add a selection listener
        //mChart.setOnChartValueSelectedListener(this);

        setData(2, 100);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        //mSeekBarX.setOnSeekBarChangeListener(this);
        //mSeekBarY.setOnSeekBarChangeListener(this);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTypeface(mTfRegular);
        mChart.setEntryLabelTextSize(12f);
    }

    // Using TedPermission library - https://github.com/ParkSangGwon/TedPermission
    private void runPermissionListener(Context ctx) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(ScrollingActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                InitializeLocationService();
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(ScrollingActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(ctx)
                .setPermissionListener(permissionlistener)
                .setRationaleTitle("Rational Title")
                .setRationaleTitle(R.string.rationale_title)
                .setRationaleMessage(R.string.rationale_message)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("bla bla")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    }

    private void InitializeLocationService() {
        mLocMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mProvider = mLocMan.getBestProvider(new Criteria(), true);

        try {
            if(null == mProvider) {
                mLocMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, mListener);
                mLocMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1, mListener);
            } else {
                mLocMan.requestLocationUpdates(mProvider, 3000, 1, mListener);
            }

        } catch (SecurityException e) {
            Log.i("InitializeLoc", "requestLocationUpdates exception!");
        }
    }

    private void runWeatherDataManager() {
        WeatherDataManager wdm = new WeatherDataManager(myLat, myLon, (TextView) findViewById(R.id.wt_ac));
        wdm.setSkyTextView((TextView) findViewById(R.id.wt_title));
        wdm.setT1hTextView((TextView) findViewById(R.id.wt_tp));
        wdm.setSkyImageView((ImageView) findViewById(R.id.wt_pic));
        wdm.setWeatherData();
    }

    public void onResume() {
        super.onResume();

        if(isPermissionGranted) {
            InitializeLocationService();
        }
    }

    public void onPause() {
        super.onPause();

        if(isPermissionGranted && (null != mLocMan)) {
            mLocMan.removeUpdates(mListener);
        }
        Log.i("onPause", "Get my location END");
    }

    private void runReverseGeoCoding(double latVal, double lonVal) {
        try {
            List<Address> list = mCoder.getFromLocation(latVal, lonVal, 5);
            Log.i("Geocode", list.get(0).toString());
            TextView tb_addr = (TextView) findViewById(R.id.wt_loc);

            if (list != null) {
                if (list.size()==0) {
                    tb_addr.setText("---");
                } else {
                    String cut[] = list.get(0).toString().split(" ");
                    tb_addr.setText(cut[2] + " " + cut[3]); // 내가 원하는 구의 값을 뽑아내 출력
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Geocode", "getFromLocation IO exception");
        }
    }

    LocationListener mListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            myLat = location.getLatitude();
            myLon = location.getLongitude();
            Log.i("onLocationChanged", myLat + "/" + myLon);

            runReverseGeoCoding(myLat, myLon);
            runWeatherDataManager();
        }

        public void onProviderDisabled(String provider) {
            Log.i("onProviderDisabled", "Get my location service disabled");
        }

        public void onProviderEnabled(String provider) {
            Log.i("onProviderEnabled", "Get my location service Enabled");
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            String sStatus = "";
            switch(status) {
                case LocationProvider.OUT_OF_SERVICE :
                    sStatus = "범위 벗어남";
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE :
                    sStatus = "일시적 불능";
                    break;
                case LocationProvider.AVAILABLE :
                    sStatus = "사용 가능";
                    break;
            }
            Log.i("onStatusChanged", sStatus);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setData(int count, float range) {

        float mult = range;
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        /*
        for (int i = 0; i < count ; i++) {
            entries.add(new PieEntry((float) ((Math.random() * mult) + mult / 5),
                    mParties[i % mParties.length],
                    getResources().getDrawable(R.drawable.star)));
        }
        */
        entries.add(new PieEntry(40, "주차점유"));
        entries.add(new PieEntry(60, "주차가능"));

        PieDataSet dataSet = new PieDataSet(entries, "주차점유율");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(mTfLight);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private SpannableString generateCenterSpannableText(String cs_text) {
        /*
        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        */
        SpannableString s = new SpannableString(cs_text + "%");
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, 0);
        return s;
    }

    private int[] getColors() {
        int stacksize = 2;
        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = ColorTemplate.COLORFUL_COLORS[i];
        }
        return colors;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
        //Toast.makeText(getApplicationContext(), "Chart single-tapped.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }
}
