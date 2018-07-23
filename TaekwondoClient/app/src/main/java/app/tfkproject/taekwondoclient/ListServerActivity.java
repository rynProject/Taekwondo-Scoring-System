package app.tfkproject.taekwondoclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import app.tfkproject.taekwondoclient.Model.Server;
import app.tfkproject.taekwondoclient.Model.Client;

public class ListServerActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference mPath;
    FirebaseRecyclerAdapter<Server, ListServerHolder> adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_server);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getSupportActionBar().setTitle("List Server");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true); //urutkan secara terbalik
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);

        readData();

    }

    private void readData() {
        mPath = mDatabase.child("server");

        adapter = new FirebaseRecyclerAdapter<Server, ListServerHolder>(
                Server.class,
                R.layout.item_server,
                ListServerHolder.class,
                mPath
        ) {
            @Override
            protected void populateViewHolder(ListServerHolder viewHolder, final Server model, final int position) {
                viewHolder.nama.setText(model.getId());
                final String status = model.getStatus();
                viewHolder.status.setText(status);
                if(status.contains("online")){
                    viewHolder.img_sts.setImageResource(R.drawable.rounded_green);
                }
                else{
                    viewHolder.img_sts.setImageResource(R.drawable.rounded_red);
                }

                viewHolder.cardItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(status.contains("online")){
                            final String key = makeClientRequest(model.getId());
                            Intent intent = new Intent(ListServerActivity.this, MainActivity.class);
                            intent.putExtra("Key_imei", model.getId());
                            intent.putExtra("Key_cKey", key);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(ListServerActivity.this, "Maaf, server offline. Anda tidak bisa masuk", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

    private int readJumClient(String imei){
        final List<Client> daftarClient = new ArrayList<>();
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
                } catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return daftarClient.size();
    }

    private String makeClientRequest(String imei){
        mPath = mDatabase.child("server").child(imei).child("score").child("client");
        String key = mPath.push().getKey();
        Map<String, Object> updates = new HashMap<>();
        updates.put("id_client", key);
        updates.put("status", "0"); //0 = belum di acc, 1 = di acc
        updates.put("biru", "0");
        updates.put("merah", "0");
        mPath.child(key).updateChildren(updates);
        return key;
    }

    private void cancelClientRequest(String imei, String key){
        mPath = mDatabase.child("server").child(imei).child("score").child("client");
        mPath.child(key).removeValue();
    }

}
