package com.zayn.swiprefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.RadioGroup;

import com.zayn.loadingview.library.NestedLoadingLayout;
import com.zayn.loadingview.ui.LoadingSwipeListener;

public class CustomNestedLoadingActivity extends AppCompatActivity {

    NestedLoadingLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customed_nested);
        loadingLayout = (NestedLoadingLayout) findViewById(R.id.loadLayout);

        LoadingSwipeListener loadingSwipeListener = new LoadingSwipeListener();
        loadingSwipeListener.bindPullLoadView(loadingLayout, Gravity.START);
    }

}