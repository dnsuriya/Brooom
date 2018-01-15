package com.sidedrive.brooom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;


public class MobileNumberEnterActivity extends AppCompatActivity {
    Button btn_login;
    AutoCompleteTextView text_email;
    TextView text_error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number_enter);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_login = findViewById(R.id.btnLogin);
        text_email = findViewById(R.id.autoTextLoginEmail);
        text_error = findViewById(R.id.mobile_text_error);
    }


    public void onLogin(View view) {
        text_error.setText("");

        String phonenumber = text_email.getText().toString();

        if (phonenumber.isEmpty()) {
            //enter email
            text_error.setText("Enter Phone Number");
            text_email.setText("");
            return;

        } else if (phonenumber.length() != 10) {
            text_error.setText("Enter Valid Phone Number");
            return;
        } else {
            //set mobile number

            // Calling Application class (see application tag in AndroidManifest.xml)
            final GlobalVariableMaintaining globalVariable = (GlobalVariableMaintaining) getApplicationContext();

            globalVariable.setPhoneNumber(phonenumber);


            //login success
            startActivity(new Intent(this, PhoneNoVerification.class));
        }
        //email.setText(String.valueOf(b));

    }
}
