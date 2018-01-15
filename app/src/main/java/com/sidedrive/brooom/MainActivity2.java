package com.sidedrive.brooom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity2 extends AppCompatActivity {

    private Button m_rider, m_driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        m_driver = (Button) findViewById(R.id.driver);
        m_rider = (Button) findViewById(R.id.rider);

        m_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent driverLoginIntent = new Intent(MainActivity2.this, DriverLoginActivity.class);
                startActivity(driverLoginIntent);
                finish();
                return;
            }
        });

        m_rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent riderLoginIntent = new Intent(MainActivity2.this, RiderLoginActivity.class);
                startActivity(riderLoginIntent);
                finish();
                return;
            }
        });

    }
}
