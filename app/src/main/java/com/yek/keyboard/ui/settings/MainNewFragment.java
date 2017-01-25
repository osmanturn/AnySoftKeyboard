package com.yek.keyboard.ui.settings;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yek.keyboard.R;
import com.yek.keyboard.keyboards.AnyKeyboard;
import com.yek.keyboard.keyboards.KeyboardFactory;
import com.yek.keyboard.keyboards.views.DemoAnyKeyboardView;
import com.yek.keyboard.ui.settings.setup.SetupSupport;
import com.yek.keyboard.ui.settings.setup.TutorialEnabledKeyboardFragment;
import com.yek.keyboard.ui.settings.setup.TutorialSwitchKeyboardFragment;

import net.evendanan.chauffeur.lib.experiences.TransitionExperiences;

/**
 * Created by banksy on 14.01.2017.
 */

public class MainNewFragment extends Fragment implements View.OnClickListener {

    public static MainNewFragment getInstance(FragmentManager fm) {
        MainNewFragment fragment = (MainNewFragment) fm.findFragmentByTag("MainNewFragment");
        if (fragment == null) {
            fragment = new MainNewFragment();
            fm.beginTransaction().replace(R.id.containter, fragment, "MainNewFragment").commit();
        } else {
            fm.beginTransaction().attach(fragment).commit();
        }
        return fragment;
    }

    private DemoAnyKeyboardView demoAnyKeyboardView;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        demoAnyKeyboardView = (DemoAnyKeyboardView) getView().findViewById(R.id.idKeyboard);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!SetupSupport.isThisKeyboardEnabled(getActivity())) {
            TutorialEnabledKeyboardFragment.getInstance(getActivity().getSupportFragmentManager());
        } else if (!SetupSupport.isThisKeyboardSetAsDefaultIME(getActivity())) {
            TutorialSwitchKeyboardFragment.getInstance(getActivity().getSupportFragmentManager());
        }
        AnyKeyboard defaultKeyboard = KeyboardFactory.getEnabledKeyboards(getContext()).get(0).createKeyboard(getContext(), getResources().getInteger(R.integer.keyboard_mode_normal));
        defaultKeyboard.loadKeyboard(demoAnyKeyboardView.getThemedKeyboardDimens());
        demoAnyKeyboardView.setKeyboard(defaultKeyboard, null, null);
        demoAnyKeyboardView.setOnClickListener(this);
        getView().findViewById(R.id.txtTheme).setOnClickListener(this);
        getView().findViewById(R.id.txtEffect).setOnClickListener(this);
        getView().findViewById(R.id.txtButtomRow).setOnClickListener(this);
        getView().findViewById(R.id.txtTopRow).setOnClickListener(this);
        getView().findViewById(R.id.txtInterfaceEventMore).setOnClickListener(this);
        getView().findViewById(R.id.txtLanguage).setOnClickListener(this);
        getView().findViewById(R.id.txtSpecialDictionary).setOnClickListener(this);
        getView().findViewById(R.id.txtLanguageEventMore).setOnClickListener(this);
        getView().findViewById(R.id.txtAbout).setOnClickListener(this);
        getActivity().setTitle(R.string.ime_name);
    }


    @Override
    public void onClick(View v) {
        Fragment fragment=null;
        switch (v.getId()) {
            case R.id.idKeyboard:
                fragment=new KeyboardThemeSelectorFragment();
                break;
            case R.id.txtTheme:
                fragment=new KeyboardThemeSelectorFragment();
                break;
            case R.id.txtEffect:
                fragment=new EffectsSettingsFragment();
                break;
            case R.id.txtButtomRow:
                fragment=new AdditionalUiSettingsFragment.BottomRowAddOnBrowserFragment();
                break;
            case R.id.txtTopRow:
                fragment=new AdditionalUiSettingsFragment.TopRowAddOnBrowserFragment();
                break;
            case R.id.txtInterfaceEventMore:
                fragment=new UiTweaksFragment();
                break;
            case R.id.txtLanguage:
                fragment=new KeyboardAddOnBrowserFragment();
                break;
            case R.id.txtSpecialDictionary:
                fragment=new DictionariesFragment();
                break;
            case R.id.txtLanguageEventMore:
                fragment=new AdditionalLanguageSettingsFragment();
                break;
            case R.id.txtAbout:
                fragment=new AboutAnySoftKeyboardFragment();
                break;
        }

        ((MainSettingsActivity) getActivity()).addFragmentToUi(fragment,TransitionExperiences.SUB_ROOT_FRAGMENT_EXPERIENCE_TRANSITION);
    }
}
