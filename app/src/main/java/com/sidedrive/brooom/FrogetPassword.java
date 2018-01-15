package com.sidedrive.brooom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FrogetPassword extends AppCompatActivity {

    AutoCompleteTextView mobile, email;
    TextView error_text;
    Button btnSendEmail;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Frogot Password");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_froget_password);
        btnSendEmail = findViewById(R.id.btnSendEmail);
        mobile = findViewById(R.id.frogotMobile);
        error_text = findViewById(R.id.frogot_password_text_error);
        email = findViewById(R.id.frogotEmail);

        mAuth = FirebaseAuth.getInstance();

    }

    public void OnSendEmail(View view) {

        error_text.setText("");

        boolean b = false;
        String resetMethod = "";

        final String mobile_str = mobile.getText().toString();
        final String email_str = email.getText().toString();

        if (mobile_str.isEmpty() && email_str.isEmpty()) {
            //error code
            error_text.setText("Enter Mobile Number or Email Address");

        } else {

            //validate email or phone number
            if (!mobile_str.isEmpty()) {
                boolean b_phone = ValidationUtil.isValidPhoneNumber(mobile_str);

                if (!b_phone) {
                    error_text.setText("Enter valide Phone Number");
                    return;
                }
                if (mobile_str.length() != 10) {
                    error_text.setText("Enter valide Phone Number");
                    return;
                }

                //correct phone number
                resetMethod = "phone";

            } else if (!email_str.isEmpty()) {
                boolean b_email = ValidationUtil.isValidEmail(email_str);
                if (!b_email) {
                    error_text.setText("Enter valide Email");
                    return;
                }

                resetMethod = "email";

            }

            //check mobile number or email is in the codebase
            //if not display a message "invalide mobile number or email address"


            //send the email


            if (resetMethod.equals("phone")) {
                Query phoneQueryRider = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").orderByChild("PHONE").equalTo(mobile_str);
                phoneQueryRider.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //user already exist
                        if (dataSnapshot.getChildrenCount() > 0) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String email_add = ds.child("EMAIL").getValue().toString();
                                sendEmail(email_add);
                            }
                        } else {
                            error_text.setText("This mobile number is not registered in Riders");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                Query phoneQueryRider = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").orderByChild("EMAIL").equalTo(email_str);
                phoneQueryRider.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //user already exist
                        if (dataSnapshot.getChildrenCount() > 0) {
                            sendEmail(email_str);
                        } else {
                            error_text.setText("This email address is not registered in Riders");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            //change the activity

        }
    }


    private void sendEmail(String email_add) {

        error_text.setText(email_add);

        GlobalVariableMaintaining globalVariable = (GlobalVariableMaintaining) getApplicationContext();
        globalVariable.setEmail(email_add);

        mAuth.sendPasswordResetEmail(email_add)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(FrogetPassword.this, SendCode.class));
                        } else {
                            error_text.setText("Failed to send email to reset password " + task.getException().getLocalizedMessage());
                        }
                    }
                });

    }
}
