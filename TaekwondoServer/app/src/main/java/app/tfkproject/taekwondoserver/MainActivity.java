package app.tfkproject.taekwondoserver;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.tfkproject.taekwondoserver.Model.Client;
import app.tfkproject.taekwondoserver.Model.Score;

public class MainActivity extends AppCompatActivity {

    public EditText msg;
    public Button poinMinBiru, pointPlusBiru;
    public Button poinMinMerah, pointPlusMerah;

    public Button gamejomMinBiru, gamejomPlusBiru;
    public Button gamejomMinMerah, gamejomPlusMerah;

    public TextView scBiru,
                    scMerah,
                    txtTimer,
                    txtRonde,
                    indBiruCl1,
                    indBiruCl2,
                    indBiruCl3,
                    indMerahCl1,
                    indMerahCl2,
                    indMerahCl3,
                    lblGamejomBiru,
                    lblGamejomMerah;

    //Server server;
    CountDownTimer timer;
    long milliLeft;

    boolean isTimerRunning, isTimerPause;

    private Menu menu;

    private DatabaseReference mDatabase;
    private DatabaseReference mPath;
    private TelephonyManager mTelephonyManager;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    private int count_ronde = 1;
    private String imei;
    private int nilai_biru, nilai_merah, poin_biru, poin_merah, gmj_biru, gmj_merah;

    AlertDialog dialog;

    List<Client> daftarClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //init timer condition
        isTimerRunning = false;
        isTimerPause = true;

