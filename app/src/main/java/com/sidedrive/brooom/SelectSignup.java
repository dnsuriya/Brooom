package com.sidedrive.brooom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SelectSignup extends AppCompatActivity {

    private Button btnRider, btnDriver, btnreset;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_signup);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Sign Up");

        btnRider = findViewById(R.id.btnregisterrider);
        btnreset = findViewById(R.id.btnreset);
        error = findViewById(R.id.text_error);
        btnRider.setEnabled(true);


    }


    public void OnRegisterRider(View view) {
        checkPhoneNumberRider();
    }

    private void checkPhoneNumberRider() {
        super.onStart();

        final GlobalVariableMaintaining globalvariable = (GlobalVariableMaintaining) getApplicationContext();
        final String phone = globalvariable.getPhoneNumber();


        Query phoneQueryRider = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").orderByChild("PHONE").equalTo(phone);
        phoneQueryRider.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //user already exist
                if (dataSnapshot.getChildrenCount() > 0) {
                    error.setText("Phone Number is already registered as a Rider.");
                    btnRider.setEnabled(false);
                    btnreset.setVisibility(View.VISIBLE);
                } else {
                    startActivity(new Intent(SelectSignup.this, Signup_Rider.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void OnReset(View view) {
        startActivity(new Intent(SelectSignup.this, MainActivity.class));


    }
}
