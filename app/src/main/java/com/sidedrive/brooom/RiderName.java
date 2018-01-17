package com.sidedrive.brooom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RiderName extends AppCompatActivity {

    private EditText autoTextFirstName, autoTextLastName;
    private TextView text_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_name);

        autoTextFirstName = findViewById(R.id.riderFirstName);
        autoTextLastName = findViewById(R.id.riderLastName);
        text_error = findViewById(R.id.rider_text_error);
    }

    public void onNameEnter(View view) {

        final String firstName = autoTextFirstName.getText().toString();
        final String lastName = autoTextLastName.getText().toString();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            //fill the empty field
            text_error.setText("Enter the Name");
            return;
        } else {
            final GlobalVariableMaintaining globalvariable = (GlobalVariableMaintaining) getApplicationContext();
            globalvariable.setUserFirstName(firstName);
            globalvariable.setUserLastName(lastName);
            //go to next activity
            startActivity(new Intent(this, RiderEmail.class));
        }

    }
}
