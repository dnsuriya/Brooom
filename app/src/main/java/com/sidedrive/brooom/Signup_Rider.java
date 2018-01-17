package com.sidedrive.brooom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class Signup_Rider extends AppCompatActivity {

    Button btnRiderSignUp;
    AutoCompleteTextView edPassword;
    TextView text_error;
    CheckBox ridercheckbox;
    String phoneNum = "";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_rider);
        setTitle("Rider Sign Up");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(Signup_Rider.this, Home.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        btnRiderSignUp = findViewById(R.id.btnSignupRider);
        edPassword = findViewById(R.id.riderPassword);
        text_error = findViewById(R.id.rider_text_error);
        ridercheckbox = findViewById(R.id.ridercheckbox);
    }

    public void onSignUpRider(View view) {
        text_error.setText("");

        final GlobalVariableMaintaining globalvariable = (GlobalVariableMaintaining) getApplicationContext();


        //validate
        final String firstName =globalvariable.getUserFirstName();
        final String lastName =globalvariable.getUserLastName();
        final String email = globalvariable.getEmail();
        final String password = edPassword.getText().toString();

        if ( password.isEmpty()) {
            //fill the empty field
            text_error.setText("Enter the Password");
            return;
        } else {

            if (!ridercheckbox.isChecked()) {
                text_error.setText("Must agree to Terms and Conditions");
                return;
            }

            //finally enter details to database
             final String phone = globalvariable.getPhoneNumber();

            //check phone number already used or not

//inset to database
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Signup_Rider.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        // Toast.makeText(Signup_Driver.this, "sign up error", Toast.LENGTH_SHORT).show();
                        text_error.setText("Sign up error" + task.getException());
                    } else {
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(user_id);

//                        text_error.setText(user_id);

                        int tripNo = 0;
                        //create map
                        Map newPost = new HashMap();
                        newPost.put("FIRST_NAME", firstName);
                        newPost.put("LAST_NAME", lastName);
                        newPost.put("PHONE", phone);
                        newPost.put("EMAIL", email);
                        newPost.put("RATES",tripNo);
                        newPost.put("NOOFTRIP", tripNo);
                        newPost.put("TRIP",true);

//                        newPost.put("URI","");

                        current_user_db.setValue(newPost);

                        globalvariable.setEmail(email);
                    }
                }
            });
            //change the activity
            // startActivity(new Intent(this, ConfirmMobile.class));
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
