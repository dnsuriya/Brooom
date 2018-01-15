package com.sidedrive.brooom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SelectPaymentMethod extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payment_method);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setTitle("Payment");
    }
}
