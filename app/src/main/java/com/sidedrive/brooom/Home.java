package com.sidedrive.brooom;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    private ImageView image_view;
    private TextView nav_user, rate;
    private FirebaseAuth mAuth;
    private RatingBar ratingBar;

    private GoogleMap m_map;
    private GoogleApiClient m_googleApiClient;
    private Location m_lastLocation;
    private LatLng m_pickupLocation, m_destination, m_driverLocation;
    private LocationRequest m_locationRequest;
    private static final int REQUEST_LOCATION = 2;
    private static final int DRIVERS_VISIBLE_RANGE = 10;

    private Button m_schedule,m_confirm;
    private TextView m_distance,m_price;

    private int m_radius = 1;
    private boolean m_driverFound = false;
    private String m_driverID;
    private Marker m_driverMarker = null;

    private void markDrivers()
    {
        DatabaseReference availableDriversRef = FirebaseDatabase.getInstance().getReference("AvailableDrivers");

        GeoFire geoFire = new GeoFire(availableDriversRef);
        GeoQuery driversLocationQuery = geoFire.queryAtLocation(new GeoLocation(m_pickupLocation.latitude, m_pickupLocation.longitude), DRIVERS_VISIBLE_RANGE);

        driversLocationQuery.removeAllListeners();

        driversLocationQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


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

        image_view = hView.findViewById(R.id.imageView);
        rate = hView.findViewById(R.id.userRating);
        ratingBar = hView.findViewById(R.id.userRatingBar);

        mAuth = FirebaseAuth.getInstance();

        loadImage();
        loadRating();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        m_schedule = (Button) findViewById(R.id.schedule);
        m_confirm = (Button) findViewById(R.id.confirm);

        m_distance = (TextView) findViewById(R.id.distance);
        m_price = (TextView) findViewById(R.id.price);

        //markDrivers();

        m_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference riderRequestRef = FirebaseDatabase.getInstance().getReference("RiderRequest");
                GeoFire geoFire = new GeoFire(riderRequestRef);
                geoFire.setLocation(userID, new GeoLocation(m_lastLocation.getLatitude(), m_lastLocation.getLongitude()));
                //geoFire.setLocation(userID, new GeoLocation(-35.8479853, 174.7715907));

                m_pickupLocation = new LatLng(m_lastLocation.getLatitude(), m_lastLocation.getLongitude());
                //m_pickupLocation = new LatLng(-35.8479853, 174.7715907);

                m_map.addMarker(new MarkerOptions().position(m_pickupLocation).title("Pickup Here"));

                getClosestDriver();
            }
        });

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

    private void loadRating(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = mAuth.getCurrentUser().getUid();
            //get user name
            Query myTopPostsQuery = FirebaseDatabase.getInstance().getReference().child("Users").
                    child("Riders").child(uid);
            myTopPostsQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getChildrenCount() > 0) {

                        rate.setText("has");

                        final String NOOFTRIP = dataSnapshot.child("NOOFTRIP").getValue().toString();
                        final String RATES = dataSnapshot.child("RATES").getValue().toString();

                        if(NOOFTRIP != "0") {
                            double tripNo = Double.parseDouble(NOOFTRIP);
                            double totalRate = Double.parseDouble(RATES);

                            double average_rate = totalRate / tripNo;

                            String rate_str = String.valueOf(average_rate);

                            rate.setText(rate_str);


                            ratingBar.setRating((float)average_rate);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
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




    private void getDriverLocation()
    {
        DatabaseReference availableDriverLocationRef = FirebaseDatabase.getInstance().getReference().child("OccupiedDrivers").child(m_driverID).child("l");

        availableDriverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    List<Object> locationMap = (List<Object>) dataSnapshot.getValue();

                    double locationLat = 0,  locationLng= 0;

                    if(locationMap.get(0) != null && locationMap.get(1) != null)
                    {
                        locationLat = Double.parseDouble(locationMap.get(0).toString());
                        locationLng = Double.parseDouble(locationMap.get(1).toString());
                    }

                    LatLng driverLatLngLocation = new LatLng(locationLat, locationLng);

                    if(m_driverMarker != null)
                        m_driverMarker.remove();

                    m_driverMarker = m_map.addMarker(new MarkerOptions().position(driverLatLngLocation).title("Your Driver"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getClosestDriver()
    {
        DatabaseReference availableDriversRef = FirebaseDatabase.getInstance().getReference("AvailableDrivers");

        GeoFire geoFire = new GeoFire(availableDriversRef);
        GeoQuery driversLocationQuery = geoFire.queryAtLocation(new GeoLocation(m_pickupLocation.latitude, m_pickupLocation.longitude), m_radius);

        driversLocationQuery.removeAllListeners();

        driversLocationQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!m_driverFound)
                {
                    m_driverID = key;
                    m_driverLocation = new LatLng(location.latitude, location.longitude);
                    m_driverFound = true;

                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(m_driverID);

                    String riderID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    HashMap mapRiderIds = new HashMap();
                    mapRiderIds.put("RiderID",riderID);

                    driverRef.updateChildren(mapRiderIds);

                   getDriverLocation();

                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!m_driverFound)
                {
                    m_radius++;
                    getClosestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void initializeGoogleApiClient() {
        m_googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        m_googleApiClient.connect();
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if((grantResults.length == 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) || (grantResults.length == 1
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) ) {
                // We can now safely use the API we requested access to
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                m_map.setMyLocationEnabled(true);

                if(m_googleApiClient != null && m_locationRequest != null)
                    LocationServices.FusedLocationApi.requestLocationUpdates(m_googleApiClient, m_locationRequest, this);

            } else {
                // Permission was denied or request was cancelled
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_map = googleMap;

        initializeGoogleApiClient();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
            return;
        }
        else {
            m_map.setMyLocationEnabled(true);
        }

        boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.style_json)));

        if(!success)
            Toast.makeText(Home.this, "Error on styles", Toast.LENGTH_SHORT).show();

        m_map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //m_map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // m_map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        m_locationRequest = new LocationRequest()
                .setInterval(1000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
            return;
        }
        else {
            LocationServices.FusedLocationApi.requestLocationUpdates(m_googleApiClient, m_locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        m_lastLocation = location;
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        m_map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        m_map.animateCamera(CameraUpdateFactory.zoomTo(11));

        //String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //DatabaseReference availableDrversRef = FirebaseDatabase.getInstance().getReference("AvailableDrivers");

        //GeoFire geoFireClient = new GeoFire(availableDrversRef);
        //geoFireClient.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
