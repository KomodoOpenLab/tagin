package ca.idrc.tagin.lib.tags;

import java.util.List;

public interface GetLabelsTaskListener {
	
	public void onGetLabelsTaskComplete(String urn, List<String> labels);

}
