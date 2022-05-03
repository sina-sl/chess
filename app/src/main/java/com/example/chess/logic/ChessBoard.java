package com.example.chess.logic;

import static com.example.chess.logic.ChessPiece.*;
import static com.example.chess.logic.ChessPiece.PieceColor.*;
import static com.example.chess.logic.ChessPiece.PieceKind.*;

import java.util.ArrayList;
import java.util.List;

public class ChessBoard {

	public static final int ROWS = 8, COLUMNS = 8;
	protected final ChessPiece[][] board = makeDefaultBoard();

	public ChessPiece getPiece(int x, int y) {
		if (!(checkXY(x, y))) { return null; }
		return board[x][y];
	}

	private final List<BaseMove> turns = new ArrayList<>();

	public PieceColor getCurrentPlayerColor() { return turns.size() % 2 == 0 ? White : Black; }
	public int getCurrentTurn() { return (turns.size() / 2) + 1; }

	private BaseMove lastAbstractMove() { return turns.get(turns.size() - 1); }
	private int movesSinceCapture = 0;

	private boolean checkXY(int x, int y) {
		return (x < ROWS && y < COLUMNS) && !(x < 0 || y < 0);
	}

	public List<BaseMove> generateLegalMovesFor(int x, int y) {
		if (!(checkXY(x, y))) { return new ArrayList<>(); }

		ChessPiece piece = board[x][y];
		List<BaseMove> pseudoMoves = generatePseudoMovesFor(x, y);

		PieceColor color = getCurrentPlayerColor();
		pseudoMoves.removeIf((move) -> {
			silentMove(move);
			boolean invalid = isKingInCheck(color);
			silentUndo(move);
			return invalid;
		});

		// Castle check moved here as using check checking in the pseudomoves generator yields to recursion
		if (piece.piece == King) {
			tryCastle(pseudoMoves, piece, x, y);
		}

		return pseudoMoves;
	}
	public boolean isKingInCheck(PieceColor kingColor) { return !kingStrikers(kingColor).isEmpty(); }

	public List<Capture> kingStrikers(PieceColor kingColor) {
		List<Capture> strikes = new ArrayList<>();
		for (int x = 0; x < ROWS; x++) {
			for (int y = 0; y < COLUMNS; y++) {
				ChessPiece piece = board[x][y];
				if (!piece.isEmpty() && piece.color != kingColor) { // Kings can't check themselves
					for (BaseMove move: generatePseudoMovesFor(x, y)) {
						if (move instanceof Capture ) {
							Capture capture = (Capture) move;
							if (capture.defeated.piece == King && kingColor == capture.getAttacked().color) { // Sanity check, must be always true
								strikes.add(capture);
							}
						}
					}
				}
			}
		}
		return strikes;
	}

	public enum GameState {
		Continue, Check,
		Stalemate, Checkmate
	}

	// Do not put invalid AbstractMoves in :)

	private void silentMove(BaseMove move) { move.apply(this); }

	private void silentUndo(BaseMove move) { move.undo(this); }

	public GameState move(BaseMove move) {
		if (move instanceof Promotion  && !((Promotion)move).hasSelectedKind()) {
			throw new IllegalStateException();
		}

		turns.add(move);

		if (move.piece.piece == Pawn || move instanceof Capture) {
			movesSinceCapture = 0;
		} else { movesSinceCapture++; }

		move.apply(this);

		if (movesSinceCapture > 50) {
			return GameState.Stalemate;
		}

		return getState();
	}

	public GameState getState() {
		PieceColor color = getCurrentPlayerColor();
		if (isKingInCheck(color)) {
			if (hasMoves(color)) {
				return GameState.Check;
			} else {
				return GameState.Checkmate;
			}
		} else {
			if (hasMoves(color)) {
				return GameState.Continue;
			} else {
				return GameState.Stalemate;
			}
		}
	}

