package ca.idrc.tagin.lib.requests;

import java.io.IOException;

import android.util.Log;

import com.google.api.services.tagin.Tagin;
import com.google.api.services.tagin.model.FingerprintCollection;

import ca.idrc.tagin.lib.TaginManager;
import ca.idrc.tagin.lib.TaginService;

public class ListFingerprintsRequest implements TaginApiCall {
	
	private Tagin mTagin;
	
	public ListFingerprintsRequest(Tagin tagin) {
		mTagin = tagin;
	}

	@Override
	public String execute() {	
		String result = null;
		try {
			FingerprintCollection fps = mTagin.fingerprints().list().execute();
			result = fps.toString();
		} catch (IOException e) {
			Log.e(TaginManager.TAG, "Failed to list fingerprints: " + e.getMessage());
		}
		return result;
	}

	@Override
	public String getBroadcastAction() {
		return TaginService.ACTION_FINGERPRINTS_READY;
	}

}
