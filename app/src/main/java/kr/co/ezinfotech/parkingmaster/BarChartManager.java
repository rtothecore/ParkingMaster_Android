package kr.co.ezinfotech.parkingmaster;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by hkim on 2018-04-11.
 */

public class BarChartManager {
    private BarChart mChart;
    private Context ctx;
    private ArrayList<String> labelStrList = new ArrayList<>();

    public BarChartManager(BarChart bcVal, Context ctxVal) {
        mChart = bcVal;
        ctx = ctxVal;
    }

    public void setLabelStr(String label) {
        labelStrList.add(label);
    }

    public void clearLabelStr() {
        labelStrList.clear();
    }

    public void draw() {
        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(10);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);

        mChart.setDrawValueAboveBar(false);
        mChart.setHighlightFullBarEnabled(false);

        // change the position of the y-labels
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setValueFormatter(new MyAxisValueFormatter());
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        mChart.getAxisRight().setEnabled(false);

        XAxis xLabels = mChart.getXAxis();
        xLabels.setGranularity(1f);
        xLabels.setCenterAxisLabels(true);
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);
        /////////////////////////////////////////////////////
        Object[] xLabelObj = labelStrList.toArray();
        String[] xLabelsStr = new String[xLabelObj.length + 2];
        for(int i = 0; i < xLabelObj.length; i++) {
            xLabelsStr[i] = (String)xLabelObj[i];
            if((xLabelObj.length - 1) == i) {
                xLabelsStr[i+1] = "";
                xLabelsStr[i+2] = "";
            }
        }
        /////////////////////////////////////////////////////
        //String xLabelsStr[] = {"제주대학교", "제주공항", "벤처마루", "제주시청", "", ""};
        xLabels.setValueFormatter(new MyAxisValueFormatter2(xLabelsStr));

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        for(int i = 0; i < xLabelObj.length; i++) {
            yVals1.add(new BarEntry(i+1, new float[]{20, 80}, ctx.getResources().getDrawable(R.drawable.star)));
        }
        /*
        yVals1.add(new BarEntry(1, new float[]{20, 80}, ctx.getResources().getDrawable(R.drawable.star)));
        yVals1.add(new BarEntry(2, new float[]{30, 70}, ctx.getResources().getDrawable(R.drawable.star)));
        yVals1.add(new BarEntry(3, new float[]{15, 85}, ctx.getResources().getDrawable(R.drawable.star)));
        yVals1.add(new BarEntry(4, new float[]{34, 66}, ctx.getResources().getDrawable(R.drawable.star)));
        */

        BarDataSet set1 = new BarDataSet(yVals1, "주차 점유대수");
        set1.setDrawIcons(false);
        set1.setColors(getColors());
        set1.setStackLabels(new String[]{"주차점유", "주차면수"});

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueFormatter(new MyValueFormatter());
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mChart.setFitBars(true);
        mChart.invalidate();

        Legend l2 = mChart.getLegend();
        l2.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l2.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l2.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l2.setDrawInside(false);
        l2.setFormSize(8f);
        l2.setFormToTextSpace(4f);
        l2.setXEntrySpace(6f);
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
}
