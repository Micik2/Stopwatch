package com.example.maciek.stopwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.math.BigDecimal;
import android.icu.text.DateFormat;
import android.icu.text.StringPrepParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.math.RoundingMode.*;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {
    Handler handler = new Handler();
    Thread thread;
    /*Runnable runnable = new Runnable() {
        public void run() {
            timer();
        }
    };*/
    private LinearLayout mainLayout;
    private Button start;
    private Button newLap;
    private Button stopClear;
    private TextView laps;
    private TextView time;
    private LinearLayout layout;
    private long timeStart;
    private Boolean go;
    private int lap = 1;
    private double elapsedSeconds = 0;
    private int elapsedMinutes = 0;
    private Toolbar toolbar;
    private Button save;
    private SharedPreferences sharedPreferences;
    private TextView sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        //layout = (LinearLayout) findViewById(R.id.layout);

        //chronometer = new Chronometer(getApplicationContext());
        //mainLayout.addView(chronometer);

        start = (Button) findViewById(R.id.start);
        newLap = (Button) findViewById(R.id.new_lap);
        stopClear = (Button) findViewById(R.id.stop_clear);
        time = (TextView) findViewById(R.id.time);
        laps = (TextView) findViewById(R.id.laps);
        save = (Button) findViewById(R.id.save);
        sp = (TextView) findViewById(R.id.sp);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //sharedPreferences = this.getSharedPreferences("com.example.maciek", getApplicationContext().MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("stan", MODE_PRIVATE);
        String stan = sharedPreferences.getString("stan", "BRAK");
        sp.setText(stan);
        sharedPreferences = getSharedPreferences("czas", MODE_PRIVATE);
        final float czas = sharedPreferences.getFloat("czas", (float) 0.0);

        if (stan.equals("włączony"))
            thread.start();
            /*start.post(new Runnable(){
                @Override
                public void run() {
                    thread.start();
                    //start.performClick();
                }
            });}*/
            //start.performClick(); }
        /*else if (stan.equals("wyłączony")) {
                stopClear.post(new Runnable(){
                    @Override
                    public void run() {
                        stopClear.performClick();
                    }
                });}*/

        //setContentView(mainLayout);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeStart = System.currentTimeMillis();
                editor = sharedPreferences.edit();
                editor.putString("stan", "włączony");
                editor.putFloat("czas", timeStart);
                editor.commit();
                //final double startTime = System.currentTimeMillis();
                //long timeDelta = actualTime - timeStart;
                //double elapsedSeconds = timeDelta / 1000.0;
                go = true;


                thread = new Thread() {
                    public void run() {
                        while (go) {
                            try {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        final double actualTime = System.currentTimeMillis();
                                        sharedPreferences = getSharedPreferences("czas", MODE_PRIVATE);
                                        double czas = sharedPreferences.getFloat("czas", (float) 0.0);
                                        if (czas != 0.0)
                                            timeStart = (long) czas;
                                        long timeDelta = (long) (actualTime - timeStart);
                                        elapsedSeconds = timeDelta / 1000.0;
                                        //if (elapsedSeconds >= 60) {
                                        if (elapsedSeconds >= 60) {
                                            //elapsedMinutes = (int) (elapsedSeconds / 60);
                                            elapsedMinutes = (int) (elapsedSeconds / 60);
                                            //elapsedSeconds = elapsedSeconds - 60 * elapsedMinutes;
                                            elapsedSeconds = elapsedSeconds - (elapsedMinutes * 60);
                                            //elapsedSeconds = (int) (elapsedSeconds * 60);
                                            /*elapsedSeconds = elapsedSeconds.multiply(new BigDecimal("60"));*/
                                            //int iElapsedSeconds = (int) elapsedSeconds;
                                            //DecimalFormat df = new DecimalFormat("###");
                                            //String dx = df.format(elapsedSeconds);
                                            //elapsedSeconds = (int) (Double.valueOf(dx) * 100);
                                            DecimalFormat df = new DecimalFormat("##.##");
                                            String dx = df.format(elapsedSeconds);
                                            elapsedSeconds = Double.valueOf(dx);
                                            if (elapsedSeconds < 10)
                                                time.setText(String.valueOf(elapsedMinutes) + ":0" + String.valueOf(elapsedSeconds));
                                            else
                                                time.setText(String.valueOf(elapsedMinutes) + ":" + String.valueOf(elapsedSeconds));
                                        }
                                        else {
                                            DecimalFormat df = new DecimalFormat("#.###");
                                            String dx = df.format(elapsedSeconds);
                                            elapsedSeconds = Double.valueOf(dx);
                                            //elapsedSeconds = elapsedSeconds.setScale(2);
                                            time.setText(String.valueOf(elapsedSeconds));
                                        }
                                    }
                                });
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                thread.start();

                //time.setText(String.valueOf(startTime));
            }
        });

        stopClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopClear.getText().equals("STOP")) {
                    if (thread != null && thread.isAlive())
                        thread.interrupt();
                    go = false;
                    stopClear.setText("CLEAR");
                }
                else if (stopClear.getText().equals("CLEAR")) {
                    if (thread != null && thread.isAlive())
                        thread.interrupt();
                    time.setText("0.00");
                    stopClear.setText("STOP");
                    laps.setText("");
                    lap = 1;
                }
                editor = sharedPreferences.edit();
                editor.putString("stan", "wyłączony");
                editor.commit();
                /*if (stopClear.getText().equals("STOP")) {
                    chronometer.stop();
                    go = false;
                    stopClear.setText("CLEAR");
                }
                else if (stopClear.getText().equals("CLEAR")) {
                    chronometer.stop();
                    chronometer.setBase(0);
                    time.setText("0.00");
                    stopClear.setText("STOP");
                }*/
            }
        });

        newLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elapsedSeconds < 10)
                    laps.append(lap + ") " + String.valueOf(elapsedMinutes) + ":0" + String.valueOf(elapsedSeconds)+ "\n");
                else
                    laps.append(lap + ") " + String.valueOf(elapsedMinutes) + ":" + String.valueOf(elapsedSeconds)+ "\n");
                lap += 1;
                /*for (; lap < 20; lap++) {
                    laps.append(String.valueOf(elapsedSeconds));
                    //laps.setText(String.valueOf(elapsedSeconds) + "\n");
                }*/
                //double actualTime = chronometer.getBase();
                //laps.setText(String.valueOf(actualTime));
                //getLayoutInflater().inflate(R.layout.layout, mainLayout);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sharedPreferences = getApplicationContext().getSharedPreferences("com.example.maciek", getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (thread.isAlive())
                    editor.putString("stan", "włączony");
                else
                    editor.putString("stan", "wyłączony");
                editor.commit();
                //    sharedPreferences.edit().putString("com.example.maciek.stan", "włączony");
                //else
                //    sharedPreferences.edit().putString("com.example.maciek.stan", "wyłączony");
                String s = sharedPreferences.getString("stan", "BRAK");
                sp.setText(s);
                //System.out.println(s);
                //String s = sharedPreferences.getString("com.example.maciek.stan", "BRAK");
                //sp.setText(s);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                String url = "http://google.pl";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }









        //layout = (LinearLayout) findViewById(R.id.layout);

        /*thread = new Thread(){
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        timer();
                    }
                });
            }
        };*/

        /*start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread.start();
                //new Thread(runnable).start();
                //Thread thread = new Thread(runnable);
                //thread.start();
                //runnable.run();
                //long startTime = System.currentTimeMillis();
            }
        });*/

        /*handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                time.setText(msg.obj.toString());
            }

        };*/


        //new Thread(runnable).start();

    //}

    private void timer() {
        timeStart = System.currentTimeMillis();
        time.setText("0.00");
        go = true;

        while(go) {
            long actualTime = System.currentTimeMillis();
            long timeDelta = actualTime - timeStart;
            double elapsedSeconds = timeDelta / 1000.0;
            //double elapsedTime = elapsedSeconds
            Message message = new Message();
            message.obj = elapsedSeconds;
            handler.sendMessage(message);
            //time.setText(String.valueOf(elapsedSeconds));
        }

        newLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                laps = (TextView) findViewById(R.id.laps);
                long actualTime = System.currentTimeMillis();
                long timeDelta = actualTime - timeStart;
                double elapsedSeconds = timeDelta / 1000.0;
                //double elapsedTime = elapsedSeconds
                laps.setText(String.valueOf(elapsedSeconds));
                getLayoutInflater().inflate(R.layout.layout, mainLayout);

                //inflateView = View.inflate(this,R.layout.view_video,parentLayout);
                //layout.addView(createNewTextView(mEditText.getText().toString()));
                //inflateView=View.inflate(this,R.layout.view_video,parentLayout);
                //long startTime = System.currentTimeMillis();
            }
        });

        stopClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopClear.getText().equals("STOP")) {
                    go = false;
                    stopClear.setText("CLEAR");
                }
                else if (stopClear.getText().equals("CLEAR")) {
                    time.setText("0.00");
                    stopClear.setText("STOP");
                    thread.interrupt();
                    //ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();
                    //Future longRunningTaskFuture = threadPoolExecutor.submit(runnable);
                    //longRunningTaskFuture.cancel(true);
                }

                /*laps = (TextView) findViewById(R.id.laps);
                long actualTime = System.currentTimeMillis();
                long timeDelta = actualTime - timeStart;
                double elapsedSeconds = timeDelta / 1000.0;
                //double elapsedTime = elapsedSeconds
                laps.setText(String.valueOf(elapsedSeconds));
                getLayoutInflater().inflate(R.layout.layout, mainLayout);
*/
                //inflateView = View.inflate(this,R.layout.view_video,parentLayout);
                //layout.addView(createNewTextView(mEditText.getText().toString()));
                //inflateView=View.inflate(this,R.layout.view_video,parentLayout);
                //long startTime = System.currentTimeMillis();
            }
        });
    }
}



