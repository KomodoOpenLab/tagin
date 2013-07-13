package ca.idrc.tagin.lib.requests;

import java.io.IOException;

import android.util.Log;
import ca.idrc.tagin.lib.TaginManager;
import ca.idrc.tagin.lib.TaginService;

import com.google.api.services.tagin.Tagin;
import com.google.api.services.tagin.model.URNCollection;

public class FindNeighboursRequest implements TaginApiCall {
	
	private Tagin mTagin;
	private String mUrn;
	
	public FindNeighboursRequest(Tagin tagin, String urn) {
		mTagin = tagin;
		mUrn = urn;
	}

	@Override
	public String execute() {
		String result = null;
		try {
			URNCollection urns = mTagin.urns().neighbours(mUrn).execute();
			result = urns.toString();
		} catch (IOException e) {
			Log.e(TaginManager.TAG, "Failed to find neighbours: " + e.getMessage());
		}
		return result;
	}

	@Override
	public String getBroadcastAction() {
		return TaginService.ACTION_NEIGHBOURS_READY;
	}

}
