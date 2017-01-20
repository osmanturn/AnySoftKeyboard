package com.yek.keyboard.ui.fragment.settings;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.anysoftkeyboard.ui.settings.setup.SetupSupport;
import com.yek.keyboard.R;
import com.yek.keyboard.ui.fragment.MainFragment;

/**
 * Created by banksy on 15.01.2017.
 */


public class TutorialSwitchKeyboardFragment extends Fragment {

    public static void getInstance(FragmentManager fm) {
        TutorialSwitchKeyboardFragment fragment = (TutorialSwitchKeyboardFragment) fm.findFragmentByTag("TutorialSwitchKeyboardFragment");
        if (fragment == null) {
            fragment = new TutorialSwitchKeyboardFragment();
            fm.beginTransaction().replace(R.id.containter, fragment, "TutorialSwitchKeyboardFragment").commit();
        } else
            fm.beginTransaction().attach(fragment).commit();
    }

    private Context mAppContext;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_switch_keyboard_action,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppContext=getActivity().getApplicationContext();
        mAppContext.getContentResolver().registerContentObserver(Settings.Secure.CONTENT_URI, true, mSecureSettingsChanged);
        getView().findViewById(R.id.btnSetupSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showInputMethodPicker();
            }
        });


    }

    private final ContentObserver mSecureSettingsChanged = new ContentObserver(null) {
        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override
        public void onChange(boolean selfChange) {
            if (isResumed()) {
                if (isStepPreConditionDone()) {
                    mGetBackHereHandler.removeMessages(KEY_MESSAGE_RETURN_TO_APP);
                    mGetBackHereHandler.sendMessageDelayed(mGetBackHereHandler.obtainMessage(KEY_MESSAGE_RETURN_TO_APP), 50/*enough for the user to see what happened.*/);
                }
            }
        }
    };

    private static final int KEY_MESSAGE_UNREGISTER_LISTENER = 447;
    private static final int KEY_MESSAGE_RETURN_TO_APP = 446;

    @SuppressWarnings("HandlerLeak"/*I want this fragment to stay in memory as long as possible*/)
    private Handler mGetBackHereHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case KEY_MESSAGE_RETURN_TO_APP:
                    if (isStepPreConditionDone())
                        MainFragment.getInstance(getActivity().getSupportFragmentManager());
                    break;
                case KEY_MESSAGE_UNREGISTER_LISTENER:
                    unregisterSettingsObserverNow();
                    break;
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();

        if (isStepPreConditionDone())
            MainFragment.getInstance(getActivity().getSupportFragmentManager());
    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    protected boolean isStepPreConditionDone() {
        return SetupSupport.isThisKeyboardSetAsDefaultIME(getActivity());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterSettingsObserverNow();
    }

    private void unregisterSettingsObserverNow() {
        //mGetBackHereHandler.removeMessages(KEY_MESSAGE_UNREGISTER_LISTENER);
        if (mAppContext != null) {
            mAppContext.getContentResolver().unregisterContentObserver(mSecureSettingsChanged);
            mAppContext = null;
        }
    }
}
