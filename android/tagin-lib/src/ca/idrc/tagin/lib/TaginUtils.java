package ca.idrc.tagin.lib;

import java.io.IOException;

import com.google.api.client.json.gson.GsonFactory;

import android.util.Log;

public class TaginUtils {
	
	public static <T> T deserialize(String result, Class<T> clazz) {
		T taginObject = null;
		if (result != null) {
			try {
				taginObject = new GsonFactory().fromString(result, clazz);
			} catch (IOException e) {
				Log.e(TaginManager.TAG, "Deserialization error: " + e.getMessage());
			}
		}
		return taginObject;
	}

}
