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
	private Integer mMaxCount;
	
	public FindNeighboursRequest(Tagin tagin, String urn, String count) {
		mTagin = tagin;
		mUrn = urn;
		mMaxCount = Integer.parseInt(count);
	}

	@Override
	public String execute() {
		String result = null;
		try {
			URNCollection urns = mTagin.urns().neighbours(mUrn, mMaxCount).execute();
			result = urns.toString();
		} catch (IOException e) {
			Log.d(TaginManager.TAG, "Failed to find neighbours: " + e.getMessage());
		}
		return result;
	}

	@Override
	public String getBroadcastAction() {
		return TaginService.ACTION_NEIGHBOURS_READY;
	}

}
