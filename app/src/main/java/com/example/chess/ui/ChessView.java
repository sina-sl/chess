package com.example.chess.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.chess.R;
import com.example.chess.logic.BaseMove;
import com.example.chess.logic.Capture;
import com.example.chess.logic.ChessBoard;
import com.example.chess.logic.ChessPiece;
import com.example.chess.logic.Promotion;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.DrawableRes;

import static com.example.chess.logic.ChessPiece.PieceKind.Bishop;
import static com.example.chess.logic.ChessPiece.PieceKind.Knight;
import static com.example.chess.logic.ChessPiece.PieceKind.Queen;
import static com.example.chess.logic.ChessPiece.PieceKind.Rook;

public class ChessView extends LinearLayout {

  @DrawableRes
  private final int
    WHITE = R.drawable.even,
    BLACK = R.drawable.odd,
    WHITE_HIGHLIGHT = R.drawable.even_hilight,
    BLACK_HIGHLIGHT = R.drawable.odd_hilight,
    RED_WHITE = R.drawable.even_red,
    RED_BLACK = R.drawable.odd_red,
    SELECT = R.drawable.select;

  private final int rowCount = 8;
  private final int colCount = 8;

  private static List<BaseMove> undone = new ArrayList<>();
  private static List<BaseMove> moves = null;
  private static List<Capture> kingStrikes = null;

  private static final ChessBoard board = new ChessBoard();

  private final PieceItem[][] pieceItems = new PieceItem[rowCount][colCount];


