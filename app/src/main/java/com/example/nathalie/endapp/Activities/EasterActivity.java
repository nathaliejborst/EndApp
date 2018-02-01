package com.example.nathalie.endapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.nathalie.endapp.R;
import com.koushikdutta.ion.Ion;

public class EasterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easter);

        // Shows gif when opening activity
        ImageView gif = (ImageView) findViewById(R.id.gif_iv);
        Ion.with(gif).load("https://media.giphy.com/media/PfHrNe1cSKAjC/giphy.gif");
    }
}
