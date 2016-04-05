package com.zayn.swiprefresh;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void gotoSimpleLoading(View view) {
        startActivity(new Intent(this, SimpleLoadingActivity.class));
    }

    public void gotoCustomLoading(View view) {
        startActivity(new Intent(this, CustomLoadingActivity.class));
    }

    public void gotoSimpleRefresh(View view) {
        startActivity(new Intent(this, NestedLoadingActivity.class));
    }

    public void gotoCustomRefresh(View view) {
        startActivity(new Intent(this, CustomNestedLoadingActivity.class));
    }

}
