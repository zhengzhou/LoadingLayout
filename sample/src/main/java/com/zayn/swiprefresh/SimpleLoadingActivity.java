package com.zayn.swiprefresh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zayn.loadingview.library.LoadingLayout;

public class SimpleLoadingActivity extends AppCompatActivity {

    LoadingLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_loading);
        loadingLayout = (LoadingLayout) findViewById(R.id.loadLayout);

    }

    public void startLoad(View view) {
        loadingLayout.startLoading();
    }

    public void loadEmpty(View view) {
        loadingLayout.empty();
    }

    public void loadError(View view) {
        loadingLayout.error();
    }

    public void stopLoad(View view) {
        loadingLayout.stopLoading();
    }
}