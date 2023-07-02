package com.example.iotkailan;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.Manifest;

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

    //controller
    private Button fanBtn,servoBtn,pump1Btn,pump2Btn;

    private Button waterlvlB;

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

    private List<Entry> levelEntries = new ArrayList<Entry>();

    //Handler
    private Handler handler;
    private boolean dataReceived = false;
    private boolean isFirstTime = true;

    private final Boolean[] elementCheck = {false, false, false, false, false, false, false, false};
    private int counter = 0;

    /**
     * 0 = rain1
     * 1 = rain2
     * 2 = humi1
     * 3 = humi2
     * 4 = temp1
     * 5 = temp2
     * 6 = moist
     * 7 = level
     **/


    private ValueEventListener valueEventListener;
    private ValueEventListener valueEventListener2;

    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance("asia-southeast1");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initFCM();
        initBtn();
        initData();
        getDbData();

        handler = new Handler();
        startTimer();
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void initFCM()
    {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } /*else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            }*/ else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = "Fcm notifications";
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TEST", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        //String msg = getString(R.string.app_name, token);
                        Log.d("TEST", token);
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("sensor/pico/");
        databaseRef.addValueEventListener(valueEventListener);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("control");
        databaseReference.addValueEventListener(valueEventListener2);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("sensor/pico/");
        databaseRef.removeEventListener(valueEventListener);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("control");
        databaseReference.removeEventListener(valueEventListener2);
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
        waterlvlB = findViewById(R.id.waterlvlBtn);
        clearData = findViewById(R.id.removeDataBtn);

        //cont
        fanBtn = findViewById(R.id.fanBtn);
        servoBtn = findViewById(R.id.servoBtn);
        pump1Btn = findViewById(R.id.pump1Btn);
        pump2Btn = findViewById(R.id.pump2Btn);

        rain1B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!elementCheck[0])
                {
                    rain1B.setText("On");
                    elementCheck[0] = true;
                }
                else
                {
                    rain1B.setText("OFF");
                    elementCheck[0] = false;
                }
            }
        });

        rain2B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!elementCheck[1])
                {
                    rain2B.setText("On");
                    elementCheck[1] = true;
                }
                else
                {
                    rain2B.setText("OFF");
                    elementCheck[1] = false;
                }
            }
        });

        humi1B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!elementCheck[2])
                {
                    humi1B.setText("On");
                    elementCheck[2] = true;
                }
                else
                {
                    humi1B.setText("OFF");
                    elementCheck[2] = false;
                }
            }
        });

        humi2B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!elementCheck[3])
                {
                    humi2B.setText("On");
                    elementCheck[3] = true;
                }
                else
                {
                    humi2B.setText("OFF");
                    elementCheck[3] = false;
                }
            }
        });

        temp1B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!elementCheck[4])
                {
                    temp1B.setText("On");
                    elementCheck[4] = true;
                }
                else
                {
                    temp1B.setText("OFF");
                    elementCheck[4] = false;
                }
            }
        });

        temp2B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!elementCheck[5])
                {
                    temp2B.setText("On");
                    elementCheck[5] = true;
                }
                else
                {
                    temp2B.setText("OFF");
                    elementCheck[5] = false;
                }
            }
        });

        moistB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!elementCheck[6])
                {
                    moistB.setText("On");
                    elementCheck[6] = true;
                }
                else
                {
                    moistB.setText("OFF");
                    elementCheck[6] = false;
                }
            }
        });

        waterlvlB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!elementCheck[7])
                {
                    waterlvlB.setText("On");
                    elementCheck[7] = true;
                }
                else
                {
                    waterlvlB.setText("OFF");
                    elementCheck[7] = false;
                }
            }
        });

        fanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ffunc("fanplant1android");
            }
        });

        /*servoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFunctions
                        .getHttpsCallable("servoOpen")
                        .call()
                        .continueWith(new Continuation<HttpsCallableResult, String>()
                        {
                            @Override
                            public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                                // This continuation runs on either success or failure, but if the task
                                // has failed then getResult() will throw an Exception which will be
                                // propagated down.
                                Toast.makeText(MainActivity.this, "Servo have been turned, wait for telegram message to appear to confirm your action.", Toast.LENGTH_SHORT).show();
                                return "200";
                            }
                        });

            }
        });*/

        pump1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ffunc("waterplant1android");
            }
        });

        pump2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ffunc("waterplant2android");
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

    private Task<String> ffunc(String text) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);

        return mFunctions
                .getHttpsCallable(text)
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        Log.d("TEST","res = "+task.getResult().toString());
                        Toast.makeText(MainActivity.this, text+" is turned on, wait for telegram message to appear to confirm your action.", Toast.LENGTH_SHORT).show();
                        String result = (String) task.getResult().getData();
                        return result;
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
        valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PicoController value = snapshot.getValue(PicoController.class);
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

