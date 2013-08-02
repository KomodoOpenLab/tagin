package ca.idrc.tagin.cloud.util;

import ca.idrc.tagin.cloud.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;


public class TagAdderDialog extends AlertDialog {
	
	private AlertDialog mLauncherDialog;
	private Activity mContext;
	
	public TagAdderDialog(Activity context) {
		super(context);
		mContext = context;
	}

	public void showDialog() {
		TagAdderBuilder builder = new TagAdderBuilder(mContext);
		mLauncherDialog = builder.create();
		mLauncherDialog.show();
	}
	
	public void dismissDialog() {
		mLauncherDialog.dismiss();
	}
	
	
	private class TagAdderBuilder extends AlertDialog.Builder {
		
		public TagAdderBuilder(Activity context) {
			super(context);
			setView(mContext.getLayoutInflater().inflate(R.layout.dialog_tag_adder, null));

			setCancelable(true);
			setTitle("Add a new tag");
			setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			
			setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}

}