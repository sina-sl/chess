package com.example.chess.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.chess.R;
import com.example.chess.logic.ChessPiece;
import com.example.chess.logic.Promotion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PromoteDialog {
	
	public interface OnChosePromote{
		void onChose(ChessPiece.PieceKind pieceKind);
	}
	
	
	private OnChosePromote onChosePromote;
	private AlertDialog alert;
	private Button btnKnight, btnRook, btnQueen, btnBishop;
	
	public PromoteDialog(Context context , OnChosePromote onChosePromote){
		this.onChosePromote = onChosePromote;
		
		AlertDialog.Builder alertB = new AlertDialog.Builder(context);
		View alertView = LayoutInflater.from(context).inflate(R.layout.promote_alert,null,false);
		
		btnBishop = alertView.findViewById(R.id.btn_bishop);
		btnKnight = alertView.findViewById(R.id.btn_knight);
		btnQueen = alertView.findViewById(R.id.btn_queen);
		btnRook = alertView.findViewById(R.id.btn_rook);
		
		btnBishop.setOnClickListener(this::onClicks);
		btnKnight.setOnClickListener(this::onClicks);
		btnQueen.setOnClickListener(this::onClicks);
		btnRook.setOnClickListener(this::onClicks);
		
		alertB.setView(alertView);
		alertB.setOnCancelListener(dialog -> onChosePromote.onChose(null));
		alert = alertB.show();
	}
	
	private void onClicks(View view){
		onChosePromote.onChose(
			ChessPiece.PieceKind.valueOf(
				((Button)view).getText().toString()
			)
		);
		alert.dismiss();
	}
	
}
