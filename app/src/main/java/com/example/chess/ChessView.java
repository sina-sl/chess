package com.example.chess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

public class ChessView extends LinearLayout {

  private final int rowCount = 8;
  private final int colCount = 8;

  private final ChessItem[][] chessItem = new ChessItem[rowCount][colCount];
  private final Piece[][] pieces =
  {
    {
      Piece.BRook , Piece.BKnight, Piece.BBishop , Piece.BQueen, Piece.BKing , Piece.BBishop, Piece.BKnight , Piece.BRook
    },
    {
      Piece.BPawn , Piece.BPawn, Piece.BPawn , Piece.BPawn, Piece.BPawn , Piece.BPawn, Piece.BPawn , Piece.BPawn
    },
    {
      Piece.Empty , Piece.Empty, Piece.Empty , Piece.Empty, Piece.Empty , Piece.Empty, Piece.Empty , Piece.Empty
    },
    {
      Piece.Empty , Piece.Empty, Piece.Empty , Piece.Empty, Piece.Empty , Piece.Empty, Piece.Empty , Piece.Empty
    },
    {
      Piece.Empty , Piece.Empty, Piece.Empty , Piece.Empty, Piece.Empty , Piece.Empty, Piece.Empty , Piece.Empty
    },
    {
      Piece.Empty , Piece.Empty, Piece.Empty , Piece.Empty, Piece.Empty , Piece.Empty, Piece.Empty , Piece.Empty
    },
    {
      Piece.WPawn , Piece.WPawn, Piece.WPawn , Piece.WPawn, Piece.WPawn , Piece.WPawn, Piece.WPawn , Piece.WPawn
    },
    {
      Piece.WRook , Piece.WKnight, Piece.WBishop , Piece.WQueen, Piece.WKing , Piece.WBishop, Piece.WKnight , Piece.WRook
    }
  };

  private final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
  );

  private final LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(
    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
  );



  private ChessItem firstChessItem = null;

  public ChessView(Context context, AttributeSet attrs) {
    super(context, attrs);
    layoutParams.weight = 1;
    childParams.weight = 1;
    createChessBackground();
  }


  @SuppressLint("UseCompatLoadingForDrawables")
  private void createChessBackground() {

    setOrientation(VERTICAL);
    setWeightSum(rowCount);

    for (int i = 0; i < rowCount; i++) {
      LinearLayout colLayout = new LinearLayout(getContext());
      colLayout.setLayoutParams(layoutParams);
      colLayout.setWeightSum(colCount);
      colLayout.setOrientation(HORIZONTAL);

      for (int j = 0; j < colCount; j++) {

        chessItem[i][j] = new ChessItem(getContext());
        chessItem[i][j].setLayoutParams(childParams);
        chessItem[i][j].setOnClickListener(this::onChessItemClickListener);

        if ((i + j) % 2 == 0) {
          chessItem[i][j].setBackground(getResources().getDrawable(R.drawable.even, getContext().getTheme()));
        } else {
          chessItem[i][j].setBackground(getResources().getDrawable(R.drawable.odd, getContext().getTheme()));
        }
        colLayout.addView(chessItem[i][j]);
      }
      addView(colLayout);
    }

    setPieces(pieces);

  }

  private void onChessItemClickListener(View item){

    ChessItem clickedItem = (ChessItem) item;

    if (firstChessItem == null && clickedItem.getPiece() != Piece.Empty){

      firstChessItem = clickedItem;

    } else if (firstChessItem != null && clickedItem.getPiece() == Piece.Empty){

      clickedItem.setPiece(firstChessItem.getPiece());
      firstChessItem.setPiece(Piece.Empty);
      firstChessItem = null;

    }

  }

  public void setPieces(Piece[][] pieces) {
    for (int i = 0; i < pieces.length; i++) {
      for (int j = 0; j < pieces[i].length; j++) {
        chessItem[i][j].setPiece(pieces[i][j]);
      }
    }
  }

}