  private final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
  );

  private final LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(
    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
  );



  public ChessView(Context context, AttributeSet attrs) {
    super(context, attrs);
    layoutParams.weight = 1;
    childParams.weight = 1;
    createChessBackground();
  }



  @SuppressLint("UseCompatLoadingForDrawables")
  private void createChessBackground() {

    setOrientation(HORIZONTAL);
    setWeightSum(rowCount);

    for (int i = 0; i < rowCount; i++) {
      LinearLayout colLayout = new LinearLayout(getContext());
      colLayout.setLayoutParams(layoutParams);
      colLayout.setWeightSum(colCount);
      colLayout.setOrientation(VERTICAL);

      for (int j = 0; j < colCount; j++) {

        pieceItems[i][j] = new PieceItem(getContext());
        pieceItems[i][j].setLayoutParams(childParams);

        int finalI = i, finalJ = j;
        pieceItems[i][j].setOnClickListener(view -> handleClick(finalI, finalJ));

        if ((i + j) % 2 == 0) {
          pieceItems[i][j].setBackground(getResources().getDrawable(R.drawable.even, getContext().getTheme()));
        } else {
          pieceItems[i][j].setBackground(getResources().getDrawable(R.drawable.odd, getContext().getTheme()));
        }
        colLayout.addView(pieceItems[i][j]);
      }
      addView(colLayout);
    }
    resetGrid();
  }



  private PieceItem.PieceResources getDrawableOfPiece(ChessPiece piece, boolean isEven) {
    int pieceRes = 0;
    int background = 0;

    switch (piece.piece) {
      case King:
        if (piece.color == ChessPiece.PieceColor.Black) {
          pieceRes = R.drawable.bking;
        } else {
          pieceRes = R.drawable.wking;
        }
        break;
      case Queen:
        if (piece.color == ChessPiece.PieceColor.Black) {
          pieceRes = R.drawable.bqueen;
        } else {
          pieceRes = R.drawable.wqueen;
        }
        break;
      case Bishop:
        if (piece.color == ChessPiece.PieceColor.Black) {
          pieceRes = R.drawable.bbishop;
        } else {
          pieceRes = R.drawable.wbishop;
        }
        break;
      case Knight:
        if (piece.color == ChessPiece.PieceColor.Black) {
          pieceRes = R.drawable.bknight;
        } else {
          pieceRes = R.drawable.wknight;
        }
        break;
      case Rook:
        if (piece.color == ChessPiece.PieceColor.Black) {
          pieceRes = R.drawable.brook;
        } else {
          pieceRes = R.drawable.wrook;
        }
        break;
      case Pawn:
        if (piece.color == ChessPiece.PieceColor.Black) {
          pieceRes = R.drawable.bpawn;
        } else {
          pieceRes = R.drawable.wpawn;
        }
        break;
    }

    if (piece.color != null && board.getCurrentPlayerColor() == piece.color) {
      background = isEven ? R.drawable.even_hilight : R.drawable.odd_hilight;
    } else {
      background = isEven ? R.drawable.even : R.drawable.odd;
    }

    return new PieceItem.PieceResources(pieceRes, background);
  }




  private void resetGrid() {
    for (int x = 0; x < board.ROWS; x++) {
      for (int y = 0; y < board.COLUMNS; y++) {

        ChessPiece piece = board.getPiece(x, y);
        PieceItem.PieceResources pieceResources = getDrawableOfPiece(piece, (x + y) % 2 == 0);
        pieceItems[x][y].setPieceResources(pieceResources);

        if (kingStrikes != null) {
          for (BaseMove move : kingStrikes) {

            PieceItem pieceItemD = pieceItems[move.pieceDX][move.pieceDY];
            PieceItem pieceItemO = pieceItems[move.pieceOX][move.pieceOY];

            PieceItem.PieceResources pieceResD = pieceItemD.getPieceResources();
            PieceItem.PieceResources pieceResO = pieceItemO.getPieceResources();

            pieceItemD.setPieceResources(new PieceItem.PieceResources(pieceResD.getPieceRes(), (move.pieceDX + move.pieceDY) % 2 == 0 ? RED_WHITE : RED_BLACK));
            pieceItemO.setPieceResources(new PieceItem.PieceResources(pieceResO.getPieceRes(), (move.pieceOX + move.pieceOY) % 2 == 0 ? RED_WHITE : RED_BLACK));
          }
        }

        if (moves != null) {
          for (BaseMove move : moves) {
            PieceItem pieceItem = pieceItems[move.pieceDX][move.pieceDY];
            PieceItem.PieceResources pieceRes = pieceItem.getPieceResources();
            pieceItem.setPieceResources(new PieceItem.PieceResources(pieceRes.getPieceRes(), SELECT));
          }
        }
      }
    }
  }



  private void handleClick(int x, int y) {
    System.out.printf("Clicked (%d; %d)\n", x, y);

    BaseMove found = null;
    if (moves != null) {
      for (BaseMove move : moves) {
        if (move.pieceDX == x && move.pieceDY == y) {
          found = move;
        }
      }
      if (found == null) {
        moves = null;
      }
    } else if (board.getPiece(x, y).piece != ChessPiece.PieceKind.Empty && board.getPiece(x, y).color == board.getCurrentPlayerColor()) {
      moves = board.generateLegalMovesFor(x, y);
      if (moves.isEmpty()) {
        moves = null;
      } else {
//        System.out.printf("Selecting for %s\n", chessGrid[x][y].getText());
      }
    }

    if (found != null) {
      if (found instanceof Promotion) { // Promotion handler
        BaseMove finalFound = found;
        new PromoteDialog(getContext(), pieceKind -> {
          ((Promotion) finalFound).setPromotionKind(pieceKind == null ? Knight : pieceKind);
          handleMove(finalFound);
        }).show();
      } else {
        handleMove(found);
      }
    }else {
      resetGrid();
    }
  }


  private void handleMove(BaseMove found) {
    switch (board.move(found)) {
      case Continue:
        kingStrikes = null;
        break;

      case Check:
        kingStrikes = board.kingStrikers(board.getCurrentPlayerColor());
        break;

      case Stalemate:
        kingStrikes = null;
        resetGrid();
        board.reset();
        new AlertDialog.Builder(getContext()).setMessage("You just stalemated, lame").show();
        break;

      case Checkmate:
        kingStrikes = board.kingStrikers(board.getCurrentPlayerColor());
        resetGrid();
        board.reset();
        new AlertDialog.Builder(getContext()).setMessage("You just won wow").show();
        break;
    }
    undone.clear();
    moves = null;
    resetGrid();
  }

  public void undo() {
    BaseMove move = board.undo();
    if (move != null) {
      undone.add(move);
    }
    moves = null;
    resetGrid();
  }

  public void redo() {
    if (!undone.isEmpty()) {
      board.redo(undone.remove(undone.size() - 1));

      kingStrikes = board.kingStrikers(board.getCurrentPlayerColor());
      moves = null;

      resetGrid();
    }
  }

}
