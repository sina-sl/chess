package com.example.chess.logic;

import static com.example.chess.logic.ChessPiece.PieceKind;
import static com.example.chess.logic.ChessPiece.PieceKind.*;
import static com.example.chess.logic.ChessPiece.PieceKind.Queen;

public interface Promotion {
	
	static BaseMove promoteMove(BaseMove simple) { return new PeacefulPromotion(simple); }
	
	static BaseMove promoteMove(Capture capture) { return new CapturingPromotion(capture);}
	
	PieceKind getPromotionKind();
	
	default BaseMove asBaseMove() { return (BaseMove) this; };
	
	default boolean hasSelectedKind() { return getPromotionKind() != null; }
	
	default void setPromotionKind(PieceKind selected) {
		PieceKind promotionKind = getPromotionKind();
		if (promotionKind != null || selected == null) { throw new IllegalStateException(); }
		switch (selected) {
			case Empty: case King: case Pawn: throw new IllegalStateException();
			case Queen: promotionKind = Queen; break;
			case Bishop: promotionKind = Bishop; break;
			case Knight: promotionKind = Knight; break;
			case Rook: promotionKind = Rook; break;
		}
	}
	
	class PeacefulPromotion extends BaseMove implements Promotion {
		
		private final BaseMove originalMove;
		protected PieceKind promotionKind;
		public PeacefulPromotion(BaseMove promoting) {
			super(promoting.piece, promoting.pieceOX, promoting.pieceOY, promoting.pieceDX, promoting.pieceDY);
			this.originalMove = promoting;
		}

		public PieceKind getPromotionKind() {
			return promotionKind;
		}
		
		@Override
		protected void apply(ChessBoard board) {
			originalMove.apply(board);
			board.board[pieceDX][pieceDY] = new ChessPiece(promotionKind, piece.color);
		}
		
		@Override
		protected void undo(ChessBoard board) { originalMove.undo(board); }
		
	}
	
	class CapturingPromotion extends Capture implements Promotion {
		
		private final Capture originalMove;
		protected PieceKind promotionKind;
		public CapturingPromotion(Capture promoting) {
			super(promoting.piece, promoting.pieceOX, promoting.pieceOY, promoting.defeated, promoting.pieceDX, promoting.pieceDY);
			this.originalMove = promoting;
		}
		
		public PieceKind getPromotionKind() {
			return promotionKind;
		}
		
		@Override
		protected void apply(ChessBoard board) {
			originalMove.apply(board);
			board.board[pieceDX][pieceDY] = new ChessPiece(promotionKind, piece.color);
		}
		
		@Override
		protected void undo(ChessBoard board) { originalMove.undo(board); }
		
	}
	
}
