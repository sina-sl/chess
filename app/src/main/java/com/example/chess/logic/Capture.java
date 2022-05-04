package com.example.chess.logic;

public class Capture extends BaseMove {

    public final ChessPiece defeated;

    public Capture(ChessPiece piece, int originX, int originY, ChessPiece defeated, int foeX, int foeY) {
        super(piece, originX, originY, foeX, foeY);
        this.defeated = defeated;
    }

    @Override
    protected void undo(ChessBoard board) {
        piece.undo();
        board.board[pieceDX][pieceDY] = defeated;
        board.board[pieceOX][pieceOY] = piece;
    }
}
