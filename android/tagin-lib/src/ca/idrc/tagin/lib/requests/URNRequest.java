package ca.idrc.tagin.lib.requests;

import java.io.IOException;

import android.util.Log;
import ca.idrc.tagin.lib.TaginManager;
import ca.idrc.tagin.lib.TaginService;

import com.google.api.services.tagin.Tagin;
import com.google.api.services.tagin.model.Pattern;
import com.google.api.services.tagin.model.URN;

public class URNRequest implements TaginApiCall {
	
	private Tagin mTagin;
	private Pattern mPattern;
	
	public URNRequest(Tagin tagin, Pattern pattern) {
		mTagin = tagin;
		mPattern = pattern;
	}

	@Override
	public String execute() {
		String result = null;
		mPattern.updateRanks();
		try {
			URN urn = mTagin.patterns().add(mPattern).execute();
			result = urn.getValue();
		} catch (IOException e) {
			Log.e(TaginManager.TAG, "Failed to submit fingerprint: " + e.getMessage());
		}
		return result;
	}
	
	@Override
	public String getBroadcastAction() {
		return TaginService.ACTION_URN_READY;
	}

}
