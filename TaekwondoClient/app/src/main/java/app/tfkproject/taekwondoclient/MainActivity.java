package app.tfkproject.taekwondoclient;

import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import app.tfkproject.taekwondoclient.Model.Client;

public class MainActivity extends AppCompatActivity {

    public EditText edtIp;
    public Button btnOk, br1, br2, br3, br4, br5, mr1, mr2, mr3, mr4, mr5;
    public int nilai_score = 0;
    private String ip;

    Client cl;
    private DatabaseReference mDatabase;
    private DatabaseReference mPath;
    private String imei, key;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        imei = getIntent().getStringExtra("Key_imei");
        key = getIntent().getStringExtra("Key_cKey");

        readClientrequest(imei, key);

        //readScore(imei);

        getSupportActionBar().setTitle("Server:");
        getSupportActionBar().setSubtitle(imei);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        br1 = (Button) findViewById(R.id.biru1);
        br2 = (Button) findViewById(R.id.biru2);
        br3 = (Button) findViewById(R.id.biru3);
        br4 = (Button) findViewById(R.id.biru4);
        br5 = (Button) findViewById(R.id.biru5);

        mr1 = (Button) findViewById(R.id.merah1);
        mr2 = (Button) findViewById(R.id.merah2);
        mr3 = (Button) findViewById(R.id.merah3);
        mr4 = (Button) findViewById(R.id.merah4);
        mr5 = (Button) findViewById(R.id.merah5);

        br1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nilai_score = 1;
                inputIndikatorBiru(imei, key, nilai_score);
            }
        });

        br2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nilai_score = 2;
                inputIndikatorBiru(imei, key, nilai_score);
            }
        });

        br3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nilai_score = 3;
                inputIndikatorBiru(imei, key, nilai_score);
            }
        });

        br4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nilai_score = 4;
                inputIndikatorBiru(imei, key, nilai_score);
            }
        });

        br5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nilai_score = 5;
                inputIndikatorBiru(imei, key, nilai_score);
            }
        });

        /////-----------------------------------/////

        mr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nilai_score = 1;
                inputIndikatorMerah(imei, key, nilai_score);
            }
        });

        mr2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nilai_score = 2;
                inputIndikatorMerah(imei, key, nilai_score);
            }
        });

        mr3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nilai_score = 3;
                inputIndikatorMerah(imei, key, nilai_score);
            }
        });

        mr4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nilai_score = 4;
                inputIndikatorMerah(imei, key, nilai_score);
            }
        });

        mr5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nilai_score = 5;
                inputIndikatorMerah(imei, key, nilai_score);
            }
        });
    }

    /*private void inputIp(String ip) {
        LayoutInflater mInflater = LayoutInflater.from(this);
        View v = mInflater.inflate(R.layout.input_ip, null);

        final AlertDialog dialog = new AlertDialog.Builder(this).create();

        dialog.setView(v);
        dialog.setTitle("Input Server IP");
        dialog.setCancelable(false);

        btnOk = (Button) v.findViewById(R.id.buttonOK);
        edtIp = (EditText) v.findViewById(R.id.editTextIp);
        edtIp.setText(ip);

        btnOk.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if(edtIp.getText().toString().equals("")){
                    Toast.makeText(getBaseContext(), "Tidak boleh kosong!", Toast.LENGTH_LONG).show();
                }else{
                    String ip_add = edtIp.getText().toString();
                    //simpan ip ke memori
                    SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("KEY_IP", ip_add );
                    edit.apply();
                    dialog.dismiss();
                }

            }
        });

        dialog.show();
    }*/

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home){
            cancelClientRequest(imei, key);
            finish();
        }
        /*else if (id == R.id.act_ip) {
            //ambil data dari memori
            SharedPreferences bb = getSharedPreferences("my_prefs", 0);
            String ip = bb.getString("KEY_IP", "");
            inputIp(ip);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void inputIndikatorBiru(final String imei, final String key, int nBiru){
        mPath = mDatabase.child("server").child(imei).child("score").child("client");
        Map<String, Object> updates = new HashMap<>();
        String biru = String.valueOf(nBiru);
        updates.put("biru", biru);
        updates.put("ind_biru", biru);
        mPath.child(key).updateChildren(updates);

        // setelah 1,5 detik kembalikan lgi ke 0
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPath = mDatabase.child("server").child(imei).child("score").child("client");
                Map<String, Object> updates2 = new HashMap<>();
                updates2.put("biru", "0");
                updates2.put("ind_biru", "0");
                mPath.child(key).updateChildren(updates2);
            }
        }, 1500);
    }

    private void inputIndikatorMerah(final String imei, final String key, int nMerah){
        mPath = mDatabase.child("server").child(imei).child("score").child("client");
        Map<String, Object> updates = new HashMap<>();
        String merah = String.valueOf(nMerah);
        updates.put("merah", merah);
        updates.put("ind_merah", merah);
        mPath.child(key).updateChildren(updates);

        // setelah 1,5 detik kembalikan lgi ke 0
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPath = mDatabase.child("server").child(imei).child("score").child("client");
                Map<String, Object> updates2 = new HashMap<>();
                updates2.put("merah", "0");
                updates2.put("ind_merah", "0");
                mPath.child(key).updateChildren(updates2);
            }
        }, 1500);

    }

    private void cancelClientRequest(String imei, String key){
        mPath = mDatabase.child("server").child(imei).child("score").child("client");
        mPath.child(key).removeValue();
    }

    private void readClientrequest(final String imei, final String key){
        mPath = mDatabase.child("server").child(imei).child("score").child("client").child(key);
        mPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    Client data = dataSnapshot.getValue(Client.class);
                    String sts = data.getStatus();
                    if(sts.contains("1")){
                        dialog.dismiss();

                    }
                    else {
                        popUp(imei, key);
                    }
                } catch (NullPointerException e){
                    e.printStackTrace();
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void popUp(final String imei, final String key){
        LayoutInflater mInflater = LayoutInflater.from(MainActivity.this);
        View v = mInflater.inflate(R.layout.waiting_dialog, null);

        dialog = new AlertDialog.Builder(MainActivity.this).create();

        dialog.setView(v);
        dialog.setCancelable(false);

        Button btnCancel = (Button) v.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelClientRequest(imei, key);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /*private void readScore(final String imei) {
        mPath = mDatabase.child("server").child(imei).child("score");
        mPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Score data = dataSnapshot.getValue(Score.class);
                String nilai_biru = data.getBiru();
                String nilai_merah = data.getMerah();

                if(nilai_biru.equals("0") && nilai_merah.equals("0")){
                    //reset index button
                    resetIndexButton(imei, key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

    /*private void resetIndexButton(String imei, String key){
        mPath = mDatabase.child("server").child(imei).child("score").child("client");
        Map<String, Object> updates = new HashMap<>();
        updates.put("biru", "-1");
        updates.put("merah", "-1");
        mPath.child(key).updateChildren(updates);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelClientRequest(imei, key);
    }
}
