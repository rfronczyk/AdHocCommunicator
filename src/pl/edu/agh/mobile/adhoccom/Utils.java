package pl.edu.agh.mobile.adhoccom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class Utils {
	public interface YesNoDialogListener {
		public void onYes();

		public void onNo();
	}

	public static AlertDialog createMessageDialog(Context ctx, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

		builder.setMessage(msg);
		builder.setCancelable(true);
		builder.setNeutralButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// empty
			}
		});

		return builder.create();
	}
	
	public static AlertDialog createMessageDialog(Context ctx, Exception ex) {
		return createMessageDialog(ctx, ex.getMessage());
	}

	public static AlertDialog createYesNoDialog(Context ctx, String msg,
			final YesNoDialogListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == Dialog.BUTTON_POSITIVE)
					listener.onYes();
				else if (which == Dialog.BUTTON_NEGATIVE)
					listener.onNo();
			}
		};

		builder.setMessage(msg);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.yes, onClickListener);
		builder.setNegativeButton(R.string.no, onClickListener);

		return builder.create();
	}
}
