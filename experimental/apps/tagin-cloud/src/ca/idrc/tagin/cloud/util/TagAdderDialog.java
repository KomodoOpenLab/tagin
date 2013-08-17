package ca.idrc.tagin.cloud.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import ca.idrc.tagin.cloud.CloudActivity;
import ca.idrc.tagin.cloud.R;
import ca.idrc.tagin.cloud.tag.Tag;


public class TagAdderDialog extends AlertDialog {
	
	private TextView mURNTextView;
	private TextView mLabelTextView;
	private EditText mLabelEditText;
	private AlertDialog mLauncherDialog;
	private CloudActivity mContext;
	
	public TagAdderDialog(CloudActivity context) {
		super(context);
		mContext = context;
	}

	public void showDialog() {
		TagAdderBuilder builder = new TagAdderBuilder(mContext);
		mLauncherDialog = builder.create();
		mURNTextView = builder.getURNTextView();
		mLabelTextView = builder.getLabelTextView();
		mLabelEditText = builder.getLabelEditText();
		mLauncherDialog.show();
	}
	
	public void dismissDialog() {
		mLauncherDialog.dismiss();
	}
	
	public TextView getURNTextView() {
		return mURNTextView;
	}
	
	public TextView getLabelTextView() {
		return mLabelTextView;
	}
	
	public EditText getLabelEditText() {
		return mLabelEditText;
	}
	
	
	private class TagAdderBuilder extends AlertDialog.Builder {
		
		private final View mView;
		private TextView mURNTextView;
		private TextView mLabelTextView;
		private EditText mLabelEditText;

		public TagAdderBuilder(Activity context) {
			super(context);
			mView = mContext.getLayoutInflater().inflate(R.layout.dialog_tag_adder, null);
			mURNTextView = (TextView) mView.findViewById(R.id.tv_urn_value);
			mLabelTextView = (TextView) mView.findViewById(R.id.tv_label_value);
			mLabelEditText = (EditText) mView.findViewById(R.id.txt_urn_label);
			setView(mView);

			setCancelable(true);
			setTitle(R.string.add_new_tag);
			setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Tag tag = new Tag(mURNTextView.getText().toString(), mLabelEditText.getText().toString(), 20);
					mContext.submitTag(tag);
				}
			});
			
			setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
		}
		
		public TextView getURNTextView() {
			return mURNTextView;
		}
		
		public TextView getLabelTextView() {
			return mLabelTextView;
		}
		
		public EditText getLabelEditText() {
			return mLabelEditText;
		}
	}

}