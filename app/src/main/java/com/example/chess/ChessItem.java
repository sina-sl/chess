package com.example.chess;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class ChessItem extends FrameLayout {

  private final static FrameLayout.LayoutParams LAYOUT_PARAMS = new FrameLayout.LayoutParams(
    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
  ) {{
    this.gravity = Gravity.CENTER;
  }};

  private ImageView pieceImage;
  private Piece piece = Piece.Empty;

  public ChessItem(@NonNull Context context) {
    super(context);
    createPieceHolder();
  }

  private void createPieceHolder() {
    pieceImage = new ImageView(getContext());
    pieceImage.setLayoutParams(LAYOUT_PARAMS);
    pieceImage.setPadding(5,5,5,5);
    addView(pieceImage);
  }

  public void setPiece(Piece piece) {
    this.piece = piece;
    if (piece.getDrawable() != -1){
      pieceImage.setImageResource(piece.getDrawable());
    }
  }

  public Piece getPiece(){
    return piece;
  }

}
