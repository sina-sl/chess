package com.example.chess.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.chess.GameDB;
import com.example.chess.GamesActivity;
import com.example.chess.R;
import com.example.chess.logic.BaseMove;
import com.example.chess.logic.Capture;
import com.example.chess.logic.ChessBoard;
import com.example.chess.logic.ChessPiece;
import com.example.chess.logic.Promotion;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.DrawableRes;

import static com.example.chess.logic.ChessPiece.PieceKind.Knight;

public class ChessView extends LinearLayout {
	
	@DrawableRes
	private static final int
		WHITE = R.drawable.even,
		BLACK = R.drawable.odd,
		WHITE_HIGHLIGHT = R.drawable.even_hilight,
		BLACK_HIGHLIGHT = R.drawable.odd_hilight,
		RED_WHITE = R.drawable.even_red,
		RED_BLACK = R.drawable.odd_red,
		SELECT = R.drawable.select;
	
	private static final int MAX_UNDO = 1;
	
	private static List<BaseMove> undoneMoves = new ArrayList<>();
	
	private static List<BaseMove> selectedPieceMoves = null;
	
	private static List<Capture> kingStrikes = null;
	
	private boolean isPlayback = false;
	
	private static final ChessBoard board = new ChessBoard();
	
	private final PieceItem[][] pieceItems = new PieceItem[board.ROWS][board.COLUMNS];
	
	
	private final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
		ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
	);
	
	private final LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(
		ViewGroup.LayoutParams.WRAP_CONTENT, 96
	);

	
	public ChessView(Context context, AttributeSet attrs) {
		super(context, attrs);
		layoutParams.weight = 1;
		childParams.weight = 1;
		createChessBackground();
	}
	
	
	@SuppressLint("UseCompatLoadingForDrawables")
	private void createChessBackground() {
		
		kingStrikes = null;
		selectedPieceMoves = null;
		undoneMoves.clear();
		board.reset();
		
		setOrientation(HORIZONTAL);
		setWeightSum(board.ROWS);
		
		for (int x = 0; x < board.ROWS; x++) {
			
			LinearLayout colLayout = new LinearLayout(getContext());
			colLayout.setLayoutParams(layoutParams);
			colLayout.setWeightSum(board.COLUMNS);
			colLayout.setOrientation(VERTICAL);
			
			for (int y = 0; y < board.COLUMNS; y++) {
				pieceItems[x][y] = new PieceItem(getContext());
				pieceItems[x][y].setLayoutParams(childParams);
				pieceItems[x][y].setPadding(6, 6, 6, 6);
				
				int finalI = x, finalJ = y;
				pieceItems[x][y].setOnClickListener(view -> handleClick(finalI, finalJ));
				pieceItems[x][y].setBackground(getResources().getDrawable((x + y) % 2 == 0 ? WHITE : BLACK, getContext().getTheme()));
				
				colLayout.addView(pieceItems[x][y]);
			}
			
			addView(colLayout);
		}
		
		updateGridCells();
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
			background = isEven ? WHITE_HIGHLIGHT : BLACK_HIGHLIGHT;
		} else {
			background = isEven ? WHITE : BLACK;
		}
		
		return new PieceItem.PieceResources(pieceRes, background);
	}
	
	
	private void updateGridCells() {
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
				
				if (selectedPieceMoves != null) {
					for (BaseMove move : selectedPieceMoves) {
						PieceItem pieceItem = pieceItems[move.pieceDX][move.pieceDY];
						PieceItem.PieceResources pieceRes = pieceItem.getPieceResources();
						pieceItem.setPieceResources(new PieceItem.PieceResources(pieceRes.getPieceRes(), SELECT));
					}
				}
			}
		}
	}
	
	
	private void handleClick(int x, int y) {
		BaseMove found = null;
		if (selectedPieceMoves != null) {
			for (BaseMove move : selectedPieceMoves) {
				if (move.pieceDX == x && move.pieceDY == y) {
					found = move;
				}
			}
			if (found == null) {
				selectedPieceMoves = null;
			}
		} else if (board.getPiece(x, y).piece != ChessPiece.PieceKind.Empty && board.getPiece(x, y).color == board.getCurrentPlayerColor()) {
			selectedPieceMoves = board.generateLegalMovesFor(x, y);
			if (selectedPieceMoves.isEmpty()) { selectedPieceMoves = null; }
		}
		
		if (found != null) {
			if (found instanceof Promotion) { // Promotion handler
				BaseMove finalFound = found;
				new PromoteDialog(getContext(), pieceKind -> {
					((Promotion) finalFound).setPromotionKind(pieceKind == null ? Knight : pieceKind);
					handleMove(finalFound);
				});
			} else {
				handleMove(found);
			}
		} else {
			updateGridCells();
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
				
				final List<BaseMove> save1 = new ArrayList<>(board.getTurns()); //clone
				
				ChessPiece.PieceColor wonColor1 = board.getCurrentPlayerColor() == ChessPiece.PieceColor.Black ?
					ChessPiece.PieceColor.White:
					ChessPiece.PieceColor.Black;
				
				new ConclusionDialog(
					getContext(),
					wonColor1.name() + " You just stalemated, lame\n" +
						"Enter title for this game",
					(input) -> {
						GamesActivity.gameDB.insertGame(
							new GameDB.Game(
								input,
								save1
							)
						);
						((Activity) getContext()).finish();
					}
				);
				
				// TODO: Instead teleport to the game viewer if the game is saved, otherwise, reset
				updateGridCells();
				freeze();
				
				break;
			
			case Checkmate:
				kingStrikes = null;
				
				final List<BaseMove> save2 = new ArrayList<>(board.getTurns()); //clone
				
				ChessPiece.PieceColor wonColor2 = board.getCurrentPlayerColor() == ChessPiece.PieceColor.Black ?
					ChessPiece.PieceColor.White:
					ChessPiece.PieceColor.Black;
				
				new ConclusionDialog(
					getContext(),
					wonColor2.name() + " You just won wow\n" +
						"Enter title for this game",
					(input) -> {
						GamesActivity.gameDB.insertGame(
							new GameDB.Game(
								input,
								save2
							)
						);
						((Activity) getContext()).finish();
					}
				);
				updateGridCells();
				freeze();
				
				break;
		}
		undoneMoves.clear();
		selectedPieceMoves = null;
		updateGridCells();
	}
	
	public void undo() {
		if (isPlayback || undoneMoves.size() < MAX_UNDO) {
			BaseMove move = board.undo();
			if (move != null) { undoneMoves.add(move); }
			selectedPieceMoves = null;
			updateGridCells();
		}
	}
	
	public void redo() {
		if (!undoneMoves.isEmpty()) {
			board.redo(undoneMoves.remove(undoneMoves.size() - 1));
			
			kingStrikes = board.kingStrikers(board.getCurrentPlayerColor());
			selectedPieceMoves = null;
			
			updateGridCells();
		}
	}
	
	public void playBack(List<BaseMove> turns) {
		for (int i = 0; i < turns.size() ; i++) {
			undoneMoves.add(i, turns.get((turns.size() - 1) - i));
		}
		
		updateGridCells();
	}
	
	private void freeze(){
		isPlayback = true;
		for (int i = 0; i < pieceItems.length ; i++) {
			for (int j = 0; j < pieceItems[i].length; j++) {
				pieceItems[i][j].setClickable(false);
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}
}