        //cek permission di android M
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
            } else {
                bacaIMEI();
            }
        }else{
            bacaIMEI();
        }

        scBiru = (TextView) findViewById(R.id.scorebiru);
        scMerah = (TextView) findViewById(R.id.scoremerah);

        txtTimer = (TextView) findViewById(R.id.timer);
        txtRonde = (TextView) findViewById(R.id.ronde);

        //indikator layout
        indBiruCl1 = (TextView) findViewById(R.id.ind_biru_cl1);
        indBiruCl2 = (TextView) findViewById(R.id.ind_biru_cl2);
        indBiruCl3 = (TextView) findViewById(R.id.ind_biru_cl3);

        indMerahCl1 = (TextView) findViewById(R.id.ind_merah_cl1);
        indMerahCl2 = (TextView) findViewById(R.id.ind_merah_cl2);
        indMerahCl3 = (TextView) findViewById(R.id.ind_merah_cl3);

        poinMinBiru = (Button) findViewById(R.id.minbiru);
        pointPlusBiru = (Button) findViewById(R.id.plusbiru);
        poinMinMerah = (Button) findViewById(R.id.minmerah);
        pointPlusMerah = (Button) findViewById(R.id.plusmerah);

        gamejomMinBiru = (Button) findViewById(R.id.gamjeomminbiru);
        gamejomPlusBiru = (Button) findViewById(R.id.gamjeomplusbiru);
        gamejomMinMerah = (Button) findViewById(R.id.gamjeomminmerah);
        gamejomPlusMerah = (Button) findViewById(R.id.gamjeoumplusmerah);

        lblGamejomBiru = (TextView) findViewById(R.id.txt_gamejom_biru);
        lblGamejomMerah = (TextView) findViewById(R.id.txt_gamejom_merah);
        /*server = new Server(this);
        tampilIp();*/
        daftarClient = new ArrayList<>();

        readScore(imei, scBiru, scMerah);
        readGamejom(imei, lblGamejomBiru, lblGamejomMerah);

        readClientReq(imei);

        penyamaanTombol(imei);

        setInitGamejom();
    }

    private void aturGamejom(){
        gamejomMinBiru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gmj_biru -= 1;
                gmj_merah += 0;
                setGamejom(gmj_biru, gmj_merah);
            }
        });

        gamejomPlusBiru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gmj_biru += 1;
                gmj_merah += 0;
                setGamejom(gmj_biru, gmj_merah);
            }
        });

        gamejomPlusMerah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gmj_biru += 0;
                gmj_merah += 1;
                setGamejom(gmj_biru, gmj_merah);
            }
        });

        gamejomMinMerah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gmj_biru += 0;
                gmj_merah -= 1;
                setGamejom(gmj_biru, gmj_merah);
            }
        });
    }

    private void aturPoint(){
        poinMinBiru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                poin_biru -= 1;
                poin_merah += 0;
                setScore(poin_biru, poin_merah);
            }
        });

        pointPlusBiru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                poin_biru += 1;
                poin_merah += 0;
                setScore(poin_biru, poin_merah);
            }
        });

        pointPlusMerah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                poin_biru += 0;
                poin_merah += 1;
                setScore(poin_biru, poin_merah);
            }
        });

        poinMinMerah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                poin_biru += 0;
                poin_merah -= 1;
                setScore(poin_biru, poin_merah);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            bacaIMEI();
        }
        else{
            finish();
        }
    }

    private void bacaIMEI() {
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = mTelephonyManager.getDeviceId();

        setServerStatus(imei, "online");
        setInitScore();
    }

    private void timerStart(long millisUntilFinished){
        timer = new CountDownTimer(millisUntilFinished, 1000) { // adjust the milli seconds here //90000

            public void onTick(long millisUntilFinished) {
                milliLeft = millisUntilFinished;
                txtTimer.setText(""+String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                txtTimer.setText("0:0");
                count_ronde += 1;
                //istirahat 45 detik
                timerIstirahat(45000); //45000 = 45 detik
                txtRonde.setText("Istirahat");

                if(count_ronde == 4){ //1-4 = 3 item yang artinya 3 ronde
                    txtTimer.setText("0:0");
                    txtRonde.setText("(selesai!)");
                    isTimerRunning = false;
                    isTimerPause = true;
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_play));
                    timer.cancel();
                }
            }
        };
        timer.start();
    }

    private void timerIstirahat(long millisUntilFinished){
        timer = new CountDownTimer(millisUntilFinished, 1000) { // adjust the milli seconds here //90000

            public void onTick(long millisUntilFinished) {
                milliLeft = millisUntilFinished;
                txtTimer.setText(""+String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                txtTimer.setText("0:0");
                txtRonde.setText("Ronde: "+count_ronde);
                //istirahat 45 detik
                timerStart(90000); //90000 = 1 menit 30 dtk
            }
        };
        timer.start();
    }

    public void timerPause() {
        /*if(timer == null){
            Toast.makeText(this, "Game harus dimulai terlebih dahulu!", Toast.LENGTH_SHORT).show();
        }else {
            timer.cancel();
        }*/
        timer.cancel();
    }

    private void timerResume() {
        timerStart(milliLeft);
    }

    /*private void tampilIp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        //builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Your Server IP");
        builder.setMessage(server.getIpAddress()+":"+server.getPort());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }*/

    private void setServerStatus(String imei, String status) {
        mPath = mDatabase.child("server").child(imei);
        Map<String, Object> updates = new HashMap<>();
        updates.put("id", imei);
        updates.put("status", status);
        mPath.updateChildren(updates);
    }

    private void readScore(String imei, final TextView txtBiru, final TextView txtMerah) {
        mPath = mDatabase.child("server").child(imei).child("score");
        mPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Score data = dataSnapshot.getValue(Score.class);
                    nilai_biru = data.getBiru();
                    nilai_merah = data.getMerah();

                    txtBiru.setText(""+nilai_biru);
                    txtMerah.setText(""+nilai_merah);
                } catch (Exception e){
                    e.printStackTrace();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readGamejom(String imei, final TextView txtGmjBiru, final TextView txtGmjMerah) {
        mPath = mDatabase.child("server").child(imei).child("gamejom");
        mPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Score data = dataSnapshot.getValue(Score.class);
                    gmj_biru = data.getBiru();
                    gmj_merah = data.getMerah();

                    txtGmjBiru.setText(""+gmj_biru);
                    txtGmjMerah.setText(""+gmj_merah);
                } catch (Exception e){
                    e.printStackTrace();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void penyamaanTombol(String imei){
        mPath = mDatabase.child("server").child(imei).child("score").child("client");
        mPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                daftarClient.clear();
                try{
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Client data = postSnapshot.getValue(Client.class);
                        daftarClient.add(data);

                        // here you can access to name property like university.name

                    }

                    String biru_cl1 = daftarClient.get(0).getBiru();
                    String biru_cl2 = daftarClient.get(1).getBiru();
                    String biru_cl3 = daftarClient.get(2).getBiru();

                    String merah_cl1 = daftarClient.get(0).getMerah();
                    String merah_cl2 = daftarClient.get(1).getMerah();
                    String merah_cl3 = daftarClient.get(2).getMerah();


                    String indbiru_cl1 = daftarClient.get(0).getInd_biru();
                    String indbiru_cl2 = daftarClient.get(1).getInd_biru();
                    String indbiru_cl3 = daftarClient.get(2).getInd_biru();

                    String indmerah_cl1 = daftarClient.get(0).getInd_merah();
                    String indmerah_cl2 = daftarClient.get(1).getInd_merah();
                    String indmerah_cl3 = daftarClient.get(2).getInd_merah();

                    //Toast.makeText(MainActivity.this, indbiru_cl1+"", Toast.LENGTH_SHORT).show();
                    //biru
                    indBiruCl1.setText(indbiru_cl1);
                    indBiruCl2.setText(indbiru_cl2);
                    indBiruCl3.setText(indbiru_cl3);
                    //merah
                    indMerahCl1.setText(indmerah_cl1);
                    indMerahCl2.setText(indmerah_cl2);
                    indMerahCl3.setText(indmerah_cl3);

                    //logika kombinasi untuk min 2 client tekan tombol bersamaan
                    //======================== BIRU ========================//
                    if(biru_cl1.equals(biru_cl2)){
                        if(biru_cl1.equals("1")){
                            nilai_biru += 1;
                            nilai_merah += 0;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(biru_cl1.equals("2")){
                            nilai_biru += 2;
                            nilai_merah += 0;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(biru_cl1.equals("3")){
                            nilai_biru += 3;
                            nilai_merah += 0;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(biru_cl1.equals("4")){
                            nilai_biru += 4;
                            nilai_merah += 0;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(biru_cl1.equals("5")){
                            nilai_biru += 5;
                            nilai_merah += 0;
                            setScore(nilai_biru, nilai_merah);
                        }

                    }
                    if (biru_cl1.equals(biru_cl3)){
                        if(biru_cl1.equals("1")){
                            nilai_biru += 1;
                            nilai_merah += 0;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(biru_cl1.equals("2")){
                            nilai_biru += 2;
                            nilai_merah += 0;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(biru_cl1.equals("3")){
                            nilai_biru += 3;
                            nilai_merah += 0;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(biru_cl1.equals("4")){
                            nilai_biru += 4;
                            nilai_merah += 0;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(biru_cl1.equals("5")){
                            nilai_biru += 5;
                            nilai_merah += 0;
                            setScore(nilai_biru, nilai_merah);
                        }
                    }
                    if(biru_cl2.equals(biru_cl3)){
                        if(biru_cl2.equals("1")){
                            nilai_biru += 1;
                            nilai_merah += 0;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(biru_cl2.equals("2")){
                            nilai_biru += 2;
                            nilai_merah += 0;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(biru_cl2.equals("3")){
                            nilai_biru += 3;
                            nilai_merah += 0;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(biru_cl2.equals("4")){
                            nilai_biru += 4;
                            nilai_merah += 0;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(biru_cl2.equals("5")){
                            nilai_biru += 5;
                            nilai_merah += 0;
                            setScore(nilai_biru, nilai_merah);
                        }
                    }

                    //logika kombinasi untuk 3 client tekan tombol bersamaan
                    if (biru_cl1.equals("1") && biru_cl2.equals("1") && biru_cl3.equals("1")) {
                        nilai_biru += 1;
                        nilai_merah += 0;
                        setScore(nilai_biru, nilai_merah);
                    }
                    else if (biru_cl1.equals("2") && biru_cl2.equals("2") && biru_cl3.equals("2")) {
                        nilai_biru += 2;
                        nilai_merah += 0;
                        setScore(nilai_biru, nilai_merah);
                    }
                    else if (biru_cl1.equals("3") && biru_cl2.equals("3") && biru_cl3.equals("3")) {
                        nilai_biru += 3;
                        nilai_merah += 0;
                        setScore(nilai_biru, nilai_merah);
                    }
                    else if (biru_cl1.equals("4") && biru_cl2.equals("4") && biru_cl3.equals("4")) {
                        nilai_biru += 4;
                        nilai_merah += 0;
                        setScore(nilai_biru, nilai_merah);
                    }
                    else if (biru_cl1.equals("5") && biru_cl2.equals("5") && biru_cl3.equals("5")) {
                        nilai_biru += 3;
                        nilai_merah += 0;
                        setScore(nilai_biru, nilai_merah);
                    }

                    //======================== MERAH ========================//

                    if(merah_cl1.equals(merah_cl2)){
                        if(merah_cl1.equals("1")){
                            nilai_biru += 0;
                            nilai_merah += 1;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(merah_cl1.equals("2")){
                            nilai_biru += 0;
                            nilai_merah += 2;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(merah_cl1.equals("3")){
                            nilai_biru += 0;
                            nilai_merah += 3;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(merah_cl1.equals("4")){
                            nilai_biru += 0;
                            nilai_merah += 4;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(merah_cl1.equals("5")){
                            nilai_biru += 0;
                            nilai_merah += 5;
                            setScore(nilai_biru, nilai_merah);
                        }
                    }
                    if(merah_cl1.equals(merah_cl3)){
                        if(merah_cl1.equals("1")){
                            nilai_biru += 0;
                            nilai_merah += 1;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(merah_cl1.equals("2")){
                            nilai_biru += 0;
                            nilai_merah += 2;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(merah_cl1.equals("3")){
                            nilai_biru += 0;
                            nilai_merah += 3;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(merah_cl1.equals("4")){
                            nilai_biru += 0;
                            nilai_merah += 4;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(merah_cl1.equals("5")){
                            nilai_biru += 0;
                            nilai_merah += 5;
                            setScore(nilai_biru, nilai_merah);
                        }
                    }
                    if(merah_cl2.equals(merah_cl3)){
                        if(merah_cl2.equals("1")){
                            nilai_biru += 0;
                            nilai_merah += 1;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(merah_cl2.equals("2")){
                            nilai_biru += 0;
                            nilai_merah += 2;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(merah_cl2.equals("3")){
                            nilai_biru += 0;
                            nilai_merah += 3;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(merah_cl2.equals("4")){
                            nilai_biru += 0;
                            nilai_merah += 4;
                            setScore(nilai_biru, nilai_merah);
                        }
                        if(merah_cl2.equals("5")){
                            nilai_biru += 0;
                            nilai_merah += 5;
                            setScore(nilai_biru, nilai_merah);
                        }
                    }

                    if (merah_cl1.equals("1") && merah_cl2.equals("1") && merah_cl3.equals("1")) {
                        nilai_biru += 0;
                        nilai_merah += 1;
                        setScore(nilai_biru, nilai_merah);
                    }
                    else if (merah_cl1.equals("2") && merah_cl2.equals("2") && merah_cl3.equals("2")) {
                        nilai_biru += 0;
                        nilai_merah += 2;
                        setScore(nilai_biru, nilai_merah);
                    }
                    else if (merah_cl1.equals("3") && merah_cl2.equals("3") && merah_cl3.equals("3")) {
                        nilai_biru += 0;
                        nilai_merah += 3;
                        setScore(nilai_biru, nilai_merah);
                    }
                    else if (merah_cl1.equals("4") && merah_cl2.equals("4") && merah_cl3.equals("4")) {
                        nilai_biru += 0;
                        nilai_merah += 4;
                        setScore(nilai_biru, nilai_merah);
                    }
                    else if (biru_cl1.equals("5") && merah_cl2.equals("5") && merah_cl3.equals("5")) {
                        nilai_biru += 0;
                        nilai_merah += 5;
                        setScore(nilai_biru, nilai_merah);
                    }



                } catch (Exception e){
                    Toast.makeText(MainActivity.this, "Client masih kurang", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readClientReq(final String imei){
        mPath = mDatabase.child("server").child(imei).child("score").child("client");
        mPath.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(daftarClient.size() < 3){
                    Client data = dataSnapshot.getValue(Client.class);
                    String sts = data.getStatus();
                    final String key = data.getId_client();
                    //Toast.makeText(MainActivity.this, ""+ sts, Toast.LENGTH_SHORT).show();
                    if(sts.contains("0")){
                        //munculkan popup
                        ////
                        LayoutInflater mInflater = LayoutInflater.from(MainActivity.this);
                        View v = mInflater.inflate(R.layout.popup_dialog, null);

                        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();

                        dialog.setView(v);
                        dialog.setCancelable(false);

                        TextView txtkey = (TextView) v.findViewById(R.id.client_key);
                        txtkey.setText(key);

                        Button btnAcc = (Button) v.findViewById(R.id.btn_acc);
                        btnAcc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateClientSts(key);
                                dialog.dismiss();
                            }
                        });

                        Button btnDec = (Button) v.findViewById(R.id.btn_dec);
                        btnDec.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        ////
                    }
                    else{
                        if(dialog != null){
                            dialog.dismiss();
                        }
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Maaf, tidak bisa menerima request, client sudah penuh", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Toast.makeText(MainActivity.this, "Client batal melakukan request", Toast.LENGTH_SHORT).show();
                if(dialog != null){
                    dialog.dismiss();
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
            }
        });
    }

    private void setScore(int scoreBiru, int scoreMerah){
        mPath = mDatabase.child("server").child(imei);
        Map<String, Object> updates = new HashMap<>();
        updates.put("biru", scoreBiru);
        updates.put("merah", scoreMerah);
        mPath.child("score").updateChildren(updates);
    }

    private void setInitScore(){
        mPath = mDatabase.child("server").child(imei);
        Map<String, Object> updates = new HashMap<>();
        updates.put("biru", 0);
        updates.put("merah", 0);
        mPath.child("score").updateChildren(updates);
    }

    private void setInitGamejom(){
        mPath = mDatabase.child("server").child(imei);
        Map<String, Object> updates = new HashMap<>();
        updates.put("biru", 0);
        updates.put("merah", 0);
        mPath.child("gamejom").updateChildren(updates);
    }

    private void setGamejom(int nilaiBiru, int nilaiMerah){
        mPath = mDatabase.child("server").child(imei);
        Map<String, Object> updates = new HashMap<>();
        updates.put("biru", nilaiBiru);
        updates.put("merah", nilaiMerah);
        mPath.child("gamejom").updateChildren(updates);
    }

    private void updateClientSts(String key){
        mPath = mDatabase.child("server").child(imei).child("score").child("client");
        Map<String, Object> updates = new HashMap<>();
        updates.put("id_client", key);
        updates.put("status", "1"); //0 = belum di acc, 1 = di acc
        mPath.child(key).updateChildren(updates);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.act_reset) {
            /*
            * Saat server tekan tombol reset, tolong client kluar satu-satu (back/kembali)
            * sehingga, tidak ada lagi client yang terhubung di server
            * */
            //reset = (isTimerRunning = false, isTimerPause = true) stop timer
            //set
            isTimerRunning = false;
            isTimerPause = true;
            txtTimer.setText("0:0");
            txtRonde.setText("(mulai)");

            //scBiru.setText("0");
            //scMerah.setText("0");
            Toast.makeText(this, "Counter direset!", Toast.LENGTH_SHORT).show();
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_play));
            timerPause();

            setInitScore();

            return true;
        }
        if (id == R.id.act_mulai) {
            if(daftarClient.size() < 3){
                int x = daftarClient.size();
                int jum_client = 3 - x;
                Toast.makeText(MainActivity.this, "Maaf, dibutuhkan "+jum_client+" client lagi untuk dapat memulai", Toast.LENGTH_SHORT).show();
            }else{
                //munculkan menu reset
                menu.getItem(1).setVisible(true);
                // (isTimerRunning = false, isTimerPause = true) start timer (init condition)
                if(!isTimerRunning && isTimerPause){
                    Toast.makeText(this, "Mulai!", Toast.LENGTH_SHORT).show();
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pause));
                    timerStart(90000); //90000 = 1 menit 30 dtk
                    //set
                    isTimerRunning = true;
                    isTimerPause = false;
                    txtRonde.setText("Ronde: "+count_ronde);

                    setInitScore();

                    aturPoint();
                    aturGamejom();
                }
                // (isTimerRunning = true, isTimerPause = false) pause timer
                else if(isTimerRunning && !isTimerPause){
                    Toast.makeText(this, "Timer di pause", Toast.LENGTH_SHORT).show();
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_play));
                    timerPause();
                    //set
                    isTimerRunning = true;
                    isTimerPause = true;
                }
                // (isTimerRunning = true, isTimerPause = true) resume timer
                else if(isTimerRunning && isTimerPause){
                    Toast.makeText(this, "Lanjutkan!", Toast.LENGTH_SHORT).show();
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pause));
                    timerResume();
                    //set
                    isTimerRunning = true;
                    isTimerPause = false;

                    aturPoint();
                    aturGamejom();
                }
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setServerStatus(imei, "online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        setServerStatus(imei, "offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setServerStatus(imei, "online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setServerStatus(imei, "offline");
    }

    @Override
    protected void onStop() {
        super.onStop();
        setServerStatus(imei, "offline");
    }
}
