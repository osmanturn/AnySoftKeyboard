package com.yek.keyboard.ui.fragment.settings;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anysoftkeyboard.ui.settings.setup.SetupSupport;
import com.yek.keyboard.R;

/**
 * Created by banksy on 15.01.2017.
 */

public class TutorialEnabledKeyboardFragment extends Fragment {

    public static void getInstance(FragmentManager fm) {
        TutorialEnabledKeyboardFragment fragment = (TutorialEnabledKeyboardFragment) fm.findFragmentByTag("TutorialEnabledKeyboardFragment");
        if (fragment == null) {
            fragment = new TutorialEnabledKeyboardFragment();
            fm.beginTransaction().replace(R.id.containter, fragment, "TutorialEnabledKeyboardFragment").commit();
        } else {
            fm.beginTransaction().attach(fragment).commit();
        }
    }


    private static final int KEY_MESSAGE_UNREGISTER_LISTENER = 447;
    private static final int KEY_MESSAGE_RETURN_TO_APP = 446;

    @SuppressWarnings("HandlerLeak"/*I want this fragment to stay in memory as long as possible*/)
    private Handler mGetBackHereHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case KEY_MESSAGE_RETURN_TO_APP:
                    if (mReLaunchTaskIntent != null && mBaseContext != null) {
                        mBaseContext.startActivity(mReLaunchTaskIntent);
                        mReLaunchTaskIntent = null;
                    }
                    break;
                case KEY_MESSAGE_UNREGISTER_LISTENER:
                    unregisterSettingsObserverNow();
                    break;
            }
        }
    };

    private final ContentObserver mSecureSettingsChanged = new ContentObserver(null) {
        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override
        public void onChange(boolean selfChange) {
            if (!isResumed()) {
                if (isStepCompleted()) {
                    mGetBackHereHandler.removeMessages(KEY_MESSAGE_RETURN_TO_APP);
                    mGetBackHereHandler.sendMessageDelayed(mGetBackHereHandler.obtainMessage(KEY_MESSAGE_RETURN_TO_APP), 50/*enough for the user to see what happened.*/);
                }
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial_enabled_keyboard, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        mBaseContext = activity.getBaseContext();
        mReLaunchTaskIntent = activity.getPackageManager().getLaunchIntentForPackage(activity.getPackageName());
        mAppContext = getActivity().getApplicationContext();
        mAppContext.getContentResolver().registerContentObserver(Settings.Secure.CONTENT_URI, true, mSecureSettingsChanged);


        getView().findViewById(R.id.btnSetupWizard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //but I don't want to listen for changes for ever!
                //If the user is taking too long to change one checkbox, I say forget about it.
                mGetBackHereHandler.removeMessages(KEY_MESSAGE_UNREGISTER_LISTENER);
                mGetBackHereHandler.sendMessageDelayed(mGetBackHereHandler.obtainMessage(KEY_MESSAGE_UNREGISTER_LISTENER),
                        45 * 1000/*45 seconds to change a checkbox is enough. After that, I wont listen to changes anymore.*/);
                Intent startSettings = new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS);
                startSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    mAppContext.startActivity(startSettings);
                } catch (ActivityNotFoundException notFoundEx) {
                    //weird.. the device does not have the IME setting activity. Nook?
                    Toast.makeText(mAppContext, R.string.setup_wizard_step_one_action_error_no_settings_activity, Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private Context mBaseContext = null;
    private Intent mReLaunchTaskIntent = null;
    private Context mAppContext;


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterSettingsObserverNow();
    }


    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        if (isStepCompleted())
            TutorialSwitchKeyboardFragment.getInstance(getActivity().getSupportFragmentManager());
    }

    protected boolean isStepCompleted() {
        return SetupSupport.isThisKeyboardEnabled(getActivity());
    }

    private void unregisterSettingsObserverNow() {
        mGetBackHereHandler.removeMessages(KEY_MESSAGE_UNREGISTER_LISTENER);
        if (mAppContext != null) {
            mAppContext.getContentResolver().unregisterContentObserver(mSecureSettingsChanged);
            mAppContext = null;
        }
    }

}
