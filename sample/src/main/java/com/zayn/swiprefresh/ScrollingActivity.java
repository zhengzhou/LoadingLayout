package com.zayn.swiprefresh;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;

import com.zayn.loadingview.library.NestedLoadingLayout;
import com.zayn.loadingview.ui.LoadingSwipeListener;

public class ScrollingActivity extends AppCompatActivity {

    NestedLoadingLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
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


        loadingLayout = (NestedLoadingLayout) findViewById(R.id.loadLayout);
        loadingLayout.setContentScrollEnable(false);
        LoadingSwipeListener loadingSwipeListener = new LoadingSwipeListener();
        loadingSwipeListener.bindPullLoadView(loadingLayout, Gravity.START);
    }
}
