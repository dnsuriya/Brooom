package com.sidedrive.brooom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ConfirmMobile extends AppCompatActivity {

    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_mobile);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Confirm Mobile Number");
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    public void OnConfirm(View view) {
        startActivity(new Intent(this, PasswordEnterActivity.class));
    }
}