	public boolean hasMoves(PieceColor color) {
		for (int x = 0; x < ROWS; x++) {
			for (int y = 0; y < COLUMNS; y++) {
				ChessPiece piece = board[x][y];
				if (!piece.isEmpty() && piece.color == color) {
					if (!generateLegalMovesFor(x, y).isEmpty()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public BaseMove undo() {
		if (turns.isEmpty()) { return null; }

		BaseMove move = turns.remove(turns.size() - 1);

		move.undo(this);

		movesSinceCapture = turns.size();
		for (int i = 0; i < turns.size(); i++) {
			BaseMove passive = turns.get(turns.size() - i - 1);
			if (passive.piece.piece == Pawn || passive instanceof Capture) {
				movesSinceCapture = i; break;
			}
		}

		return move;
	}

	// Only give valid AbstractMoves, obtained from `undo`
	public void redo(BaseMove redo) { move(redo); }

	public void reset() {
		ChessPiece[][] newBoard = makeDefaultBoard();
		for (int x = 0; x < ROWS; x++) {
			for (int y = 0; y < COLUMNS; y++) {
				board[x][y] = newBoard[x][y];
			}
		}

		turns.clear();
		movesSinceCapture = 0;
	}

	private boolean canLandOn(ChessPiece piece, int x, int y) {
		if (!(checkXY(x, y))) { return false; }
		ChessPiece dest = board[x][y];
		return dest.piece == Empty || dest.color != piece.color;
	}

	private boolean hasOpponent(ChessPiece piece, int x, int y) {
		if (!(checkXY(x, y))) { return false; }
		ChessPiece dest = board[x][y];
		return dest.piece != Empty && dest.color != piece.color;
	}

	private List<BaseMove> generatePseudoMovesFor(int x, int y) {
		if (!(checkXY(x, y))) { return new ArrayList<>(); }

		ChessPiece piece = board[x][y];

		switch (piece.piece) {
			case Empty: return new ArrayList<>();

			case Rook: return extrudeCardinals(new ArrayList<>(), piece, x, y);
			case Bishop: return extrudeDiagonals(new ArrayList<>(), piece, x, y);
			case Queen: return generateQueen(piece, x, y);

			case King: {
				List<BaseMove> pseudoMoves = generateKing(piece, x, y);

				// Castling check has to be done in the valid moves checker as we can't rely on check checking here or else we get recursion
				// tryCastle

				return pseudoMoves;
			}

			case Knight: return generateKnight(piece, x, y);

			case Pawn: {
				List<BaseMove> pseudoMoves = new ArrayList<>();

				// Direction depends on color
				int dir = piece.color == Black ? 1 : -1;
				int dy = y + dir;

				if (checkXY(x, dy)) { // Checks for bounds as well (should theatrically not end up at the top row (promotion))
					if (board[x][dy].piece == Empty) {
						pseudoMoves.add(new BaseMove(piece, x, y, x, dy));

						// First AbstractMove can be two ahead
						if (piece.notMovedYet() && checkXY(x, dy + dir) && board[x][dy + dir].piece == Empty) { // Check is always be inbounds
							pseudoMoves.add(new BaseMove(piece, x, y, x, dy + dir));
						}
					}
				}

				for (int dx = x - 1; dx <= x + 1; dx += 2) {
					if (hasOpponent(piece, dx, dy)) { // includes the bound check
						pseudoMoves.add(new Capture(piece, x, y, board[dx][dy], dx, dy));
					} else if (
						hasOpponent(piece, dx, y) && board[dx][y].piece == Pawn && board[dx][y].movedOnce()
							&& lastAbstractMove().piece == board[dx][y] // we do need == as we want to know exactly which pawn, otherwise just do a pos check
							&& Math.abs(lastAbstractMove().pieceDY - lastAbstractMove().pieceOY) == 2
					) { // En Passant Yikes
						pseudoMoves.add(new EnPassant(piece, x, y, dx, dy, board[dx][y], dx, y));
					}
				}

				for (int i = 0; i < pseudoMoves.size(); i++) {
					BaseMove move = pseudoMoves.get(i);
					if (move.pieceDY == (piece.color == White ? 0 : COLUMNS - 1)) {
						pseudoMoves.set(i, new Promotion(move));
					}
				}

				return pseudoMoves;

			}
		}

		return new ArrayList<>(); // Must be unreachable
	}


	private void tryCastle(List<BaseMove> pseudoMoves, ChessPiece piece, int x, int y) {
		if (piece.notMovedYet() && !isKingInCheck(piece.color)) {
			int ry = piece.color == Black ? 0 : COLUMNS - 1; // == y thus y
			for (int rx = 0; rx < ROWS; rx += ROWS - 1) {
				ChessPiece rook = board[rx][ry];
				if (rook.piece == Rook && rook.color == piece.color && rook.notMovedYet()) {
					int sign = rx < x ? -1 : 1;
					int dx = x + sign + sign; // Must be inbounds
					Castle castle = new Castle(piece, x, y, dx, y, rook, rx, ry, dx - sign, ry);
					boolean canCastle = true;
					for (int cx = x + sign; cx != dx + sign; cx += sign) {
						if (!board[cx][y].isEmpty()) { canCastle = false; break; }
						BaseMove move = new BaseMove(piece, x, y, cx, y);
						silentMove(move); // can end up being a BaseMove (EMPTY will be swapped in)
						if (isKingInCheck(piece.color)) { canCastle = false; silentUndo(move); break; }
						silentUndo(move);
					}
					if (canCastle && board[dx - sign][y].isEmpty()) {
						pseudoMoves.add(castle);
					}
				}
			}
		}
	}
	private List<BaseMove> extrudeEnd(List<BaseMove> pseudoMoves, ChessPiece piece, int x, int y, int dx, int dy) {
		return extrude(pseudoMoves, piece, x, y, dx, dy, ROWS * COLUMNS);
	}

	private List<BaseMove> extrudeOnce(List<BaseMove> pseudoMoves, ChessPiece piece, int x, int y, int dx, int dy) {
		return extrude(pseudoMoves, piece, x, y, dx, dy, 1);
	}

	private List<BaseMove> extrudeDiagonals(List<BaseMove> pseudoMoves, ChessPiece piece, int x, int y) {
		return extrudeEnd(pseudoMoves, piece, x, y, 1, 1);
	}

	private List<BaseMove> extrudeCardinals(List<BaseMove> pseudoMoves, ChessPiece piece, int x, int y) {
		return extrudeEnd(pseudoMoves, piece, x, y, 1, 0);
	}

	private List<BaseMove> generateKnight(ChessPiece piece, int x, int y) {
		if (piece.piece != Knight) { return new ArrayList<>(); }
		return extrudeOnce(
			extrudeOnce(new ArrayList<>(), piece, x, y, 1, 2),
			piece, x, y, -1, 2
		);
	}

	private List<BaseMove> generateKing(ChessPiece piece, int x, int y) {
		if (piece.piece != King) { return new ArrayList<>(); }
		return extrudeOnce(
			extrudeOnce(new ArrayList<>(), piece, x, y, 1, 0),
			piece, x, y, 1, 1
		);
	}

	private List<BaseMove> generateQueen(ChessPiece piece, int x, int y) {
		if (piece.piece != Queen) { return new ArrayList<>(); }
		return extrudeCardinals(
			extrudeDiagonals(new ArrayList<>(), piece, x, y),
			piece, x, y
		);
	}

	private List<BaseMove> extrude(List<BaseMove> pseudoMoves, ChessPiece piece, int x, int y, int dx, int dy, int max) {
		for (int rotations = 0; rotations < 4; rotations++) {
			for (int px = x + dx, py = y + dy, c = 0; checkXY(px, py) && c < max; px += dx, py += dy, c++) {
				if (!(canLandOn(piece, px, py))) { break; }
				if (!hasOpponent(piece, px, py)) {
					pseudoMoves.add(new BaseMove(piece, x, y, px, py));
				} else {
					pseudoMoves.add(new Capture(piece, x, y, board[px][py], px, py));
					break;
				}
			}
			int tm = dx;
			dx = -dy;
			dy = tm;
		}
		return pseudoMoves;
	}

	public static ChessPiece[][] makeDefaultBoard() {
		ChessPiece[][] pieces = new ChessPiece[ROWS][COLUMNS];
		for (int x = 0; x < ROWS; x++) {
			for (int y = 0; y < COLUMNS; y++) {
				pieces[x][y] = DEFAULT_BOARD[y][x].clone();
			}
		}
		return pieces;
	}

	protected static final ChessPiece EMPTY = new ChessPiece();
	private static final ChessPiece[][] DEFAULT_BOARD = {
		{ new ChessPiece(Rook, Black), new ChessPiece(Knight, Black), new ChessPiece(Bishop, Black), new ChessPiece(Queen, Black), new ChessPiece(King, Black), new ChessPiece(Bishop, Black), new ChessPiece(Knight, Black), new ChessPiece(Rook, Black) },
		{ new ChessPiece(Pawn, Black), new ChessPiece(Pawn, Black), new ChessPiece(Pawn, Black), new ChessPiece(Pawn, Black), new ChessPiece(Pawn, Black), new ChessPiece(Pawn, Black), new ChessPiece(Pawn, Black), new ChessPiece(Pawn, Black) },
		{ EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY },
		{ EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY },
		{ EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY },
		{ EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY },
		{ new ChessPiece(Pawn, White), new ChessPiece(Pawn, White), new ChessPiece(Pawn, White), new ChessPiece(Pawn, White), new ChessPiece(Pawn, White), new ChessPiece(Pawn, White), new ChessPiece(Pawn, White), new ChessPiece(Pawn, White) },
		{ new ChessPiece(Rook, White), new ChessPiece(Knight, White), new ChessPiece(Bishop, White), new ChessPiece(Queen, White), new ChessPiece(King, White), new ChessPiece(Bishop, White), new ChessPiece(Knight, White), new ChessPiece(Rook, White) }
	};

	public List<BaseMove> getTurns() {
		return turns;
	}

	public void setTurns(List<BaseMove> turns){
		this.turns.clear();
		this.turns.addAll(turns);
	}

}