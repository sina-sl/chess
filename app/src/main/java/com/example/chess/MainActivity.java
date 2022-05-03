package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

  public static final String IS_PLAYBACK = "IS_PLAYBACK";

  public static GameDB gameDB = new GameDB();
  public static GameDB.Game currentGame;

  private Button btnStartGame;
  private RecyclerView recGame;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    recGame = findViewById(R.id.rec_games);
    btnStartGame = findViewById(R.id.btn_start_game);

    recGame.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    btnStartGame.setOnClickListener((btn) -> {
      Intent intent = new Intent(this, ChessActivity.class);
      intent.putExtra(IS_PLAYBACK, false);
      startActivity(intent);
    });

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
}