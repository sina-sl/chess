package com.example.chess.logic;

public class EnPassant extends Capture {

    public final ChessPiece pawn;
    public final int foeX, foeY;
    public EnPassant(ChessPiece piece, int originX, int originY, int destX, int destY, ChessPiece defeated, int foeX, int foeY) {
        super(piece, originX, originY, defeated, destX, destY);
        this.pawn = defeated;
        this.foeX = foeX;
        this.foeY = foeY;
    }

    @Override
    protected void apply(ChessBoard board) {
        super.apply(board);
        board.board[foeX][foeY] = board.EMPTY;
    }

    @Override
    protected void undo(ChessBoard board) {
        piece.undo();
        board.board[pieceDX][pieceDY] = board.EMPTY;
        board.board[foeX][foeY] = defeated;
        board.board[pieceOX][pieceOY] = piece;
    }
}
