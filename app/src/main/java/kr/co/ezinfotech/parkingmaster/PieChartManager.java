package kr.co.ezinfotech.parkingmaster;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

/**
 * Created by hkim on 2018-04-11.
 */

public class PieChartManager extends ActivityBase implements OnChartGestureListener {
    private PieChart mChart = null;
    private PisDataManager pdm = null;
    private String clickedNo = "";
    private Context ctx = null;
    LinearLayout ll = null;
    private String pkName = null;

    public PieChartManager(PisDataManager pdmVal, String clickedNoVal, Context ctxVal, LinearLayout llVal, String name) {
        pdm = pdmVal;
        clickedNo = clickedNoVal;
        ctx = ctxVal;
        ll = llVal;
        pkName = name;

        //createLayout();
    }

    public void removeLayout() {
        ll.removeAllViews();
    }

    public void createLayout() {
        //LinearLayout ll = (LinearLayout)findViewById(R.id.ll_parkinglock);

        RelativeLayout rl = new RelativeLayout(ctx);
        final int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, ctx.getResources().getDisplayMetrics());
        final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, ctx.getResources().getDisplayMetrics());

        //RelativeLayout.LayoutParams rlLP = new RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams rlLP = new RelativeLayout.LayoutParams(width, height);
        rlLP.setMargins(5, 5, 5, 5);
        String strColor = "#64BEF7";
        rl.setBackgroundColor(Color.parseColor(strColor));
        rl.setPadding(5, 5, 5, 5);
        rl.setLayoutParams(rlLP);

        /////////////////////////// Added for background /////////////////////////////////
        /*
        <ImageView
                        android:id="@+id/AirBg"
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:adjustViewBounds="true"
                        android:maxHeight="0dp"
                        android:scaleType="fitXY"
                        android:src="@drawable/airinfo_bg" />
         */
        ImageView bgImage = new ImageView(ctx);
        RelativeLayout.LayoutParams bgImageParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bgImage.setLayoutParams(bgImageParams);
        bgImage.setAdjustViewBounds(true);
        bgImage.setMaxHeight(0);
        bgImage.setScaleType(ImageView.ScaleType.FIT_XY);
        bgImage.setImageDrawable(ctx.getResources().getDrawable(R.drawable.placeinfo_bg));

        rl.addView(bgImage);
        /////////////////////////// Added for background /////////////////////////////////

        TextView tvTitle = new TextView(ctx);
        RelativeLayout.LayoutParams tvTitleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvTitleParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        tvTitle.setText(pkName);
        String textColor = "#FFFFFF";
        tvTitle.setTextColor(Color.parseColor(textColor));
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setLayoutParams(tvTitleParams);
        tvTitle.setId(R.id.d_pks_title);

        rl.addView(tvTitle);

        PieChart pieChart = new PieChart(ctx);
        RelativeLayout.LayoutParams pieChartParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        pieChartParams.addRule(RelativeLayout.BELOW, tvTitle.getId());
        pieChart.setLayoutParams(pieChartParams);
        pieChart.setId(R.id.d_pie_chart);

        rl.addView(pieChart);

        ll.addView(rl);

        mChart = pieChart;
    }

    public void draw() {
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
        mChart.setHoleColor(Color.parseColor("#E6E6E6"));

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

        // add curstom color
        final int[] pieColors = {
                Color.rgb(9,127,218),
                Color.rgb(255,168,32)
        };

        ArrayList<Integer> colors = new ArrayList<Integer>();
        /* ORIGINAL
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        */
        for (int color : pieColors) {
            colors.add(color);
        }

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
        //initPdmForJustOne();

        pdm.setMode(2);
        pdm.setContext(ctx);
        pdm.setClickedPZNo(clickedNo);
        pdm.setPisData();

        /*
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
        */
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
