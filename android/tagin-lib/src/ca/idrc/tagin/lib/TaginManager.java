package ca.idrc.tagin.lib;

import android.content.Context;
import android.content.Intent;

public class TaginManager {
	
	private static Context mContext;

	public TaginManager(Context context) {
		mContext = context;
	}
	
	public void requestURN() {
		final Intent intent = new Intent(mContext, TaginService.class);
		intent.putExtra(TaginService.EXTRA_TYPE, TaginService.ACTION_REQUEST_URN);
		mContext.startService(intent);
	}
}
