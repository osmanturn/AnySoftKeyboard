package com.yek.keyboard.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anysoftkeyboard.keyboards.KeyboardFactory;
import com.anysoftkeyboard.theme.KeyboardTheme;
import com.anysoftkeyboard.theme.KeyboardThemeFactory;
import com.anysoftkeyboard.ui.settings.setup.SetupSupport;
import com.yek.keyboard.R;
import com.yek.keyboard.ui.MainActivity;
import com.yek.keyboard.ui.fragment.settings.AboutAnySoftKeyboardFragment;
import com.yek.keyboard.ui.fragment.settings.DictionariesFragment;
import com.yek.keyboard.ui.fragment.settings.EffectsSettingsFragment;
import com.yek.keyboard.ui.fragment.settings.KeyboardAddOnSettingsFragment;
import com.yek.keyboard.ui.fragment.settings.KeyboardUiThemeFragment;
import com.yek.keyboard.ui.fragment.settings.TutorialEnabledKeyboardFragment;
import com.yek.keyboard.ui.fragment.settings.TutorialSwitchKeyboardFragment;

import net.evendanan.chauffeur.lib.experiences.TransitionExperiences;

/**
 * Created by banksy on 14.01.2017.
 */

public class MainFragment extends Fragment implements View.OnClickListener {

    public static MainFragment getInstance(FragmentManager fm) {
        MainFragment fragment = (MainFragment) fm.findFragmentByTag("MainFragment");
        if (fragment == null) {
            fragment = new MainFragment();
            fm.beginTransaction().replace(R.id.containter, fragment, "MainFragment").commit();
        } else {
            fm.beginTransaction().attach(fragment).commit();
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!SetupSupport.isThisKeyboardEnabled(getActivity())) {
            TutorialEnabledKeyboardFragment.getInstance(getActivity().getSupportFragmentManager());
        } else if (!SetupSupport.isThisKeyboardSetAsDefaultIME(getActivity())) {
            TutorialSwitchKeyboardFragment.getInstance(getActivity().getSupportFragmentManager());
        } else {
            KeyboardTheme theme = KeyboardThemeFactory.getCurrentKeyboardTheme(getActivity().getApplicationContext());
            if (theme == null)
                theme = KeyboardThemeFactory.getFallbackTheme(getActivity().getApplicationContext());
            Drawable themeScreenShot = theme.getScreenshot();

            if (themeScreenShot == null)
                themeScreenShot = ContextCompat.getDrawable(getActivity(), R.drawable.lean_dark_theme_screenshot);
            ((ImageView) getView().findViewById(R.id.imgCover)).setImageDrawable(themeScreenShot);

            theme = KeyboardThemeFactory.getFallbackTheme(getActivity().getApplicationContext());
           // ((TextView) getView().findViewById(R.id.txtDesc)).setText(getString(R.string.selected_add_on_summary, theme.getName()));
        }

        TextView keyboardsData = (TextView) getView().findViewById(R.id.txtDescLanguage);
        final int all = KeyboardFactory.getAllAvailableKeyboards(getActivity().getApplicationContext()).size();
        final int enabled = KeyboardFactory.getEnabledKeyboards(getActivity().getApplicationContext()).size();
        keyboardsData.setText(getString(R.string.keyboards_group_extra_template, String.valueOf(enabled), String.valueOf(all)));
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
                ((MainActivity) getActivity()).addFragmentToUi(new KeyboardUiThemeFragment(), TransitionExperiences.SUB_ROOT_FRAGMENT_EXPERIENCE_TRANSITION);

                break;
            case R.id.cardLanguage:
                ((MainActivity) getActivity()).addFragmentToUi(new KeyboardAddOnSettingsFragment(), TransitionExperiences.SUB_ROOT_FRAGMENT_EXPERIENCE_TRANSITION);

                break;
            case R.id.txtSpecialDictional:

                ((MainActivity) getActivity()).addFragmentToUi(new DictionariesFragment(), TransitionExperiences.SUB_ROOT_FRAGMENT_EXPERIENCE_TRANSITION);

                break;
            case R.id.txtEffect:
                ((MainActivity) getActivity()).addFragmentToUi(new EffectsSettingsFragment(), TransitionExperiences.SUB_ROOT_FRAGMENT_EXPERIENCE_TRANSITION);

                break;
            case R.id.txtAbout:
                ((MainActivity) getActivity()).addFragmentToUi(new AboutAnySoftKeyboardFragment(), TransitionExperiences.SUB_ROOT_FRAGMENT_EXPERIENCE_TRANSITION);

                break;

        }
    }
}
