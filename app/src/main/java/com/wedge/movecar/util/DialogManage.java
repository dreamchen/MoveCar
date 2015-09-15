package com.wedge.movecar.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.ArrayAdapter;

import com.wedge.movecar.R;

public class DialogManage {

	/**
	 * 自定义view的dialog
	 * 
	 * @param context
	 * @param title
	 * @param view
	 * @param ok
	 * @param cancel
	 * @param lOk
	 * @param lCancel
	 * @return
	 */
	public static AlertDialog showAlert(final Context context,
			final String title, final View view, final String ok,
			final String cancel, final OnClickListener lOk,
			final OnClickListener lCancel) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new Builder(context);
		if (title != null)
			builder.setTitle(title);
		builder.setView(view);
		if (ok != null)
			builder.setNegativeButton(ok, lOk);
		if (cancel != null)
			builder.setPositiveButton(cancel, lCancel);
		// builder.setNeutralButton("资源", new DialogInterface.OnClickListener()
		// {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub
		// DialogManage.showAddressListDialog(((Activity) context), null);
		// DialogManage.showAddressListDialog(((Activity) context), null);
		// }
		// });

		builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context,
			final String title, final String msg, final View view,
			final String ok, final String cancel,
			final OnClickListener lOk,
			final OnClickListener lCancel) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new Builder(context);
		if (title != null)
			builder.setTitle(title);
		builder.setView(view);
		if (ok != null)
			builder.setNegativeButton(ok, lOk);
		if (cancel != null)
			builder.setPositiveButton(cancel, lCancel);
		if (msg != null)
			builder.setMessage(msg);
		// builder.setNeutralButton("资源", new DialogInterface.OnClickListener()
		// {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub
		// DialogManage.showAddressListDialog(((Activity) context), null);
		// DialogManage.showAddressListDialog(((Activity) context), null);
		// }
		// });

		builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlertDismiss(final Context context,
			final String msg, final String title,
			final OnClickListener l) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new Builder(context);
		builder.setIcon(R.drawable.logo);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setNegativeButton("确定", l);
		builder.setPositiveButton("取消",
				new OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						dialog.cancel();

					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlertDialog(final Context context,
			final String msg, final String title, String left,
			final OnClickListener lf, String right,
			final OnClickListener ri) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new Builder(context);
		builder.setIcon(R.drawable.logo);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setNegativeButton(left, lf);
		builder.setPositiveButton(right, ri);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlertDialog(final Context context,
			final String msg, final String title) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new Builder(context);
		builder.setIcon(R.drawable.logo);
		builder.setTitle(title);
		builder.setMessage(msg);
		// builder.setPositiveButton(R.string.app_ok,
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(final DialogInterface dialog,
		// final int which) {
		// dialog.cancel();
		//
		// }
		// });
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	// public static final void showInputDialog(Activity activity,
	// String title, String message, OnClickListener ocl) {
	//
	// Bundle bunle = new Bundle();
	// bunle.putString(DialogsAlertDialogInputFragment.KEY_TITLE, title);
	// bunle.putString(DialogsAlertDialogInputFragment.KEY_MESSAGE, message);
	// ((DialogsAlertDialogInputFragment)
	// Fragment.instantiate(DialogsAlertDialogInputFragment.class, bunle))
	// .setOnClickListener(ocl).show(activity);
	// }

	public static final void showListObjectDialog(Activity activity,

	String title, String message, Object[] array, OnClickListener ocl) {

		Builder dialog = new Builder(activity);
		if (title != null)
			dialog.setTitle(title);
		if (message != null)
			dialog.setMessage(message);

		ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(activity,
				android.R.layout.simple_list_item_1, array);

		dialog.setAdapter(adapter, ocl);

		dialog.show();
	}

	// Class<? extends DialogsAlertDialogListFragment> clazz
	public static final void showListDialog(Activity activity,

	String title, String message, int arraylistid, OnClickListener ocl) {

		Builder dialog = new Builder(activity);
		if (title != null)
			dialog.setTitle(title);
		if (message != null)
			dialog.setMessage(message);
		String array[] = activity.getResources().getStringArray(arraylistid);
		dialog.setAdapter(new ArrayAdapter<String>(activity,
				android.R.layout.simple_list_item_1, array), ocl);
		dialog.show();
	}

	//
	public static final void showListDialog(Context activity,

	String title, String message, String[] arraylistid, OnClickListener ocl) {

		Builder dialog = new Builder(activity);
		if (title != null)
			dialog.setTitle(title);
		if (message != null)
			dialog.setMessage(message);
		dialog.setAdapter(new ArrayAdapter<String>(activity,
				android.R.layout.simple_list_item_1, arraylistid), ocl);
		dialog.show();

		// Bundle bunle = new Bundle();
		// bunle.putString(DialogsListFragment.KEY_TITLE, title);
		// bunle.putString(DialogsListFragment.KEY_MESSAGE, message);
		// bunle.putStringArray(DialogsListFragment.KEY_lIST, arraylistid);
		// SystemUtil.println("bunle:" + bunle);
		// ((DialogsListFragment)
		// Fragment.instantiate(DialogsListFragment.class,
		// bunle)).setOnClickListener(ocl).show(activity);
	}

	

}
