package com.example.tasteless.svangur;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Info extends ActionBarActivity {

    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String logo = extras.getString("logo");
            String title = extras.getString("name");
            String address = extras.getString("address");
            String phoneNumber = extras.getString("phoneNumber");
            String website = extras.getString("website");
            latitude = extras.getString("latitude");
            longitude = extras.getString("longitude");

            TextView view1 = (TextView)findViewById(R.id.infoText);
            view1.setText(title);
            TextView view2 = (TextView)findViewById(R.id.address);
            view2.setText(address);
            TextView view3 = (TextView)findViewById(R.id.phoneNumber);
            view3.setText(phoneNumber);
            TextView view4 = (TextView)findViewById(R.id.website);
            view4.setText(website);

            ImageView imageView = (ImageView)findViewById(R.id.info_image);
            Picasso.with(this)
                    .load(logo)
                    .into(imageView);
        }

        Button button = (Button)findViewById(R.id.mapButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Info.this, Map.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
