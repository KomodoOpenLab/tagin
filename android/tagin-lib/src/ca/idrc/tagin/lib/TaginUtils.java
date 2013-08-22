package ca.idrc.tagin.lib;

import java.io.IOException;

import com.google.api.client.json.gson.GsonFactory;

import android.util.Log;

public class TaginUtils {
	
	public static <T> T deserialize(String result, Class<T> clazz) {
		T container = null;
		if (result != null) {
			try {
				container = new GsonFactory().fromString(result, clazz);
			} catch (IOException e) {
				Log.e(TaginManager.TAG, "Deserialization error: " + e.getMessage());
			}
		}
		return container;
	}

}
