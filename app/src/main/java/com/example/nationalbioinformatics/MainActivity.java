package com.example.nationalbioinformatics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
Button bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    init();
    bt.setOnClickListener(this);
    }

    private void init() {
    bt=findViewById(R.id.bt);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.bt:
                startActivity(new Intent(this,MapsActivity.class));
                break;
        }
    }
}
