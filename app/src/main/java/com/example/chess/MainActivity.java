package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

  public static final String IS_PLAYBACK = "IS_PLAYBACK";

  public static GameDB gameDB = new GameDB();
  public static GameDB.Game currentGame;

  private Button btnStartGame, btnDateSort, btnNameSort;
  private RecyclerView recGame;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    recGame = findViewById(R.id.rec_games);
    btnDateSort = findViewById(R.id.btn_date);
    btnNameSort = findViewById(R.id.btn_name);
    btnStartGame = findViewById(R.id.btn_start_game);

    btnNameSort.setOnClickListener(v -> gameDB.sortByName());

    btnDateSort.setOnClickListener(v -> gameDB.sortByDate());

    btnStartGame.setOnClickListener((btn) -> {
      Intent intent = new Intent(this, ChessActivity.class);
      intent.putExtra(IS_PLAYBACK, false);
      startActivity(intent);
    });

    recGame.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    recGame.setAdapter(new GamesListAdaptor(gameDB, position -> {
      currentGame = gameDB.getGame(position);
      Intent intent = new Intent(this, ChessActivity.class);
      intent.putExtra(IS_PLAYBACK, true);
      startActivity(intent);
    }));

    gameDB.setOnItemChange(new GameDB.OnItemChange() {
      @Override
      public void onInsert(int position) {
        recGame.getAdapter().notifyItemInserted(position);
      }

      @SuppressLint("NotifyDataSetChanged")
      @Override
      public void onSort() {
        recGame.getAdapter().notifyDataSetChanged();
      }
    });

  }
}