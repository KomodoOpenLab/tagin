package ca.idrc.tagin.cloud.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import ca.idrc.tagin.cloud.R;


public class TagAdderDialog extends AlertDialog {
	
	private TextView mURNTextView;
	private EditText mLabelEditText;
	private AlertDialog mLauncherDialog;
	private Activity mContext;
	
	public TagAdderDialog(Activity context) {
		super(context);
		mContext = context;
	}

	public void showDialog() {
		TagAdderBuilder builder = new TagAdderBuilder(mContext);
		mLauncherDialog = builder.create();
		mURNTextView = builder.getURNTextView();
		mLabelEditText = builder.getLabelEditText();
		mLauncherDialog.show();
	}
	
	public void dismissDialog() {
		mLauncherDialog.dismiss();
	}
	
	public TextView getURNTextView() {
		return mURNTextView;
	}
	
	public EditText getLabelEditText() {
		return mLabelEditText;
	}
	
	
	private class TagAdderBuilder extends AlertDialog.Builder {
		
		private final View mView;

		public TagAdderBuilder(Activity context) {
			super(context);
			mView = mContext.getLayoutInflater().inflate(R.layout.dialog_tag_adder, null);
			setView(mView);

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
		
		public TextView getURNTextView() {
			return (TextView) mView.findViewById(R.id.tv_urn_value);
		}
		
		public EditText getLabelEditText() {
			return (EditText) mView.findViewById(R.id.txt_urn_label);
		}
	}

}