/*
 * Copyright (c) 2013 Menny Even-Danan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yek.keyboard.ui.settings;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import com.yek.keyboard.R;
import com.yek.keyboard.anysoftkeyboard.PermissionsRequestCodes;

import net.evendanan.chauffeur.lib.permissions.PermissionsFragmentChauffeurActivity;
import net.evendanan.chauffeur.lib.permissions.PermissionsRequest;
import net.evendanan.pushingpixels.EdgeEffectHacker;

import java.lang.ref.WeakReference;

public class MainSettingsActivity extends PermissionsFragmentChauffeurActivity {

    public static final String EXTRA_KEY_APP_SHORTCUT_ID = "shortcut_id";


    private CharSequence mTitle;
    private CharSequence mDrawerTitle;


    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        mTitle = mDrawerTitle = getTitle();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.

        //applying my very own Edge-Effect color
        EdgeEffectHacker.brandGlowEffect(this, ContextCompat.getColor(this, R.color.app_accent));
        handleAppShortcuts(getIntent());
    }

    private void handleAppShortcuts(Intent intent) {
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction()) && intent.hasExtra(EXTRA_KEY_APP_SHORTCUT_ID)) {
            final String shortcutId = intent.getStringExtra(EXTRA_KEY_APP_SHORTCUT_ID);
            intent.removeExtra(EXTRA_KEY_APP_SHORTCUT_ID);

            switch (shortcutId) {
                case "keyboards":
                  //  onNavigateToKeyboardAddonSettings(null);
                    break;
                case "themes":
                   // onNavigateToKeyboardThemeSettings(null);
                    break;
                case "gestures":
                  //  onNavigateToGestureSettings(null);
                    break;
                case "quick_keys":
                   // onNavigateToQuickTextSettings(null);
                    break;
                case "ui_tweaks":
                    //onNavigateToUserInterfaceSettings(null);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown app-shortcut "+shortcutId);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleAppShortcuts(intent);
    }

    @NonNull
    @Override
    protected Fragment createRootFragmentInstance() {
        return new MainNewFragment();
    }

    @Override
    protected int getFragmentRootUiElementId() {
        return R.id.containter;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }






    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    //side menu navigation methods



    public void setFullScreen(boolean fullScreen) {
        if (fullScreen) {
            getSupportActionBar().hide();
        } else {
            getSupportActionBar().show();
        }
    }



    /**
     * Will set the title in the hosting Activity's title.
     * Will only set the title if the fragment is hosted by the Activity's manager, and not inner one.
     */
    public static void setActivityTitle(Fragment fragment, CharSequence title) {
        FragmentActivity activity = fragment.getActivity();
        if (activity.getSupportFragmentManager() == fragment.getFragmentManager()) {
            activity.setTitle(title);
        }
    }

    private final DialogInterface.OnClickListener mContactsDictionaryDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, final int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainSettingsActivity.this, Manifest.permission.READ_CONTACTS)) {
                        startContactsPermissionRequest();
                    } else {
                        startAppPermissionsActivity();
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(getString(R.string.settings_key_use_contacts_dictionary), false);
                    SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
                    break;
            }
        }
    };

    private AlertDialog mAlertDialog;

    public void startContactsPermissionRequest() {
        startPermissionsRequest(new ContactPermissionRequest(this));
    }

    @NonNull
    protected PermissionsRequest createPermissionRequestFromIntentRequest(int requestId, @NonNull String[] permissions, @NonNull Intent intent) {
        if (requestId == PermissionsRequestCodes.CONTACTS.getRequestCode()) {
            return new ContactPermissionRequest(this);
        } else {
            return super.createPermissionRequestFromIntentRequest(requestId, permissions, intent);
        }
    }


    private static class ContactPermissionRequest extends PermissionsRequest.PermissionsRequestBase {

        private final WeakReference<MainSettingsActivity> mMainSettingsActivityWeakReference;

        public ContactPermissionRequest(MainSettingsActivity activity) {
            super(PermissionsRequestCodes.CONTACTS.getRequestCode(), Manifest.permission.READ_CONTACTS);
            mMainSettingsActivityWeakReference = new WeakReference<>(activity);
        }
        @Override
        public void onPermissionsGranted() {
            /*
            nothing to do here, it will re-load the contact dictionary next time the
            input-connection will start.
            */
        }

        @Override
        public void onPermissionsDenied(@NonNull String[] grantedPermissions, @NonNull String[] deniedPermissions, @NonNull String[] declinedPermissions) {
            MainSettingsActivity activity = mMainSettingsActivityWeakReference.get();
            if (activity == null) return;
            //if the result is DENIED and the OS says "do not show rationale", it means the user has ticked "Don't ask me again".
            final boolean userSaysDontAskAgain = !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS);
            //the user has denied us from reading the Contacts information.
            //I'll ask them to whether they want to grant anyway, or disable ContactDictionary
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(true);
            builder.setIcon(R.drawable.ic_notification_contacts_permission_required);
            builder.setTitle(R.string.notification_read_contacts_title);
            builder.setMessage(activity.getString(R.string.contacts_permissions_dialog_message));
            builder.setPositiveButton(activity.getString(userSaysDontAskAgain ? R.string.navigate_to_app_permissions : R.string.allow_permission), activity.mContactsDictionaryDialogListener);
            builder.setNegativeButton(activity.getString(R.string.turn_off_contacts_dictionary), activity.mContactsDictionaryDialogListener);

            if (activity.mAlertDialog != null && activity.mAlertDialog.isShowing()) activity.mAlertDialog.dismiss();
            activity.mAlertDialog = builder.create();
            activity.mAlertDialog.show();
        }
    }
}
