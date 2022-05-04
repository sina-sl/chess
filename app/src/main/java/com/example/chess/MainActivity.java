package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chess.R;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void newGame(View view) {
		Intent intent = new Intent(this, ChessActivity.class);
		startActivity(intent);
	}
	
	public void loadGame(View view) {
		Intent intent = new Intent(this, GamesActivity.class);
		startActivity(intent);
	}
	
}