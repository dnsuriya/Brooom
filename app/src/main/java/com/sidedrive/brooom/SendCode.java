package com.sidedrive.brooom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SendCode extends AppCompatActivity {

    private Button btnResend, btnOk;
    private TextView text_error;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_code);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setTitle("Code Sent");
        btnOk = findViewById(R.id.btnReceiveCode);
        btnResend = findViewById(R.id.btnReSendEmail);
        text_error = findViewById(R.id.code_text_error);

        mAuth = FirebaseAuth.getInstance();

    }

    public void OnReceiveCode(View view) {
        //validate the code has been send or not
        startActivity(new Intent(this, MainActivity.class));
    }

    public void OnReSendEmail(View view) {
        //validate the code has been send or not

        GlobalVariableMaintaining globalVariable = (GlobalVariableMaintaining) getApplicationContext();
        String email_add = globalVariable.getEmail();

        mAuth.sendPasswordResetEmail(email_add)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        } else {
                            text_error.setText("Failed to send email to reset password " + task.getException().getLocalizedMessage());
                        }
                    }
                });
    }
}
