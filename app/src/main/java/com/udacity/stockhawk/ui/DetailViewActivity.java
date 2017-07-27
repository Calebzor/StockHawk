package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;

public class DetailViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        Bundle extras = getIntent().getExtras();
        addFragment(extras);
    }

    private void addFragment(Bundle extras) {
        DetailViewFragment fragment = new DetailViewFragment();
        fragment.setArguments(extras);
        getSupportFragmentManager().beginTransaction().add(R.id.detail_view_fragment_container,
                fragment).commit();
    }
}
