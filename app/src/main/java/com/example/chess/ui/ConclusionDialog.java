package com.example.chess.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chess.R;

public class ConclusionDialog {
	
	public interface OnOkClick {
		void onOkClick(String input);
	}
	
	private AlertDialog alert;
	private EditText edtTitle;
	private TextView txtMsg;
	
	public ConclusionDialog(Context context, String message, OnOkClick onOkClick) {
		
		AlertDialog.Builder alertB = new AlertDialog.Builder(context);
		View alertView = LayoutInflater.from(context).inflate(R.layout.conclusing_alert, null, false);
		
		edtTitle = alertView.findViewById(R.id.editTitle);
		txtMsg = alertView.findViewById(R.id.textMsg);
		
		alertB.setView(alertView);
		alertB.setCancelable(false);
		txtMsg.setText(message);
		
		alertB.setPositiveButton("Ok", (dialog, which) -> {
			if (edtTitle.getText().length() > 0) {
				onOkClick.onOkClick(edtTitle.getText().toString());
				alert.dismiss();
			}
		});
		alert = alertB.show();
	}
	
}
