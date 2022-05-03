package com.example.chess.logic;

import static com.example.chess.logic.ChessPiece.PieceKind;
import static com.example.chess.logic.ChessPiece.PieceKind.*;
import static com.example.chess.logic.ChessPiece.PieceKind.Queen;

public class Promotion extends BaseMove {

  private final BaseMove originalMove;
  protected PieceKind promotionKind;
  public Promotion(BaseMove promoting) {
    super(promoting.piece, promoting.pieceOX, promoting.pieceOY, promoting.pieceDX, promoting.pieceDY);
    this.originalMove = promoting;
  }

  public boolean hasSelectedKind() { return promotionKind != null; }

  public PieceKind getPromotionKind() {
    return promotionKind;
  }

  public void setPromotionKind(PieceKind selected) {
    if (promotionKind != null || selected == null) { throw new IllegalStateException(); }
    else switch (selected) {
      case Empty:
      case King:
      case Pawn:
      case Queen:
        promotionKind = Queen;
        break;
      case Bishop:
        promotionKind = Bishop;
        break;
      case Knight:
        promotionKind = Knight;
        break;
      case Rook:
        promotionKind = Rook;
        break;
    }
  }

  @Override
  protected void apply(ChessBoard board) {
    originalMove.apply(board);
    board.board[pieceDX][pieceDY] = new ChessPiece(promotionKind, piece.color);
  }

  @Override
  protected void undo(ChessBoard board) { originalMove.undo(board); }
}
