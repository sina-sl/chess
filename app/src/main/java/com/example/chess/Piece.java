package com.example.chess;

import androidx.annotation.DrawableRes;

public enum Piece {
  Empty(-1),
  BKing(R.drawable.bking), BQueen(R.drawable.bqueen), BKnight(R.drawable.bknight), BBishop(R.drawable.bbishop), BPawn(R.drawable.bpawn), BRook(R.drawable.brook),
  WKing(R.drawable.wking), WQueen(R.drawable.wqueen), WKnight(R.drawable.wknight), WBishop(R.drawable.wbishop), WPawn(R.drawable.wpawn), WRook(R.drawable.wrook);
  
  @DrawableRes
   private final int drawable;
  
  Piece(@DrawableRes int drawable){
    this.drawable = drawable;
  }

  public int getDrawable() {
    return drawable;
  }
}
