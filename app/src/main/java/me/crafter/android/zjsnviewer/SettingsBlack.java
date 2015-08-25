package me.crafter.android.zjsnviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class SettingsBlack extends PreferenceActivity {

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupToolBar();
        setupSimplePreferencesScreen();
        registerListener(getApplicationContext());
    }

    private void setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.xml.pref_shared_empty);
        // Add 'black' preferences.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_black);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_part_black);
        bindPreferenceSummaryToValue(findPreference("alt_url_login"));
        bindPreferenceSummaryToValue(findPreference("alt_url_server"));
    }

    // Listener
    private void registerListener(final Context context){
        // TODO change
        // Ref: http://stackoverflow.com/questions/2542938/sharedpreferences-onsharedpreferencechangelistener-not-being-called-consistently
//        findPreference("black").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference,Object newValue) {
//                if ((Boolean)newValue){
//                    Toast.makeText(context, context.getResources().getString(R.string.black_warning), Toast.LENGTH_SHORT).show();
//                } else {
//                    //PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("root", false).commit();
//                    ((SwitchPreference)findPreference("root")).setChecked(false);
//                }
//                return true;
//            }
//        });
        findPreference("root").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,Object newValue) {
                if ((Boolean)newValue){
                    Toast.makeText(context, context.getResources().getString(R.string.root_please), Toast.LENGTH_SHORT).show();
                    if (Worker.testSuperUser(context)){
                        Toast.makeText(context, context.getResources().getString(R.string.root_success), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.root_fail), Toast.LENGTH_SHORT).show();
                        // TODO not working
                        ((SwitchPreference)preference).setChecked(false);
                    }
                }
                return true;
            }
        });
//        findPreference("black").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            public boolean onPreferenceClick(Preference preference) {
//                if (Storage.black(context)) {
//                    Toast.makeText(context, context.getResources().getString(R.string.black_warning), Toast.LENGTH_SHORT).show();
//                } else {
//                    //PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("root", false).commit();
//                    ((SwitchPreference)findPreference("root")).setChecked(false);
//                }
//                return true;
//            }
//        });
//        findPreference("root").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            public boolean onPreferenceClick(Preference preference) {
//                if (Storage.root(context)){
//                    Toast.makeText(context, context.getResources().getString(R.string.root_please), Toast.LENGTH_SHORT).show();
//                    if (Worker.testSuperUser(context)){
//                        Toast.makeText(context, context.getResources().getString(R.string.root_success), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(context, context.getResources().getString(R.string.root_fail), Toast.LENGTH_SHORT).show();
//                        ((SwitchPreference)findPreference("root")).setChecked(false);
//                    }
//                }
//                return true;
//            }
//        });
    }

    private void setupToolBar(){
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.pref_shared_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            }
            else if (preference instanceof RingtonePreference) {
                if (TextUtils.isEmpty(stringValue)) {
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        preference.setSummary(null);
                    } else {
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }
            }
            else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
}
