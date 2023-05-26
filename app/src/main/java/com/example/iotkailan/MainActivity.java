package com.example.iotkailan;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //UI
    private LineChart rainChart;


    //data
    private List<Entry> rainEntries = new ArrayList<Entry>();
    private List<Entry> rainEntries2 = new ArrayList<Entry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initData();
    }

    private void initUI()
    {
        rainChart=findViewById(R.id.rainChart);
        rainChart.setDrawGridBackground(false);
        rainChart.setNoDataText("Collection rain data");
    }

    private void initData()
    {
        for(int i = 0 ; i < 12 ; i++)
        {
            rainEntries.add(new Entry(i,i));
        }
        for(int i = 0 ; i < 12 ; i++)
        {
            rainEntries2.add(new Entry(i,12-i));
        }
        LineDataSet rainDataSet = new LineDataSet(rainEntries,"Rain 1");
        rainDataSet.setColor(Color.BLUE);
        rainDataSet.setValueTextColor(Color.BLACK);

        LineDataSet rainDataSet2 = new LineDataSet(rainEntries2,"Rain 2");
        rainDataSet2.setColor(Color.RED);
        rainDataSet2.setValueTextColor(Color.BLACK);

        LineData lineRainData = new LineData(rainDataSet);
        lineRainData.addDataSet(rainDataSet2);
        rainChart.setData(lineRainData);
        rainChart.invalidate();
    }

    //soil = cubic line
    //humidity = COmbined chart (bar and line)
    //tempb=
    //rain sensor = linechart(sine/cosine)
}