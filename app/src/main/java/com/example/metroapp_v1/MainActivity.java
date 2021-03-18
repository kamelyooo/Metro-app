package com.example.metroapp_v1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.Permission;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements LocationListener, ShakeDetector.ShakeListener {

    AutoCompleteTextView startSpinner, distSpinner;
    Button calButton, nearStButton, allStaBT;
    Switch alert;

    SharedPreferences pref;
    ImageView allstimage;
    TextView calTextView, calTextView2, dateTv, timeTv;
    MenuItem clearPref;
    String start, end = "";
    ArrayList<String> line2 = new ArrayList();
    ArrayList<String> line1 = new ArrayList();
    ArrayList<String> line3 = new ArrayList();
    List<String> combinedList;
    LocationManager manager;
    ArrayList<String> rout;
    Location malloc;
    double mylatitude, mylongitude;
    float v;
    int ii;
    ArrayList<AllLocation> locations = new ArrayList();
    ArrayAdapter adapter;
    UserHistory userHistory;
    userDAO userDAO;
    String dateText, timeText;
    SimpleDateFormat date, time;
    int p = Runtime.getRuntime().availableProcessors();
    ExecutorService pool = Executors.newFixedThreadPool(p);


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startSpinner = findViewById(R.id.startSpinner);
        distSpinner = findViewById(R.id.distSpinner);
        calButton = findViewById(R.id.calButton);
        alert = findViewById(R.id.switch1);
        calButton.setEnabled(false);
        nearStButton = findViewById(R.id.nearStButton);
        allStaBT = findViewById(R.id.allStaBT);
        allstimage = findViewById(R.id.allstimage);
        calTextView = findViewById(R.id.calTextView);
        calTextView2 = findViewById(R.id.calTextView2);
        dateTv = findViewById(R.id.dateTv);
        timeTv = findViewById(R.id.timeTv);
        Sensey.getInstance().init(this);
        Sensey.getInstance().startShakeDetection(this);
        // get from pref
        pref = getPreferences(MODE_PRIVATE);
        start = pref.getString("start", start);
        startSpinner.setText(start);
        end = pref.getString("end", end);
        distSpinner.setText(end);

        //switch alert

        alert.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {


                Toast.makeText(MainActivity.this, "you will be notified 1 km before " + end + "station", Toast.LENGTH_SHORT).show();
                pool.execute(new Runnable() {
                    @Override
                    public void run() {


                        Intent in = new Intent(MainActivity.this,MyReceiver.class);
                        PendingIntent pe = PendingIntent.getBroadcast(MainActivity.this, 1, in, 0);
                        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                                (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            String[] perm = {Manifest.permission.ACCESS_FINE_LOCATION};
                            ActivityCompat.requestPermissions(MainActivity.this, perm, 2);


                        } else {
                            end = distSpinner.getText().toString();
                            Log.i("xxx", end);
                            Log.i("xxx", getLocation(end).getLatitude() + "");
                            Log.i("xxx", getLocation(end).getLongitude() + "");

                            manager.removeProximityAlert(pe);
                            manager.addProximityAlert(Objects.requireNonNull(getLocation(end)).getLatitude(),
                                    Objects.requireNonNull(getLocation(end)).getLongitude(), 500, -1, pe);


                        }
                    }
                });
//
//mytask t=new mytask();
//t.execute();

            }
        });


        date = new SimpleDateFormat("dd.MM.yyyy");
        dateText = date.format(new Date());
        time = new SimpleDateFormat("h:mm aaa");

        timeText = time.format(new Date());
        userDAO = MetroDatabase.getInstance(this).userDAO();

        if (startSpinner.getListSelection() < 0 && distSpinner.getListSelection() < 0)
            calButton.setEnabled(true);
        else calButton.setEnabled(false);


        Collections.addAll(locations, new AllLocation(31.334230899999998, 29.848982000000003, "Helwan")
                , new AllLocation(31.324875399999996, 29.862609100000004, "Ain Helwan")
                , new AllLocation(31.319091299999997, 30.131025700000002, "Ain Shams")
                , new AllLocation(31.334230899999998, 29.848982000000003, "Helwan University")
                , new AllLocation(31.3383642, 30.163648700000003, "New El Marg")
                , new AllLocation(31.3135831, 29.879082399999998, "Wadi Hof")
                , new AllLocation(31.303966199999998, 29.897136000000003, "Hadayeq Helwan")
                , new AllLocation(31.299515799999998, 29.906078400000002, "El Maasara")
                , new AllLocation(31.2875444, 29.9259651, "Tora El Asmant")
                , new AllLocation(31.281820600000003, 29.936259, "Kozzika")
                , new AllLocation(31.272979999999997, 29.9467633, "Tora El Balad")
                , new AllLocation(31.262956, 29.953300900000002, "Thakanat El Maadi")
                , new AllLocation(31.2576431, 29.9603028, "Maadi")
                , new AllLocation(31.25068259904597, 29.970412714467404, "Hadayeq El Maadi")
                , new AllLocation(31.242432966502054, 29.981770690471215, "Dar El Salam")
                , new AllLocation(31.231198254865276, 29.995639643526513, "El Zahraa")
                , new AllLocation(31.22961900579423, 30.006078823643545, "Mar Girgis")
                , new AllLocation(31.229244274946033, 30.018357411681972, "El Malek El Saleh")
                , new AllLocation(31.23541424496127, 30.029374623049247, "Al Sayyeda Zeinab")
                , new AllLocation(31.238383454866298, 30.03728405690291, "Saad Zaghloul")
                , new AllLocation(31.234445354866416, 30.044349689772606, "Sadat")
                , new AllLocation(31.23890291253915, 30.053660715441204, "Gamal Abdel nasser")
                , new AllLocation(31.242075654866614, 30.056948493246683, "Urabi")
                , new AllLocation(31.24605059719422, 30.061349953126516, "Al Shohadaa")
                , new AllLocation(31.25532718184934, 30.06774169465664, "Ghamra")
                , new AllLocation(31.27777953952215, 30.077557270706727, "El Demerdash")
                , new AllLocation(31.287545226030996, 30.082207991836928, "Manshiet El Sadr")
                , new AllLocation(31.29412633505965, 30.087449579471723, "Kobri El Qobba")
                , new AllLocation(31.298945704633102, 30.091662716099894, "Hammamat El Qobba")
                , new AllLocation(31.304544393162306, 30.098165826846618, "Saray El Qobba")
                , new AllLocation(31.31048329719539, 30.10610305646754, "Hadayeq El Zaitoun")
                , new AllLocation(31.313927253586083, 30.113258829641552, "Helmeyet El-Zaitoun")
                , new AllLocation(31.313669761497067, 30.121555536404212, "El-Matareyya")
                , new AllLocation(31.32439859758456, 30.139278827346267, "Ezbet El Nakhl")
                , new AllLocation(31.335682, 30.1520807, "El Marg")
        );
        Collections.addAll(line1,
                "Helwan", "Ain Helwan", "Helwan University", "Wadi Hof",
                "Hadayeq Helwan", "El Maasara", "Tora El Asmant", "Kozzika",
                "Tora El Balad", "Thakanat El Maadi", "Maadi", "Hadayeq El Maadi",
                "Dar El Salam", "El Zahraa", "Mar Girgis", "El Malek El Saleh", "Al Sayyeda Zeinab",
                "Saad Zaghloul", "Sadat", "Gamal Abdel nasser", "Urabi", "Al Shohadaa", "Ghamra",
                "El Demerdash", "Manshiet El Sadr", "Kobri El Qobba", "Hammamat El Qobba", "Saray El Qobba",
                "Hadayeq El Zaitoun", "Helmeyet El-Zaitoun", "El-Matareyya", "Ain Shams", "Ezbet El Nakhl", "El Marg"
                , "New El Marg");
        Collections.addAll(line2,
                "El Mounib", "Sakiat Mekki", "Omm el Misryeen", "Giza", "Faisal", "Cairo University", "Bohooth", "Dokki",
                "Opera", "Sadat", "Naguib", "Ataba", "Al Shohadaa", "Massara", "Road El Farag",
                "Sainte Teresa", "Khalafawy", "Mezallat", "Koliet El Zeraa", "Shobra El Kheima");
        Collections.addAll(line3,
                "Adly Mansour", "Hikestep", "Omar Ibn Al Khattab", "Qobaa", "Hisham Barakat", "El Nozha", "El Shams Club"
                , "Alf Maskan", "Heliopolis", "Haroun", "Al Ahram", "Koleyet El Banat", "Cairo Stadium", "Fair Zone",
                "Abbassiya", "Abdou Pasha", "El Geish", "Bab El Shaaria", "Ataba");

        combinedList = Stream.of(line1, line2, line3).flatMap(x -> x.stream()).collect(Collectors.toList());

        combinedList.remove("Sadat");
        combinedList.remove("Al Shohadaa");
        combinedList.remove("Ataba");
        Log.i("List with stream", combinedList + "");

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, combinedList);
        startSpinner.setAdapter(adapter);
        distSpinner.setAdapter(adapter);
        startSpinner.setThreshold(1);
        distSpinner.setThreshold(1);


        startSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                start = String.valueOf(s);
                Log.d("afterTextChanged", start);
                if (combinedList.contains(start)) {
                    distSpinner.setEnabled(true);
                    distSpinner.setText("");
                } else {
                    distSpinner.setEnabled(false);
                    calButton.setEnabled(false);

                }

            }

        });

        distSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                end = String.valueOf(s);
                Log.d("afterText Changed", end);
                if (combinedList.contains(end))
                    calButton.setEnabled(true);
                else {
                    calButton.setEnabled(false);
                    alert.setVisibility(View.INVISIBLE);
                }
            }

        });

    }

    static class Message {
        public String name;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }


    private Location getLocation(String address) {
        Geocoder geocoder = new Geocoder(this);
        try {
            address = address + " Metro Station Egypt";
            List<Address> addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null && !addressList.isEmpty()) {
                double latitude = addressList.get(0).getLatitude();
                double longitude = addressList.get(0).getLongitude();
                Location loc = new Location("");
                loc.setLatitude(latitude);
                loc.setLongitude(longitude);
                return loc;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onStop() {
        SharedPreferences.Editor editor = pref.edit();
        SharedPreferences.Editor editor1 = pref.edit();
        editor.putString("start", start);
        editor.apply();
        editor1.putString("end", end);
        editor1.apply();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Sensey.getInstance().stopShakeDetection(this);
        Sensey.getInstance().stop();
        pool.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (!combinedList.contains(start) || !combinedList.contains(end))
            calButton.setEnabled(false);
        super.onResume();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        pool.execute(() -> {
            Location loc = new Location("");
            Log.i("my loc", malloc + "");
            double minfloat = 500000000;
            for (int i = 0; i < locations.size(); i++) {

                loc.setLatitude(locations.get(i).getLat());
                loc.setLongitude(locations.get(i).getLon());
                v = loc.distanceTo(location);
                if (v < minfloat) {
                    minfloat = v;
                    ii = i;
                }

                Log.i("destance to", locations.get(i).getName() + (v / 1000) + "");

            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startSpinner.setText(locations.get(ii).getName());
                    Toast.makeText(MainActivity.this, "the nearest station is =" + locations.get(ii).getName()
                            , Toast.LENGTH_SHORT).show();
                }
            });

        });


    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    public void nearStButton(View view) {
        closeKeyBoard();
        YoYo.with(Techniques.Wave)
                .duration(500).repeat(3).playOn(findViewById(R.id.nearStButton));
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                String[] perm = {Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(this, perm, 1);
            } else {
                manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
            }
        } else {
            Intent in = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(in);
        }


    }


    public void calButton(View view) {
        closeKeyBoard();
        alert.setChecked(false);
        calTextView.setText("");
        calTextView2.setText("");
        dateTv.setText("");
        int startindex = 0, endindex = 0;

        rout = new ArrayList<>();
        ArrayList<String> revrout = new ArrayList<>();
        boolean wrong = false;
        byte elShohada1 = 21, elShohada2 = 12, sadat1 = 18, sadat2 = 9, ataba1 = 11, ataba2 = 18;

        userHistory = new UserHistory();
        userHistory = MetroDatabase.getInstance(this).userDAO().selectUserByfrom(start, end, dateText);
        if (userHistory != null) {
            calTextView2.setText(userHistory.route);
            calTextView.setText(userHistory.cals);
            dateTv.setText(userHistory.date);
            timeTv.setText(userHistory.time);
            alert.setVisibility(View.VISIBLE);

            //////////////// same line//////////////////////////////////

        } else {
            if (line1.contains(start) && line1.contains(end)) {
                startindex = line1.indexOf(start);
                endindex = line1.indexOf(end);
                if (startindex < endindex) {
                    rout.addAll(line1.subList(startindex + 1, endindex + 1));
                    calTextView.append("-Take El marg direction");
                } else {
                    rout.addAll(line1.subList(endindex, startindex));
                    Collections.reverse(rout);
                    calTextView.append("-Take Helwan direction");
                }
            } else if (line2.contains(start) && line2.contains(end)) {
                startindex = line2.indexOf(start);
                endindex = line2.indexOf(end);
                if (startindex < endindex) {
                    rout.addAll(line2.subList(startindex + 1, endindex + 1));
                    calTextView.append("-Take Shobra El Kheima direction");
                } else {
                    rout.addAll(line2.subList(endindex, startindex));
                    Collections.reverse(rout);
                    calTextView.append("-Take El Mounib direction");
                }

            } else if (line3.contains(start) && line3.contains(end)) {
                startindex = line3.indexOf(start);
                endindex = line3.indexOf(end);
                if (startindex < endindex) {
                    rout.addAll(line3.subList(startindex + 1, endindex + 1));
                    calTextView.append("-Take Ataba direction");
                } else {
                    rout.addAll(line3.subList(endindex, startindex));
                    Collections.reverse(rout);
                    calTextView.append("-Take Adly mansour direction");
                }
            }
            //***********************************************************************************
            else if (line1.contains(start) && line2.contains(end)) {//star in line 1 && end in line 2
                startindex = (byte) line1.indexOf(start);
                endindex = (byte) line2.indexOf(end);
                if (startindex < sadat1) { //line1 start in part1 // end line 2
                    rout.addAll(line1.subList(startindex + 1, sadat1 + 1));//from helwan to sadat
                    if (endindex < sadat2) {
                        calTextView.append("-Take El marg direction then El Mounib direction");
                        revrout.addAll(line2.subList(endindex, sadat2)); //from sadat to el monib
                        Collections.reverse(revrout);
                        rout.addAll(revrout);
                    } else if (endindex > sadat2) {//from sadat to shobra
                        rout.addAll(line2.subList(sadat2 + 1, endindex + 1));
                        calTextView.append("-Take El marg direction then Shobra El Kheima direction");
                    }
                } else if (startindex > elShohada1) {//start line 1 part 2 //end line2
                    rout.addAll(line1.subList(elShohada1, startindex));//from new marg to el shohada
                    Collections.reverse(rout);
                    if (endindex < elShohada2) {//from el shohada to el monib
                        calTextView.append("-Take Helwan direction then El Mounib direction");
                        revrout.addAll(line2.subList(endindex, elShohada2));
                        Collections.reverse(revrout);
                        rout.addAll(revrout);
                    } else if (endindex > elShohada2) {//from elshohada to shobra
                        rout.addAll(line2.subList(elShohada2 + 1, endindex + 1));
                        calTextView.append("-Take Helwan direction then Shobra El Kheima direction");
                    }
                } else if (startindex > sadat1 && startindex < elShohada1) {
                    if (start.equalsIgnoreCase("Gamal Abdel nasser")) {
                        rout.addAll(line1.subList(sadat1, startindex));
                        Collections.reverse(rout);
                        if (endindex < sadat2) {
                            calTextView.append("-Take Helwan direction then El Mounib direction");
                            revrout.addAll(line2.subList(endindex, sadat2)); //from sadat to el monib
                            Collections.reverse(revrout);
                            rout.addAll(revrout);
                        } else if (endindex > sadat2) {//from sadat to shobra
                            rout.addAll(line2.subList(sadat2 + 1, endindex + 1));
                            calTextView.append("-Take Helwan direction then shobra elkhema direction");
                        }
                    } else if (start.equalsIgnoreCase("Urabi")) {
                        rout.addAll(line1.subList(startindex + 1, elShohada1 + 1));
                        if (endindex < elShohada2) {//from el shohada to el monib
                            calTextView.append("-Take El marg direction then El Mounib direction");
                            revrout.addAll(line2.subList(endindex, elShohada2));
                            Collections.reverse(revrout);
                            rout.addAll(revrout);
                        } else if (endindex > elShohada2) {//from elshohada to shobra
                            calTextView.append("-Take El marg direction then ShobraEl Kheima direction");
                            rout.addAll(line2.subList(elShohada2 + 1, endindex + 1));
                        }
                    }
                }
            }
///***************************************************************************************************************************
            else if (line1.contains(end) && line2.contains(start)) {//star in line 2 && end in line 1
                startindex = (byte) line2.indexOf(start);
                endindex = (byte) line1.indexOf(end);
                // monib    sadat     shohada     shobra
                if (startindex < sadat2) {//start in line2 part1 //end in line1
                    rout.addAll(line2.subList(startindex + 1, sadat2 + 1));//from el monib to sadat
                    if (endindex < sadat1) {
                        revrout.addAll(line1.subList(endindex, sadat1));//sadat to helwan
                        Collections.reverse(revrout);
                        rout.addAll(revrout);
                        calTextView.append("-Take Shobra El Kheima direction then Helwan direction");
                    } else if (endindex > sadat1) {//sadat to helwan
                        rout.addAll(line1.subList(sadat1 + 1, endindex + 1));
                        calTextView.append("-Take Shobra El Kheima direction then El-Marg direction");
                    }
                }/// monib    sadat      shohada     shobra
                else if (startindex > elShohada2) {// start in line2 part2 //end in line1
                    rout.addAll(line2.subList(elShohada2, startindex));//from shobra to shohada
                    Collections.reverse(rout);
                    if (endindex < elShohada1) {
                        revrout.addAll(line1.subList(endindex, elShohada1));//from shohada to helwan
                        Collections.reverse(revrout);
                        calTextView.append("-Take El Mounib direction then Helwan direction");
                        rout.addAll(revrout);
                    } else if (endindex > elShohada1) {//from shohada to new marg
                        rout.addAll(line1.subList(elShohada1 + 1, endindex + 1));
                        calTextView.append("-Take El Mounib direction then El-Marg direction");
                    }
                } else if (startindex > sadat2 && startindex < elShohada2) {
                    if (start.equalsIgnoreCase("Naguib")) {
                        rout.addAll(line2.subList(sadat2, startindex));
                        Collections.reverse(rout);
                        if (endindex < sadat1) {
                            revrout.addAll(line1.subList(endindex, sadat1));//sadat to helwan
                            Collections.reverse(revrout);
                            rout.addAll(revrout);
                            calTextView.append("-Take El monib direction then Helwan direction");
                        } else if (endindex > sadat1) {//sadat to helwan
                            rout.addAll(line1.subList(sadat1 + 1, endindex + 1));
                            calTextView.append("-Take El monib direction then El-Marg direction");
                        }
                    }
                    if (start.equalsIgnoreCase("Ataba")) {
                        rout.addAll(line2.subList(startindex + 1, elShohada2 + 1));
                        if (endindex < elShohada1) {
                            revrout.addAll(line1.subList(endindex, elShohada1));//from shohada to helwan
                            Collections.reverse(revrout);
                            rout.addAll(revrout);
                            calTextView.append("-Take Shobra direction then Helwan direction");
                        } else if (endindex > elShohada1) {//from shohada to new marg
                            rout.addAll(line1.subList(elShohada1 + 1, endindex + 1));
                            calTextView.append("-Take Shobra direction then El-Marg direction");
                        }
                    }
                }

            }
////************************************************************************************************************88


            else if (line2.contains(start) && line3.contains(end)) {//start line 2   end line 3
                startindex = (byte) line2.indexOf(start);
                endindex = (byte) line3.indexOf(end);


                if (startindex < ataba1) {
                    rout.addAll(line2.subList(startindex + 1, ataba1 + 1));
                    calTextView.append("-Take Shobra direction then adly mansour direction");
                }
//            *************************
                else if (startindex > ataba1) {//start line2 part2 :end line3
                    rout.addAll(line2.subList(ataba1, startindex));
                    Collections.reverse(rout);
                    calTextView.append("-Take El monib direction then adly mansour direction");
                }
                revrout.addAll(line3.subList(endindex, ataba2));
                Collections.reverse(revrout);
                rout.addAll(revrout);
                //***************************************************
            } else if (line2.contains(end) && line3.contains(start)) {//start line3 :end line2 part1
                startindex = (byte) line3.indexOf(start);
                endindex = (byte) line2.indexOf(end);

                rout.addAll(line3.subList(startindex + 1, ataba2 + 1));
                if (endindex < ataba1) {
                    revrout.addAll(line2.subList(endindex, ataba1));
                    Collections.reverse(revrout);
                    rout.addAll(revrout);
                    calTextView.append("-Take Ataba direction then el monib direction");
                } else if (endindex > ataba1) {//start line3 :end line2 part2
                    rout.addAll(line2.subList(ataba1 + 1, endindex + 1));
                    calTextView.append("-Take Ataba direction shobra then direction");
                }

                /// ----------------------------------------------------
                else if (line1.contains(start) && line3.contains(end)) {//star in line 1 && end in line 3
                    startindex = (byte) line1.indexOf(start);
                    endindex = (byte) line3.indexOf(end);
                    if (startindex < sadat1) { //line1 start in part1 // end line 3
                        rout.addAll(line1.subList(startindex + 1, sadat1));//from helwan to sadat
                        rout.addAll(line2.subList(sadat2, ataba1));
                        calTextView.append("-Take new el marg direction then shobra directionthen then adly mansour direction");

                    } else if (startindex > elShohada1) {//start line 1 part 2 //then line2 // end in line3
                        rout.addAll(line1.subList(elShohada1, startindex));//from new marg to el shohada
                        Collections.reverse(rout);
                        calTextView.append("-Take Helwan direction then el monuib direction then adly mansour direction");
                    } else if (startindex > sadat1 && startindex < elShohada1) {
                        if (start.equalsIgnoreCase("Gamal Abdel nasser")) {
                            rout.addAll(line1.subList(sadat1, startindex));
                            Collections.reverse(rout);
                            rout.addAll(line2.subList(sadat2 + 1, ataba1));
                            calTextView.append("-Take helwan direction then shobra direction then adly mansour direction");
                        } else if (start.equalsIgnoreCase("Urabi")) {
                            rout.addAll(line1.subList(startindex + 1, elShohada1 + 1));
                            calTextView.append("-Take elmarg direction then then monuib direction adly mansour direction");
                        }

                    }
                    revrout.addAll(line3.subList(endindex, ataba2 + 1));
                    Collections.reverse(revrout);
                    rout.addAll(revrout);


                }

            }

            //------------------------------------------
            else if (line1.contains(start) && line3.contains(end)) {//star in line 1 && end in line 3
                startindex = (byte) line1.indexOf(start);
                endindex = (byte) line3.indexOf(end);
                if (startindex < sadat1) { //line1 start in part1 // end line 3
                    rout.addAll(line1.subList(startindex + 1, sadat1));//from helwan to sadat
                    rout.addAll(line2.subList(sadat2, ataba1));
                    calTextView.append("-Take new el marg direction then shobra directionthen then adly mansour direction");

                } else if (startindex > elShohada1) {//start line 1 part 2 //end line 3
                    rout.addAll(line1.subList(elShohada1, startindex));//from new marg to el shohada
                    Collections.reverse(rout);
                    calTextView.append("-Take Helwan direction then El monuib direction then adly mansour direction");
                } else if (startindex > sadat1 && startindex < elShohada1) {
                    if (start.equalsIgnoreCase("Gamal Abdel nasser")) {
                        rout.addAll(line1.subList(sadat1, startindex));
                        Collections.reverse(rout);
                        rout.addAll(line2.subList(sadat2 + 1, ataba1));
                        calTextView.append("-Take helwan direction then then adly mansour direction");
                    } else if (start.equalsIgnoreCase("Urabi")) {
                        rout.addAll(line1.subList(startindex + 1, elShohada1 + 1));
                        calTextView.append("-Take elmarg direction then adly mansour direction");
                    }

                }
                revrout.addAll(line3.subList(endindex, ataba2 + 1));
                Collections.reverse(revrout);
                rout.addAll(revrout);

            }//*****************
            else if (line1.contains(end) && line3.contains(start)) {     //start line3 to end line1
                startindex = (byte) line3.indexOf(start);
                endindex = (byte) line1.indexOf(end);

                rout.addAll(line3.subList(startindex + 1, ataba2 + 1));
                if (endindex > elShohada1) {
                    rout.addAll(line1.subList(elShohada1, endindex + 1));
                    calTextView.append("-Take ataba direction then shoubra direction then new el marg direction");
                } else if (endindex < elShohada1 && endindex > sadat1) {
                    revrout.addAll(line1.subList(endindex, elShohada1 + 1));
                    Collections.reverse(revrout);
                    rout.addAll(revrout);
                    calTextView.append("-Take ataba direction then Shoubra direction then helwan direction");
                } else if (endindex < sadat1) {
                    revrout.addAll(line2.subList(sadat2, ataba1));
                    Collections.reverse(revrout);
                    rout.addAll(revrout);
                    revrout.clear();
                    revrout.addAll(line1.subList(endindex, sadat1));
                    Collections.reverse(revrout);
                    rout.addAll(revrout);
                    calTextView.append("-Take ataba direction the El monuib direction then helwan direction");

                }
            }


            if (start.equals(end) || !combinedList.contains(start) || !combinedList.contains(end)) {
                YoYo.with(Techniques.Bounce)
                        .duration(500).repeat(1).playOn(findViewById(R.id.calButton));
                calTextView.setText("");
                Toast.makeText(this, "Please choose 2 different stations", Toast.LENGTH_SHORT).show();

            } else {
                alert.setVisibility(View.VISIBLE);
                calTextView2.append("-Your route is : " + rout);

                calTextView.append("\n-The time estimation is : " + rout.size() * 2 + " mins");
                if (rout.size() > 16)
                    calTextView.append("\n-The ticket price is : 10 L.E\n");
                else if (rout.size() > 9)
                    calTextView.append("\n-The ticket price  is : 7 L.E\n");
                if (rout.size() <= 9)
                    calTextView.append("\n-The ticket price  is : 5 L.E\n");
                calTextView.append("-Number of station is : " + rout.size());
                dateTv.setText(dateText);
                timeTv.setText(timeText);


                UserHistory userHistory = new UserHistory();
                userHistory.start = start;
                userHistory.end = end;
                userHistory.route = calTextView2.getText().toString();
                userHistory.cals = calTextView.getText().toString();
                userHistory.date = dateText;
                userHistory.time = timeText;
                long insert = userDAO.insert(userHistory);
                if (insert > 0)
                    Toast.makeText(this, "Trip added to history", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public void startclic(View view) {

        if (combinedList.contains(start)) {

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + start + " metro station"));
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } else {
            closeKeyBoard();
            Toast.makeText(this, "please enter a station to navigate to it", Toast.LENGTH_SHORT).show();
        }

    }

    public void onEndClick(View view) {
        if (combinedList.contains(end)) {

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + end + " metro station"));
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } else {
            closeKeyBoard();
            Toast.makeText(this, "please enter a station to navigate to it", Toast.LENGTH_SHORT).show();
        }
    }

    public void allStBt(View view) {
        Intent in = new Intent(this, AllstaActivity.class);
        startActivity(in);
    }


    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = pref.edit();
        SharedPreferences.Editor editor1 = pref.edit();
        editor.putString("start", start);
        editor.apply();
        editor1.putString("end", end);
        editor1.apply();
        super.onBackPressed();
    }


    private void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "feature not supported", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 2) {
            Intent in = new Intent(MainActivity.this, ArrivedActivity.class);
            PendingIntent pe = PendingIntent.getActivity(MainActivity.this, 0, in, 0);
            manager = (LocationManager) getSystemService(LOCATION_SERVICE);
            end = distSpinner.getText().toString();
            Log.i("xxx", end);
            Log.i("xxx", getLocation(end).getLatitude() + "");
            Log.i("xxx", getLocation(end).getLongitude() + "");

            try {

                manager.removeProximityAlert(pe);
                manager.addProximityAlert(Objects.requireNonNull(getLocation(end)).getLatitude(),
                        Objects.requireNonNull(getLocation(end)).getLongitude(), 1000, -1, pe);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onShakeDetected() {


    }

    @Override
    public void onShakeStopped() {
        calTextView.setText("");
        calTextView2.setText("");
        startSpinner.setText("");
        distSpinner.setText("");
        Toast.makeText(this, "Data cleared, enter new stations", Toast.LENGTH_SHORT).show();
        alert.setVisibility(View.INVISIBLE);

    }

    public void onClear(MenuItem item) {
        startSpinner.setText("");
        distSpinner.setText("");
        alert.setVisibility(View.INVISIBLE);
        calTextView.setText("");
        calTextView2.setText("");
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        SharedPreferences.Editor editor1 = pref.edit();
        editor1.clear();
        editor.apply();
        editor1.apply();
        Toast.makeText(this, "All saved user preferences deleted", Toast.LENGTH_SHORT).show();

    }

    public void historyClick(MenuItem item) {
        Intent intent = new Intent(this, HistoryListActivty.class);
        startActivity(intent);
    }

//class mytask extends AsyncTask<String,Void,Void>{
//    @Override
//    protected Void doInBackground(String... strings) {
//        Intent in = new Intent(MainActivity.this, ArrivedActivity.class);
//        PendingIntent pe = PendingIntent.getActivity(MainActivity.this, 1, in, 0);
//        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
//                (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            String[] perm = {Manifest.permission.ACCESS_FINE_LOCATION};
//            ActivityCompat.requestPermissions(MainActivity.this, perm, 2);
//
//
//        } else {
//            end = distSpinner.getText().toString();
//            Log.i("xxx", end);
//            Log.i("xxx", getLocation(end).getLatitude() + "");
//            Log.i("xxx", getLocation(end).getLongitude() + "");
//
//            manager.removeProximityAlert(pe);
//            manager.addProximityAlert(Objects.requireNonNull(getLocation(end)).getLatitude(),
//                    Objects.requireNonNull(getLocation(end)).getLongitude(), 1000, -1, pe);
//
//        }return null;
//    }
//}
}