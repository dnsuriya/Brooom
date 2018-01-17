package com.sidedrive.brooom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RiderEmail extends AppCompatActivity {

    private EditText autoTextEmail;
    private TextView text_error;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_email);

        autoTextEmail = findViewById(R.id.riderEmail);
        text_error = findViewById(R.id.rider_text_error);
    }

    public void onEmailEnter(View view) {
        final String email = autoTextEmail.getText().toString();

        if ( email.isEmpty()) {
            //fill the empty field
            text_error.setText("Enter the Email address");
            return;
        } else {
            //validate the email
            CharSequence emailStr = email;
            boolean b_email = ValidationUtil.isValidEmail(emailStr);

            if (!b_email) {
                text_error.setText("Enter valide Email address");
                return;
            }

            //change the activity
            final GlobalVariableMaintaining globalvariable = (GlobalVariableMaintaining) getApplicationContext();
            globalvariable.setEmail(email);

            startActivity(new Intent(this, Signup_Rider.class));
        }

    }
}
