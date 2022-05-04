package com.example.chess;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.chess.ui.ChessView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.chess.GamesActivity.IS_PLAYBACK;

public class ChessActivity extends AppCompatActivity {
	
	private ChessView chessView;
	private Button btnUndo,btnRedo;
	
	private boolean isPlayback;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chess);
		
		isPlayback = getIntent().getBooleanExtra(IS_PLAYBACK,false);
		
		chessView = findViewById(R.id.chessView);
		btnUndo = findViewById(R.id.undoButton);
		btnRedo = findViewById(R.id.redoButton);
		
		if (isPlayback){
			btnUndo.setText("Back");
			btnRedo.setText("Next");
			
			chessView.setEnabled(false);
			chessView.setClickable(false);
			chessView.playBack(GamesActivity.currentGame.getMoves());
		} else { // else default value in layout xml
			chessView.setEnabled(true);
			chessView.setClickable(true);
		}
		
		btnUndo.setOnClickListener((view) -> chessView.undo());
		btnRedo.setOnClickListener((view) -> chessView.redo());
		
	}
	
	public void chessUndo(View view) { chessView.undo(); }
	public void chessRedo(View view) { chessView.redo(); }
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}
}
