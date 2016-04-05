package com.zayn.swiprefresh;

import android.content.Intent;
import android.os.Bundle;
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
    }

    public void gotoSimpleLoading(View view) {
        startActivity(new Intent(this, SimpleLoadingActivity.class));
    }

    public void gotoCustomLoading(View view) {
        startActivity(new Intent(this, CustomLoadingActivity.class));
    }

    public void gotoSimpleRefresh(View view) {
        startActivity(new Intent(this, SimpleRefreshActivity.class));
    }

    public void gotoCustomRefresh(View view) {
        startActivity(new Intent(this, CustomRefreshActivity.class));
    }

    public void gotoNestedRefresh(View view) {
        startActivity(new Intent(this, NestedParentActivity.class));
    }
}
