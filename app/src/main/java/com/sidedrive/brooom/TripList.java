package com.sidedrive.brooom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TripList extends AppCompatActivity {

    //show the last trip as a list view
    //https://www.tutorialspoint.com/android/android_list_view.htm


    private ListView trip_list;
    private FirebaseAuth mAuth;
    private TextView tripdetail;

    ArrayList<String> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        tripdetail = findViewById(R.id.tripdetail);
        trip_list = findViewById(R.id.trip_list);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.list_view, list);

        trip_list.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        showlist();

    }

    private void showlist() {
        //using user ID get the trip details and show in list view

//        tripdetail.setText("has");



        String user_id = mAuth.getCurrentUser().getUid();
        Query myTopPostsQuery = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Riders").child(user_id);
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {

                    Long child =  dataSnapshot.child("TRIP").getChildrenCount();

                    String array[] = new String[5];

                    int i = 0;

                    for(DataSnapshot ds : dataSnapshot.child("TRIP").getChildren())
                    {
                        //retrive last 5 trips

                        if(i<5)
                        {
//                          tripdetail.setText(tripdetail.getText().toString() + " "+ds.getValue().toString());

                            array[i] = ds.getValue().toString();
                            ++i;
                            //add list view

                            list.add(ds.getValue().toString());

                        }


                    }
                    //iterate though to show last 5 trips

//
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });




    }
}
