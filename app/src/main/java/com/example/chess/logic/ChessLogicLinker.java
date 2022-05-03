package com.example.chess.logic;


public interface ChessLogicLinker {

    /**
     * @return current chess board
     */
    ChessPiece[][] getSnapShot();

    /**
     * @param piece selected piece possible move set (good for ui!)
     * @return
     */
    BaseMove[] getAllowedMoveSet(ChessPiece piece);

    void undo();

    void redo();

    void move(ChessPiece piece, BaseMove move);

    boolean isStalemate();

    boolean isCheckmate();

    // optional
    long getWhiteScore();

    // optional
    long getBlackScore();

    void resetToStateZero();

    ChessPiece.PieceColor getCurrentPlayerColor();

    long getTurnNumber();
}