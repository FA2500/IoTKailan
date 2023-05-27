package com.example.iotkailan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

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
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //UI
    private LineChart rainChart;
    private RadioButton rb;

    private Button rain1B;
    private Button rain2B;
    private Button humi1B;
    private Button humi2B;
    private Button temp1B;
    private Button temp2B;
    private Button moistB;

    private Button clearData;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://iot-hkkailan-default-rtdb.asia-southeast1.firebasedatabase.app");

    //data
    private List<Entry> rainEntries = new ArrayList<Entry>();
    private List<Entry> rainEntries2 = new ArrayList<Entry>();
    private List<Entry> humiEntries = new ArrayList<Entry>();
    private List<Entry> humiEntries2 = new ArrayList<Entry>();
    private List<Entry> tempEntries = new ArrayList<Entry>();
    private List<Entry> tempEntries2 = new ArrayList<Entry>();
    private List<Entry> moistEntries = new ArrayList<Entry>();

    //Handler
    private Handler handler;
    private boolean dataReceived = false;
    private boolean isFirstTime = true;

    private final Boolean[] elementCheck = {false, false, false, false, false, false, false};
    private int counter = 0;

    /**
     * 0 = rain1
     * 1 = rain2
     * 2 = humi1
     * 3 = humi2
     * 4 = temp1
     * 5 = temp2
     * 6 = moist
     **/


    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initBtn();
        initData();
        getDbData();

        handler = new Handler();
        startTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("sensor/pico/");
        databaseRef.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("sensor/pico/");
        databaseRef.removeEventListener(valueEventListener);
    }

    private void initBtn()
    {
        rain1B = findViewById(R.id.rain1Btn);
        rain2B = findViewById(R.id.rain2Btn);
        humi1B = findViewById(R.id.humi1Btn);
        humi2B = findViewById(R.id.humi2Btn);
        temp1B = findViewById(R.id.temp1Btn);
        temp2B = findViewById(R.id.temp2Btn);
        moistB = findViewById(R.id.moistBtn);
        clearData = findViewById(R.id.removeDataBtn);

        rain1B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button a = new Button(MainActivity.this);
                if(!elementCheck[0])
                {
                    elementCheck[0] = true;
                }
                else
                {
                    a.setText("OFF");
                    elementCheck[0] = false;
                }
            }
        });

        rain2B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button a = new Button(MainActivity.this);
                if(!elementCheck[1])
                {
                    elementCheck[1] = true;
                }
                else
                {
                    a.setText("OFF");
                    elementCheck[1] = false;
                }
            }
        });

        humi1B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button a = new Button(MainActivity.this);
                if(!elementCheck[2])
                {
                    elementCheck[2] = true;
                }
                else
                {
                    a.setText("OFF");
                    elementCheck[2] = false;
                }
            }
        });

        humi2B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button a = new Button(MainActivity.this);
                if(!elementCheck[3])
                {
                    elementCheck[3] = true;
                }
                else
                {
                    a.setText("OFF");
                    elementCheck[3] = false;
                }
            }
        });

        temp1B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button a = new Button(MainActivity.this);
                if(!elementCheck[4])
                {
                    elementCheck[4] = true;
                }
                else
                {
                    a.setText("OFF");
                    elementCheck[4] = false;
                }
            }
        });

        temp2B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button a = new Button(MainActivity.this);
                if(!elementCheck[5])
                {
                    elementCheck[5] = true;
                }
                else
                {
                    a.setText("OFF");
                    elementCheck[5] = false;
                }
            }
        });

        moistB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button a = new Button(MainActivity.this);
                if(!elementCheck[6])
                {
                    elementCheck[6] = true;
                }
                else
                {
                    a.setText("OFF");
                    elementCheck[6] = false;
                }
            }
        });

        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rainEntries.clear();
                rainEntries2.clear();
                humiEntries.clear();
                humiEntries2.clear();
                tempEntries.clear();
                tempEntries2.clear();
                moistEntries.clear();
                rainChart.clear();
                rainChart.invalidate();
                counter = 0;
            }
        });
    }

    private void initUI()
    {
        //init value

        //init UI
        rainChart=findViewById(R.id.rainChart);
        rainChart.setDrawGridBackground(false);
        rainChart.setNoDataText("Collection rain data");
        rb = findViewById(R.id.radioIndicator);
        rb.setChecked(true);
        isOnline(false);
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
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PicoData value = snapshot.getValue(PicoData.class);
                LineData lineRainData = new LineData();
                if(isFirstTime)
                {
                    isFirstTime = false;
                    int color = Color.parseColor("#ff4f00"); // Red color
                    ColorStateList colorStateList = ColorStateList.valueOf(color);
                    rb.setButtonTintList(colorStateList);
                    rb.setText("INITIALIZING");
                }
                else
                {
                    dataReceived = true;
                    isOnline(dataReceived);
                    resetTimer();
                }
                for(int i = 0 ; i < elementCheck.length ; i++)
                {
                    if(elementCheck[i])
                    {
                        if(i == 0)
                        {
                            double b = Double.parseDouble(value.getWater1());
                            int a = (int) b;
                            rainEntries.add(new Entry(counter,a));

                            LineDataSet rainDataSet = new LineDataSet(rainEntries,"Rain 1");
                            rainDataSet.setColor(Color.GRAY);
                            rainDataSet.setCircleColor(Color.GRAY);
                            rainDataSet.setValueTextColor(Color.BLACK);
                            lineRainData.addDataSet(rainDataSet);

                            rain1B.setText(String.valueOf(a));
                        }
                        else if(i == 1)
                        {
                            double b = Double.parseDouble(value.getWater2());
                            int a = (int) b;
                            rainEntries2.add(new Entry(counter,a));

                            LineDataSet rainDataSet = new LineDataSet(rainEntries2,"Rain 2");
                            rainDataSet.setColor(Color.RED);
                            rainDataSet.setCircleColor(Color.RED);
                            rainDataSet.setValueTextColor(Color.BLACK);
                            lineRainData.addDataSet(rainDataSet);

                            rain2B.setText(String.valueOf(a));
                        }
                        else if(i == 2)
                        {
                            double b = Double.parseDouble(value.getHumidity1());
                            int a = (int) b;
                            humiEntries.add(new Entry(counter,a));

                            LineDataSet rainDataSet = new LineDataSet(humiEntries,"Humi 1");
                            rainDataSet.setColor(Color.YELLOW);
                            rainDataSet.setCircleColor(Color.YELLOW);
                            rainDataSet.setValueTextColor(Color.BLACK);
                            lineRainData.addDataSet(rainDataSet);

                            humi1B.setText(String.valueOf(a));
                        }
                        else if(i == 3)
                        {
                            double b = Double.parseDouble(value.getHumidity2());
                            int a = (int) b;
                            humiEntries2.add(new Entry(counter,a));

                            LineDataSet rainDataSet = new LineDataSet(humiEntries2,"Humi 2");
                            rainDataSet.setColor(Color.GREEN);
                            rainDataSet.setCircleColor(Color.GREEN);
                            rainDataSet.setValueTextColor(Color.BLACK);
                            lineRainData.addDataSet(rainDataSet);

                            humi2B.setText(String.valueOf(a));
                        }
                        else if(i == 4)
                        {
                            double b = Double.parseDouble(value.getTemperature1());
                            int a = (int) b;
                            tempEntries.add(new Entry(counter,a));

                            LineDataSet rainDataSet = new LineDataSet(tempEntries,"Temp 1");
                            rainDataSet.setColor(Color.BLUE);
                            rainDataSet.setCircleColor(Color.BLUE);
                            rainDataSet.setValueTextColor(Color.BLACK);
                            lineRainData.addDataSet(rainDataSet);

                            temp1B.setText(String.valueOf(a));
                        }
                        else if(i == 5)
                        {
                            double b = Double.parseDouble(value.getTemperature2());
                            int a = (int) b;
                            tempEntries2.add(new Entry(counter,a));

                            LineDataSet rainDataSet = new LineDataSet(tempEntries2,"Temp 2");
                            rainDataSet.setColor(Color.MAGENTA);
                            rainDataSet.setCircleColor(Color.MAGENTA);
                            rainDataSet.setValueTextColor(Color.BLACK);
                            lineRainData.addDataSet(rainDataSet);

                            temp2B.setText(String.valueOf(a));
                        }
                        else if(i == 6)
                        {
                            double b = Double.parseDouble(value.getMoisture());
                            int a = (int) b;
                            moistEntries.add(new Entry(counter,a));

                            LineDataSet rainDataSet = new LineDataSet(moistEntries,"Moist");
                            rainDataSet.setColor(Color.BLACK);
                            rainDataSet.setCircleColor(Color.BLACK);
                            rainDataSet.setValueTextColor(Color.BLACK);
                            lineRainData.addDataSet(rainDataSet);

                            moistB.setText(String.valueOf(a));
                        }
                    }
                }

                rainChart.setData(lineRainData);
                rainChart.invalidate();
                counter++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        //database.getReference("sensor/pico/").addValueEventListener(valueEventListener);
    }

    private void startTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!dataReceived) {
                    // Display toast indicating no new data received
                    isOnline(dataReceived);
                }
                dataReceived = false; // Reset the dataReceived flag
                handler.postDelayed(this, 30000); // Run the handler again after 30 seconds
            }
        }, 30000); // Run the handler for the first time after 30 seconds
    }

    private void resetTimer() {
        handler.removeCallbacksAndMessages(null); // Remove any pending callbacks
        startTimer();
    }

    private void isOnline(Boolean state)
    {
        if(state)
        {
            int color = Color.parseColor("#03c03c"); // Red color
            ColorStateList colorStateList = ColorStateList.valueOf(color);
            rb.setButtonTintList(colorStateList);
            rb.setText("ONLINE");
        }
        else
        {
            int color = Color.parseColor("#ff0800"); // Red color
            ColorStateList colorStateList = ColorStateList.valueOf(color);
            rb.setButtonTintList(colorStateList);
            rb.setText("OFFLINE");
        }
    }
}

