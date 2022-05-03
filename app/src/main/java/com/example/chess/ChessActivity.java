package com.example.chess;

import android.os.Bundle;
import android.widget.Button;

import com.example.chess.ui.ChessView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.chess.MainActivity.IS_PLAYBACK;

public class ChessActivity extends AppCompatActivity {

  private ChessView chessView;
  private Button btnUndo,btnRedo;

  private boolean isPlayback;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chess);

    isPlayback = getIntent().getBooleanExtra(IS_PLAYBACK,false);

    chessView = findViewById(R.id.chess_view);
    btnUndo = findViewById(R.id.btn_undo);
    btnRedo = findViewById(R.id.btn_redo);

    if (isPlayback){
      btnUndo.setText("Back");
      btnRedo.setText("Next");

      chessView.setEnabled(false);
      chessView.setClickable(false);
      chessView.playBack(MainActivity.currentGame.getMoves());
    } else { // else default value in layout xml
      chessView.setEnabled(true);
      chessView.setClickable(true);
    }

    btnUndo.setOnClickListener((view) -> chessView.undo());
    btnRedo.setOnClickListener((view) -> chessView.redo());

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    finish();
  }
}
