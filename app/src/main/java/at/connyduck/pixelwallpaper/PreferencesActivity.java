package at.connyduck.pixelwallpaper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

@SuppressWarnings("deprecation")
public class PreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    public static final String PREF_BLACK_WHITE = "pref_black_white";
    public static final String PREF_TOUCH = "pref_touch";
    public static final String PREF_PIXEL = "pref_pixel";
    public static final String PREF_FRAME = "pref_frame";
    public static final String PREF_OPEN_SOURCE = "pref_os";
	public static final String PREF_BORDER = "pref_border";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		prefs.registerOnSharedPreferenceChangeListener(this);

        findPreference(PREF_OPEN_SOURCE).setOnPreferenceClickListener(this);

        refreshView();

    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        refreshView();
	}

	private void refreshView() {

        Resources res = getResources();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);


        boolean blackWhite = prefs.getBoolean(PREF_BLACK_WHITE, true);

        if(blackWhite) {
            findPreference(PREF_BLACK_WHITE).setSummary(R.string.pref_black_white_summary);

            ((ListPreference)findPreference(PREF_TOUCH)).setEntries(R.array.pref_touch_entries);
            ((ListPreference)findPreference(PREF_TOUCH)).setEntryValues(R.array.pref_touch_values);

            if(getIndexOf(prefs.getString(PREF_TOUCH, res.getString(R.string.pref_touch_default)), res.getStringArray(R.array.pref_touch_values)) == -1) {
                ((ListPreference)findPreference(PREF_TOUCH)).setValue(res.getString(R.string.pref_touch_default));
            }

        } else {
            findPreference(PREF_BLACK_WHITE).setSummary(R.string.pref_black_white_colorful_summary);
            ((ListPreference)findPreference(PREF_TOUCH)).setEntries(R.array.pref_touch_entries_color);
            ((ListPreference)findPreference(PREF_TOUCH)).setEntryValues(R.array.pref_touch_values_color);

        }

        String touchmode = prefs.getString(PREF_TOUCH, res.getString(R.string.pref_touch_default));
        String pixelsize = prefs.getString(PREF_PIXEL, res.getString(R.string.pref_pixel_default));
        String framerate = prefs.getString(PREF_FRAME, res.getString(R.string.pref_frame_default));

        int touchmodeIndex = getIndexOf(touchmode, res.getStringArray(R.array.pref_touch_values_color));
        int pixelsizeIndex = getIndexOf(pixelsize, res.getStringArray(R.array.pref_pixel_values));
        int framerateIndex = getIndexOf(framerate, res.getStringArray(R.array.pref_frame_values));

        findPreference(PREF_TOUCH).setSummary(res.getStringArray(R.array.pref_touch_entries_color)[touchmodeIndex]);
        findPreference(PREF_PIXEL).setSummary(res.getStringArray(R.array.pref_pixel_entries)[pixelsizeIndex]);
        findPreference(PREF_FRAME).setSummary(res.getStringArray(R.array.pref_frame_entries)[framerateIndex]);

    }

	
	private int getIndexOf(String a, String[] b) {
		for(int i=0;i<b.length; i++) {
			if(b[i].equals(a))
				return i;
		}
		
		return -1;
	}

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(PREF_OPEN_SOURCE)) {
            Uri uri = Uri.parse("https://github.com/connyduck/pixelwallpaper");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }
        return false;
    }
}