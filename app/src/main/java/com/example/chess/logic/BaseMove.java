package com.example.chess.logic;

public class BaseMove {
    
    public final ChessPiece piece;
    public final int pieceOX, pieceOY, pieceDX, pieceDY;

    public BaseMove(ChessPiece piece, int pieceOX, int pieceOY, int pieceDX, int pieceDY) {
        this.piece = piece;
        this.pieceOX = pieceOX;
        this.pieceOY = pieceOY;
        this.pieceDX = pieceDX;
        this.pieceDY = pieceDY;
    }

    protected void apply(ChessBoard board) {
        piece.move();
        board.board[pieceDX][pieceDY] = piece;
        board.board[pieceOX][pieceOY] = board.EMPTY;
    }
    protected void undo(ChessBoard board) {
        piece.undo();
        board.board[pieceDX][pieceDY] = board.EMPTY;
        board.board[pieceOX][pieceOY] = piece;
    }
}
