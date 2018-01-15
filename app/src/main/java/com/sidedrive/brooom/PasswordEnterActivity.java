package com.sidedrive.brooom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PasswordEnterActivity extends AppCompatActivity {

    TextView txtSignUp;
    AutoCompleteTextView text_password;
    TextView text_error;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_enter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtSignUp = findViewById(R.id.txtSignUp);
        text_password = findViewById(R.id.autoTextPassword);
        text_error = findViewById(R.id.password_text_error);

        mAuth = FirebaseAuth.getInstance();

    }

    public void on_signup(View view) {

        startActivity(new Intent(this, SelectSignup.class));
    }

    public void onFrogotPassword(View view) {
//        startActivity(new Intent(this, FrogetPassword.class));

        startActivity(new Intent(this, FrogetPassword.class));
    }

    public void onLogin(View view) {
        text_error.setText("");
        final String password = text_password.getText().toString();

        if (password.isEmpty()) {
            //enter the password
            text_error.setText("Enter Password");
            return;
        } else {
            //login success

            //check in the codebase using password and the phone number
            final GlobalVariableMaintaining globalvariable = (GlobalVariableMaintaining) getApplicationContext();
            final String phone = globalvariable.getPhoneNumber();

            //get user ID from firebase and again authendicated
            //check user is there or not
            Query phoneQueryRider = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").orderByChild("PHONE").equalTo(phone);
            phoneQueryRider.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //user already exist
                    if (dataSnapshot.getChildrenCount() > 0) {
                        //check the password
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                            text_error.setText(ds.getRef().getKey().toString());
                            String email = ds.child("EMAIL").getValue().toString();

                            globalvariable.setEmail(email);

                            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(PasswordEnterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        Log.d("enter", "showData: name: ");
                                        //change activity
                                        startActivity(new Intent(PasswordEnterActivity.this, Home.class));
                                    } else {
                                        text_error.setText("Authentication failed." + task.getException().getLocalizedMessage());
                                    }
                                }
                            });
                        }
                    } else {
                        text_error.setText("This mobile number is not registered in the Riders");

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            //if success
            //startActivity(new Intent(this, Home.class));

        }


    }


}
