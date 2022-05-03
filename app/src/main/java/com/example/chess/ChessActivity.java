package com.example.chess;

import android.os.Bundle;
import android.widget.Button;

import com.example.chess.ui.ChessView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChessActivity extends AppCompatActivity {

  private ChessView chessView;
  private Button btnUndo,btnRedo;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chess);

    chessView = findViewById(R.id.chess_view);
    btnUndo = findViewById(R.id.btn_undo);
    btnRedo = findViewById(R.id.btn_redo);


    btnUndo.setOnClickListener((view) -> chessView.undo());
    btnRedo.setOnClickListener((view) -> chessView.redo());

  }

}
