package com.zayn.swiprefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.zayn.loadingview.library.LoadingLayout;
import com.zayn.loadingview.library.StateViewHolder;


/**
 * 简单的loading state的情况
 */
public class CustomLoadingActivity extends AppCompatActivity {

    LoadingLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_loading);
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


    public void customLoading(MenuItem item) {
        StateViewHolder stateViewHolder = loadingLayout.getStateViewHolder();
        stateViewHolder.setLoadingView(R.layout.custom_loading);
        stateViewHolder.setEmptyView(R.layout.custom_empty);
        stateViewHolder.setErrorView(R.layout.custom_error);
        loadingLayout.startLoading();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.custom_loading, menu);
        return super.onCreateOptionsMenu(menu);
    }
}