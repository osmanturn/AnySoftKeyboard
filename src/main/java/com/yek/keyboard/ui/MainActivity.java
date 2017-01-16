package com.yek.keyboard.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.yek.keyboard.R;
import com.yek.keyboard.ui.fragment.MainFragment;

import net.evendanan.chauffeur.lib.FragmentChauffeurActivity;

/**
 * Created by banksy on 14.01.2017.
 */

public class MainActivity extends FragmentChauffeurActivity {
    MainFragment fragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if (savedInstanceState==null)
            fragment=  MainFragment.getInstance(getSupportFragmentManager());

    }

    @Override
    protected int getFragmentRootUiElementId() {
        return R.id.containter;
    }

    @NonNull
    @Override
    protected Fragment createRootFragmentInstance() {
        return fragment;
    }
}
