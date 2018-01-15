package com.sidedrive.brooom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class PhoneNoVerification extends AppCompatActivity {

    private EditText securityCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_no_verification);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        securityCode = findViewById(R.id.securityKey);
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
