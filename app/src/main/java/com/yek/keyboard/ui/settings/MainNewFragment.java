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
        MainNewFragment fragment = (MainNewFragment) fm.findFragmentByTag("MainFragment");
        if (fragment == null) {
            fragment = new MainNewFragment();
            fm.beginTransaction().replace(R.id.containter, fragment, "MainFragment").commit();
        } else {
            fm.beginTransaction().attach(fragment).commit();
        }
        return fragment;
    }

    private DemoAnyKeyboardView demoAnyKeyboardView;
    private AsyncTask<Bitmap, Void, Palette.Swatch> mPaletteTask;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        demoAnyKeyboardView = (DemoAnyKeyboardView) view.findViewById(R.id.idKeyboard);
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

        mPaletteTask = new AsyncTask<Bitmap, Void, Palette.Swatch>() {
            @Override
            protected Palette.Swatch doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                Palette p = Palette.from(bitmap).generate();
                Palette.Swatch highestSwatch = null;
                for (Palette.Swatch swatch : p.getSwatches()) {
                    if (highestSwatch == null || highestSwatch.getPopulation() < swatch.getPopulation())
                        highestSwatch = swatch;
                }
                return highestSwatch;
            }

            @Override
            protected void onPostExecute(Palette.Swatch swatch) {
                super.onPostExecute(swatch);
                if (!isCancelled()) {
                    final View rootView = getView();
                    if (swatch != null && rootView != null) {
                        final int backgroundRed = Color.red(swatch.getRgb());
                        final int backgroundGreed = Color.green(swatch.getRgb());
                        final int backgroundBlue = Color.blue(swatch.getRgb());
                        final int backgroundColor = Color.argb(200/*~80% alpha*/, backgroundRed, backgroundGreed, backgroundBlue);
                        TextView gplusLink = (TextView) rootView.findViewById(R.id.txtTitle);
                        gplusLink.setTextColor(getResources().getColor(R.color.colorAccent));
                        gplusLink.setBackgroundColor(backgroundColor);
                    }
                }
            }
        };

        demoAnyKeyboardView.startPaletteTask(mPaletteTask);


        //TextView keyboardsData = (TextView) getView().findViewById(R.id.txtDescLanguage);
        //final int all = KeyboardFactory.getAllAvailableKeyboards(getActivity().getApplicationContext()).size();
        //final int enabled = KeyboardFactory.getEnabledKeyboards(getActivity().getApplicationContext()).size();
        //keyboardsData.setText(getString(R.string.keyboards_group_extra_template, enabled, all));
        getView().findViewById(R.id.cardLanguage).setOnClickListener(this);
        getView().findViewById(R.id.changeTheme).setOnClickListener(this);
        getView().findViewById(R.id.txtSpecialDictional).setOnClickListener(this);
        getView().findViewById(R.id.txtEffect).setOnClickListener(this);
        getView().findViewById(R.id.txtAbout).setOnClickListener(this);
        getActivity().setTitle(R.string.ime_name);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changeTheme:
                ((MainSettingsActivity) getActivity()).addFragmentToUi(new KeyboardThemeSelectorFragment(), TransitionExperiences.SUB_ROOT_FRAGMENT_EXPERIENCE_TRANSITION);
                //((MainActivity) getActivity()).addFragmentToUi(new KeyboardUiThemeFragment(), TransitionExperiences.SUB_ROOT_FRAGMENT_EXPERIENCE_TRANSITION);

                break;
            case R.id.cardLanguage:
                //   ((MainActivity) getActivity()).addFragmentToUi(new KeyboardAddOnSettingsFragment(), TransitionExperiences.SUB_ROOT_FRAGMENT_EXPERIENCE_TRANSITION);
                ((MainSettingsActivity) getActivity()).addFragmentToUi(new KeyboardAddOnBrowserFragment(), TransitionExperiences.SUB_ROOT_FRAGMENT_EXPERIENCE_TRANSITION);
                break;
            case R.id.txtSpecialDictional:

                ((MainSettingsActivity) getActivity()).addFragmentToUi(new DictionariesFragment(), TransitionExperiences.SUB_ROOT_FRAGMENT_EXPERIENCE_TRANSITION);

                break;
            case R.id.txtEffect:
                ((MainSettingsActivity) getActivity()).addFragmentToUi(new EffectsSettingsFragment(), TransitionExperiences.SUB_ROOT_FRAGMENT_EXPERIENCE_TRANSITION);

                break;
            case R.id.txtAbout:
                ((MainSettingsActivity) getActivity()).addFragmentToUi(new AboutAnySoftKeyboardFragment(), TransitionExperiences.SUB_ROOT_FRAGMENT_EXPERIENCE_TRANSITION);

                break;

        }
    }
}
