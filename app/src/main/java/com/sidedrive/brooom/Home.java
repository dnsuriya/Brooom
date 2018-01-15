package com.sidedrive.brooom;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView image_view;
    private TextView nav_user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
         nav_user = hView.findViewById(R.id.userName);
//        nav_user.setText("Wajira");

        image_view = hView.findViewById(R.id.imageView);

        mAuth = FirebaseAuth.getInstance();

        loadImage();

    }



    private void loadName(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = mAuth.getCurrentUser().getUid();
            //get user name
            Query myTopPostsQuery = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(uid);
            myTopPostsQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getChildrenCount() > 0) {

                        final String first_name = dataSnapshot.child("FIRST_NAME").getValue().toString();

                        nav_user.setText(first_name);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    }
    private void loadImage() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                String uid = mAuth.getCurrentUser().getUid();
                String email_add = profile.getEmail();
                nav_user.setText(uid);
                try {
                        Uri photoUrl = profile.getPhotoUrl();

                        Transformation transformation = new RoundedTransformationBuilder()
                                .borderColor(Color.BLACK)
                                .borderWidthDp(3)
                                .cornerRadiusDp(30)
                                .oval(true)
                                .build();

                        Picasso.with(Home.this).load(photoUrl).resize(300, 300).centerCrop().transform(transformation).into(image_view);



                } catch (Exception e) {
                }

//
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadImage();
        loadName();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImage();
    }


    ///home/wajira/AndroidStudioProjects/Riders/app
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        loadImage();
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_payment) {
            //handle the payments
            startActivity(new Intent(this, SelectPaymentMethod.class));
        } else if (id == R.id.nav_endTrip) {
            startActivity(new Intent(this, EndOfTrip.class));
        } else if (id == R.id.nav_complain) {
            startActivity(new Intent(this, Complain.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, UserSetting.class));
        }else if(id == R.id.nav_trips)
        {
            startActivity(new Intent(this, TripList.class));
        }
        else if(id == R.id.nav_addtrip)
        {
            startActivity(new Intent(this, AddTrip.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
