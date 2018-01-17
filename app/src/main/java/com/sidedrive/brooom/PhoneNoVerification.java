package com.sidedrive.brooom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class PhoneNoVerification extends AppCompatActivity {

    private EditText securityCode;

    private TextView phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_no_verification);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        securityCode = findViewById(R.id.securityKey);
        phone = findViewById(R.id.phone);

        //check in the codebase using password and the phone number
        final GlobalVariableMaintaining globalvariable = (GlobalVariableMaintaining) getApplicationContext();
        final String phoneno = globalvariable.getPhoneNumber();


        phone.setText("Enter the code sent to "+phoneno);
    }

    public void Onverify(View view) {
        //check the security key

        startActivity(new Intent(this, PasswordEnterActivity.class));
        finish();
    }

    public void onResend(View view) {

        //resend the code
    }
}
