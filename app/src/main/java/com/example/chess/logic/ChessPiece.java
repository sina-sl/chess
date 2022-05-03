package com.example.chess.logic;

import java.util.Objects;

public class ChessPiece {

  public enum PieceKind { Empty, King, Queen, Bishop, Knight, Rook, Pawn }

  public enum PieceColor { White, Black }

  public final PieceKind piece;
  public final PieceColor color;

  private int timesMoved;

  public boolean notMovedYet() { return timesMoved == 0; }
  public boolean movedOnce() { return timesMoved == 1; }

  public boolean isEmpty() { return this.piece == PieceKind.Empty; }

  protected void move() { timesMoved++; }
  protected void undo() { timesMoved--; }

  public ChessPiece() { this(PieceKind.Empty, null, -1); }

  public ChessPiece(PieceKind piece, PieceColor color) { this(piece, color, 0); }

  protected ChessPiece(PieceKind piece, PieceColor color, int timesMoved) {
    this.piece = piece;
    this.color = color;
    this.timesMoved = timesMoved;
  }

  private ChessPiece(ChessPiece clone) { this(clone.piece, clone.color, clone.timesMoved); }

  @Override public ChessPiece clone() { return new ChessPiece(this); }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChessPiece that = (ChessPiece) o;
    return piece == that.piece && color == that.color;
  }

  // Can use an HashMap to detect triple repetitions, thus gotta ommit the moves counter
  @Override
  public int hashCode() { return Objects.hash(piece, color); }

}