package com.example.chess.logic;

public class Castle extends BaseMove {

    private final BaseMove rookMove;

    public Castle(ChessPiece king, int kingOX, int kingOY, int kingDX, int kingDY, ChessPiece rook, int rookOX, int rookOY, int rookDX, int rookDY) {
        super(king, kingOX, kingOY, kingDX, kingDY);
        this.rookMove = new BaseMove(rook, rookOX, rookOY, rookDX, rookDY);
    }

    @Override
    protected void undo(ChessBoard board) {
        super.undo(board);
        rookMove.undo(board);
    }

    @Override
    protected void apply(ChessBoard board) {
        super.apply(board);
        rookMove.apply(board);
    }
}
