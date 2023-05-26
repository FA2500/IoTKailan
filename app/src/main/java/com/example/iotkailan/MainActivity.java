package com.example.iotkailan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //UI
    private LineChart rainChart;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://iot-hkkailan-default-rtdb.asia-southeast1.firebasedatabase.app");

    //data
    private List<Entry> rainEntries = new ArrayList<Entry>();
    private List<Entry> rainEntries2 = new ArrayList<Entry>();

    private int humiC1 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initData();
        getDbData();
    }

    private void initUI()
    {
        rainChart=findViewById(R.id.rainChart);
        rainChart.setDrawGridBackground(false);
        rainChart.setNoDataText("Collection rain data");
    }

    private void initData()
    {
        /*for(int i = 0 ; i < 12 ; i++)
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
        rainChart.invalidate();*/
    }

    //soil = cubic line
    //humidity = COmbined chart (bar and line)
    //tempb=
    //rain sensor = linechart(sine/cosine)

    public void getDbData()
    {
        database.getReference("sensor/pico/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PicoData value = snapshot.getValue(PicoData.class);
                Log.d("MA", "Value is: " + value.getMoisture());
                double b = Double.parseDouble(value.getMoisture());
                int a = (int) b;
                rainEntries.add(new Entry(humiC1,a));
                humiC1++;
                LineDataSet rainDataSet = new LineDataSet(rainEntries,"Rain 1");
                rainDataSet.setColor(Color.BLUE);
                rainDataSet.setValueTextColor(Color.BLACK);

                LineData lineRainData = new LineData(rainDataSet);
                rainChart.setData(lineRainData);
                rainChart.invalidate();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

