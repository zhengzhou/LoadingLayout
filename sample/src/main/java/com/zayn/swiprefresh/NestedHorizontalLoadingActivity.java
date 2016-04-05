package com.zayn.swiprefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zayn.loadingview.library.LoadingLayout;

// TODO: 16-4-5 todo
public class NestedHorizontalLoadingActivity extends AppCompatActivity {

    LoadingLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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