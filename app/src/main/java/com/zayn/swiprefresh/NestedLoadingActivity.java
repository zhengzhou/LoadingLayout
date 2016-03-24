package com.zayn.swiprefresh;

import android.os.Bundle;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zayn.library.LoadingLayout;
import com.zayn.library.NestedLoadingLayout;

public class NestedLoadingActivity extends AppCompatActivity {

    NestedLoadingLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested);
        loadingLayout = (NestedLoadingLayout) findViewById(R.id.loadLayout);
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