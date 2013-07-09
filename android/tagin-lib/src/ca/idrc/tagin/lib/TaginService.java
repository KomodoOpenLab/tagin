package ca.idrc.tagin.lib;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tagin.Tagin;

public class TaginService {
	
	public static Tagin newInstance() {
		Tagin.Builder builder = new Tagin.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
		return builder.build();
	}

}
