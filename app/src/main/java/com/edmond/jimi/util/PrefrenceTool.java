package com.edmond.jimi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefrenceTool {

	public PrefrenceTool() {
	}

	public static int getValue(String cfgFile, String cfgName, Context context) {
		SharedPreferences preference = context.getSharedPreferences(cfgFile,
				Context.MODE_PRIVATE);
		return preference.getInt(cfgName, -1);
	}

	public static String getStringValue(String cfgFile, String cfgName,
			Context context) {
		SharedPreferences preference = context.getSharedPreferences(cfgFile,
				Context.MODE_PRIVATE);
		return preference.getString(cfgName, "");
	}

	public static long getLongValue(String cfgFile, String cfgName,
			Context context) {
		SharedPreferences preference = context.getSharedPreferences(cfgFile,
				Context.MODE_PRIVATE);
		return preference.getLong(cfgName, -1);
	}

	public static void saveValue(String cfgFile, String cfgName, int cfgValue,
			Context context) {
		SharedPreferences preference = context.getSharedPreferences(cfgFile,
				Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putInt(cfgName, cfgValue);
		editor.commit();
	}

	public static void saveValue(String cfgFile, String cfgName, long cfgValue,
			Context context) {
		SharedPreferences preference = context.getSharedPreferences(cfgFile,
				Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putLong(cfgName, cfgValue);
		editor.commit();
	}

	public static void saveValue(String cfgFile, String cfgName,
			boolean cfgValue, Context context) {
		SharedPreferences preference = context.getSharedPreferences(cfgFile,
				Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putBoolean(cfgName, cfgValue);
		editor.commit();
	}

	public static void saveValue(String cfgFile, String cfgName,
			float cfgValue, Context context) {
		SharedPreferences preference = context.getSharedPreferences(cfgFile,
				Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putFloat(cfgName, cfgValue);
		editor.commit();
	}

	public static void saveValue(String cfgFile, String cfgName,
			String cfgValue, Context context) {
		SharedPreferences preference = context.getSharedPreferences(cfgFile,
				Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putString(cfgName, cfgValue);
		editor.commit();
	}

	public static void removeValue(String cfgFile, String cfgName,
			Context context) {
		SharedPreferences preference = context.getSharedPreferences(cfgFile,
				Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.remove(cfgName);
		editor.commit();
	}

	public static void removeValues(String cfgFile, String[] cfgNames,
			Context context) {
		SharedPreferences preference = context.getSharedPreferences(cfgFile,
				Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		for (String name : cfgNames) {
			editor.remove(name);
		}
		editor.commit();
	}
}
