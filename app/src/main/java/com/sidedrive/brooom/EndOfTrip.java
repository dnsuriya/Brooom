package com.sidedrive.brooom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class EndOfTrip extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RatingBar ratingBar;
    private Button submit;
    private String old_rates;

    int j = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_trip);

        ratingBar = findViewById(R.id.ratingBar);
        submit = findViewById(R.id.submit);

        mAuth = FirebaseAuth.getInstance();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setTitle("Your Trip");


        getCurrentRate();
    }

    private void getCurrentRate() {

        final String user_id = mAuth.getCurrentUser().getUid();
        Query myTopPostsQuery = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Riders").child(user_id);
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {
                    old_rates =dataSnapshot.child("RATES").getValue().toString();

//                    submit.setText(old_rates);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void rateMe(View view) {

        final String user_id = mAuth.getCurrentUser().getUid();
        double old = Double.parseDouble(old_rates);
        double new_rate =  old+ ratingBar.getRating();

        String new_v = String.valueOf(new_rate);

//        submit.setText(new_v);
        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(user_id);
        current_user_db.child("RATES").setValue(new_v);

        startActivity(new Intent(this, Home.class));

    }

    @Override
    protected void onStart() {
        super.onStart();
        getCurrentRate();
    }
}
