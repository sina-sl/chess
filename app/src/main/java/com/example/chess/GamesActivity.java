package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GamesActivity extends AppCompatActivity {
	
	public static final String IS_PLAYBACK = "IS_PLAYBACK";
	
	public static final GameDB gameDB = new GameDB();
	public static GameDB.Game currentGame;
	
	private RecyclerView recGame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_games);
		
		recGame = findViewById(R.id.savedGames);
		
		recGame.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
		
		recGame.setAdapter(new GamesListAdaptor(gameDB, position -> {
			currentGame = gameDB.getGame(position);
			Intent intent = new Intent(this, ChessActivity.class);
			intent.putExtra(IS_PLAYBACK, true);
			startActivity(intent);
		}));
		
		gameDB.setOnInsertNewItem(position -> {
			recGame.getAdapter().notifyItemInserted(position);
		});
		
	}
	
	public void newGame(View view) {
		Intent intent = new Intent(this, ChessActivity.class);
		startActivity(intent); // IS_PLAYBACK = false
		finish();
	}
}