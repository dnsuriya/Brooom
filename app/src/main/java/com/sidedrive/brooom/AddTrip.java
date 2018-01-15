package com.sidedrive.brooom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddTrip extends AppCompatActivity {

    private EditText price;
    private FirebaseAuth mAuth;
    private TextView tripNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        price = findViewById(R.id.price);
        tripNo = findViewById(R.id.tripNo);
        mAuth = FirebaseAuth.getInstance();

        getTripCount();
    }

    private void getTripCount(){


        String user_id = mAuth.getCurrentUser().getUid();
        Query myTopPostsQuery = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Riders").child(user_id);
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {

                      Long x = (Long) dataSnapshot.child("NOOFTRIP").getValue();
                      x = x+1;
                      String trip = String.valueOf(x);
                    tripNo.setText(trip);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
            });

    }

    public void onAdd(View view) {
        String value = price.getText().toString();
        String user_id = mAuth.getCurrentUser().getUid();

        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(user_id).child("TRIP");

        String tripNumber = tripNo.getText().toString();
        String tripID = "Trip"+tripNumber;
        int trip_int = Integer.parseInt(tripNumber);

        //create map
        Map newPost = new HashMap();
        newPost.put(tripID, value);

        current_user_db.setValue(newPost);

        DatabaseReference current_user_db_update_trip = FirebaseDatabase.getInstance().
                getReference().child("Users").child("Riders").child(user_id);

        current_user_db_update_trip.child("NOOFTRIP").setValue(trip_int);


    }
}
