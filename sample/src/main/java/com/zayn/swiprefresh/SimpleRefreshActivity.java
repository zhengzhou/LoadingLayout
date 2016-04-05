package com.zayn.swiprefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.zayn.loadingview.library.NestedLoadingLayout;

public class SimpleRefreshActivity extends AppCompatActivity {

    NestedLoadingLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_refresh);
        loadingLayout = (NestedLoadingLayout) findViewById(R.id.loadLayout);
        RadioGroup group = (RadioGroup) findViewById(R.id.radio);
        if(group != null)
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(group.getCheckedRadioButtonId() == R.id.radioScroll){
                    loadingLayout.setContentScrollEnable(true);
                }else {
                    loadingLayout.setContentScrollEnable(false);
                }
            }
        });
    }

}