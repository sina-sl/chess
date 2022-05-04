package com.example.chess.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public class PieceItem extends FrameLayout {
	
	public static class PieceResources {
		@DrawableRes
		private int pieceRes, background;
		
		public PieceResources(@DrawableRes int pieceRes, @DrawableRes int background) {
			this.pieceRes = pieceRes;
			this.background = background;
		}
		
		public int getPieceRes() {
			return pieceRes;
		}
		
		public int getBackground() {
			return background;
		}
	}
	
	private final static FrameLayout.LayoutParams LAYOUT_PARAMS = new FrameLayout.LayoutParams(
		ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
	) {{
		this.gravity = Gravity.CENTER;
	}};
	
	private PieceResources pieceResources;
	private ImageView pieceImage;
	
	public PieceItem(@NonNull Context context) {
		super(context);
		createPieceHolder();
	}
	
	private void createPieceHolder() {
		pieceImage = new ImageView(getContext());
		pieceImage.setLayoutParams(LAYOUT_PARAMS);
		pieceImage.setPadding(5, 5, 5, 5);
		addView(pieceImage);
	}
	
	@SuppressLint("UseCompatLoadingForDrawables")
	public void setPieceResources(PieceResources pieceResources) {
		this.pieceResources = pieceResources;
		pieceImage.setBackground(getContext().getDrawable(pieceResources.background));
		pieceImage.setImageResource(pieceResources.pieceRes);
	}
	
	public PieceResources getPieceResources() {
		return pieceResources;
	}
}
