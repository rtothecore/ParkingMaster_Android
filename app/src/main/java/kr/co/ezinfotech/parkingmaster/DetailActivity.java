package kr.co.ezinfotech.parkingmaster;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

public class DetailActivity extends ActivityBase {

    private LineChart mChart;

    ParkingMasterDBHelper dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String pzNo = intent.getStringExtra("pzNo");

        dbHelper = new ParkingMasterDBHelper(this);
        setPZData(selectWithNo(pzNo));

        showLineChart();
    }

    private void setPZData(PZData pzdataVal) {
        ((TextView)findViewById(R.id.parkDetailTitle)).setText(pzdataVal.name);
        ((TextView)findViewById(R.id.parkDetailFee)).setText(pzdataVal.feeInfo + " : 기본 " + pzdataVal.park_base.time + "분 " + pzdataVal.park_base.fee +"원, 초과 " + pzdataVal.add_term.time + "분 " + pzdataVal.add_term.fee + "원");
        ((TextView)findViewById(R.id.parkDetailTotal)).setText("주차면수 : " + pzdataVal.totalP + "대");
    }

    private PZData selectWithNo(String noVal) {
        SQLiteDatabase db= dbHelper.getReadableDatabase();
        String sqlSelect = ParkingZoneDBCtrct.SQL_SELECT_WITH_NO + noVal + "'";
        Cursor cursor = db.rawQuery(sqlSelect, null);

        PZData tempPZData = new PZData();

        if(cursor.moveToFirst()) {
            tempPZData.no = cursor.getString(0);
            tempPZData.name = cursor.getString(1);
            tempPZData.addr = cursor.getString(2);
            tempPZData.tel = cursor.getString(3);
            tempPZData.loc = new Location("");
            tempPZData.loc.setLatitude(Double.parseDouble(cursor.getString(4)));
            tempPZData.loc.setLongitude(Double.parseDouble(cursor.getString(5)));
            tempPZData.totalP = cursor.getString(6);
            tempPZData.opDate = cursor.getString(7);
            tempPZData.w_op = new PZTermData();
            tempPZData.w_op.start_date = cursor.getString(8);
            tempPZData.w_op.end_date = cursor.getString(9);
            tempPZData.s_op = new PZTermData();
            tempPZData.s_op.start_date = cursor.getString(10);
            tempPZData.s_op.end_date = cursor.getString(11);
            tempPZData.h_op = new PZTermData();
            tempPZData.h_op.start_date = cursor.getString(12);
            tempPZData.h_op.end_date = cursor.getString(13);
            tempPZData.feeInfo = cursor.getString(14);
            tempPZData.park_base = new PZTFData();
            tempPZData.park_base.time = cursor.getString(15);
            tempPZData.park_base.fee = cursor.getString(16);
            tempPZData.add_term = new PZTFData();
            tempPZData.add_term.time = cursor.getString(17);
            tempPZData.add_term.fee = cursor.getString(18);
            tempPZData.remarks = cursor.getString(19);
        }
        return tempPZData;
    }

    private void showLineChart() {
        //////////////////// Line chart START ///////////////////////////////
        mChart = (LineChart) findViewById(R.id.linechart1);
        //mChart.setOnChartGestureListener(this);
        //mChart.setOnChartValueSelectedListener(this);
        //mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line


        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        LimitLine ll1 = new LimitLine(100f, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);

        LimitLine ll2 = new LimitLine(0f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setTypeface(tf);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        //leftAxis.addLimitLine(ll1);
        //leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        // add data
        setData(8, 100);

//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        mChart.animateX(1500);
        //mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);

        // // dont forget to refresh the drawing
        // mChart.invalidate();
        //////////////////// Line chart END /////////////////////////////////
    }

    private void setData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float val = (float) (Math.random() * range) + 3;
            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.star)));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "예측값");

            set1.setDrawIcons(false);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            // set1.setColor(Color.BLACK);
            set1.setColor(Color.rgb(255,169,34));
            // set1.setCircleColor(Color.BLACK);
            set1.setCircleColor(Color.rgb(255,169,34));
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }
    }
}
