package at.connyduck.pixelwallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class PersistenceManager {

	private static final String fileID = "at.connyduck.pixelwallpaper.save";
	private static final String preferenceID = "at.connyduck.pixelwallpaper prefs";

	private Context context;

	private ArrayList<IntList> colors;
	
	private int maxPixelsPerRow;
	private int maxPixelsPerColumn;

	private int colorCount;
	private int nextColor;
	private int colorDirection;

	private int brightnessCount;
	private int nextBrightness;

	public  PersistenceManager(Context c) {
		this.context = c;	
	}

	@SuppressWarnings("unchecked")
	public boolean load() {

		colors = null;

		FileInputStream fis;
		
		try {
			fis = context.openFileInput(fileID);
		} catch (FileNotFoundException e) {
			//first time load
			Log.i("PersistenceManager", Log.getStackTraceString(e));
			return false;
		}
		
		if(fis!=null) {
			ObjectInputStream i;
			try {
				i = new ObjectInputStream( fis );

				colors = (ArrayList<IntList>)i.readObject();

				i.close();
				fis.close();

			} catch (IOException | ClassNotFoundException e) {
				Log.w("PersistenceManager", Log.getStackTraceString(e));
				return false;
			}
		}

		SharedPreferences pref = context.getSharedPreferences(preferenceID, 0);

		maxPixelsPerRow = pref.getInt("mpprPref", 0);
		maxPixelsPerColumn = pref.getInt("mppcPref", 0);
		colorCount = pref.getInt("colorCountPref", 0);
		nextColor = pref.getInt("nextColorPref", 0);
		colorDirection = pref.getInt("colorDirectionPref", 1);
		brightnessCount = pref.getInt("brightnessCountPref", 0);
		nextBrightness = pref.getInt("nextBrightnessPref", 0);
		
		return true;
		
	}

	public void save() {
		
		FileOutputStream fos = null;
		
		try {
			fos = context.openFileOutput(fileID, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			Log.w("PersistenceManager", Log.getStackTraceString(e));
		}
		
		ObjectOutputStream o;
		try {
			
			o = new ObjectOutputStream(fos);

			o.writeObject(colors);
			
			o.close();
			if (fos != null) {
				fos.close();
			}

		} catch (IOException e) {
			Log.w("PersistenceManager", Log.getStackTraceString(e));
		}

		SharedPreferences.Editor editor = context.getSharedPreferences(preferenceID, 0).edit();

		editor.putInt("mpprPref", maxPixelsPerRow);
		editor.putInt("mppcPref", maxPixelsPerColumn);
		editor.putInt("colorCountPref", colorCount);
		editor.putInt("nextColorPref", nextColor);
		editor.putInt("colorDirectionPref", colorDirection);
		editor.putInt("brightnessCountPref", brightnessCount);
		editor.putInt("nextBrightnessPref", nextBrightness);

		editor.commit();
	}

	public void setAll(ArrayList<IntList> colors, int maxPixelsPerRow, int maxPixelsPerColumn, int colorCount, int nextColor, int colorDirection, int brightnessCount, int nextBrightness) {
		this.colors = colors;
		this.maxPixelsPerRow = maxPixelsPerRow;
		this.maxPixelsPerColumn = maxPixelsPerColumn;
		this.colorCount = colorCount;
		this.nextColor = nextColor;
		this.colorDirection = colorDirection;
		this.brightnessCount = brightnessCount;
		this.nextBrightness = nextBrightness;
	}

	public ArrayList<IntList> getColors() {
		return colors;
	}

	public int getColorCount() {
		return colorCount;
	}

	public int getNextColor() {
		return nextColor;
	}

	public int getBrightnessCount() {
		return brightnessCount;
	}

	public int getNextBrightness() {
		return nextBrightness;
	}

	public int getColorDirection() {
		return colorDirection;
	}

	public int getMaxPixelsPerRow() {
		return maxPixelsPerRow;
	}
	
	public int getMaxPixelsPerColumn() {
		return maxPixelsPerColumn;
	}

}