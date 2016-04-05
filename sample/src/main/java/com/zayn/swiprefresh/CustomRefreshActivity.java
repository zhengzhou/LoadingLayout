package com.zayn.swiprefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;

import com.zayn.loadingview.library.NestedLoadingLayout;
import com.zayn.loadingview.ui.LoadingSwipeListener;

public class CustomRefreshActivity extends AppCompatActivity {

    NestedLoadingLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customed_refresh);
        loadingLayout = (NestedLoadingLayout) findViewById(R.id.loadLayout);
        loadingLayout.setContentScrollEnable(false);
        LoadingSwipeListener loadingSwipeListener = new LoadingSwipeListener();
        loadingSwipeListener.bindPullLoadView(loadingLayout, Gravity.START);


    }

}