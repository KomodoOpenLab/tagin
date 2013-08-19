package ca.idrc.tagin.cloud;

import android.app.Application;
import ca.idrc.tagin.cloud.util.Persistence;

public class TaginCloudApp extends Application {
	
	public static final String APP_TAG = "tagin-cloud";
	
	public static Persistence persistence;
	private static TaginCloudApp instance;
	
	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}
	
	private void init() {
		instance = this;
		persistence = new Persistence(this);
	}

	public static TaginCloudApp getInstance() {
		return instance;
	}

}
