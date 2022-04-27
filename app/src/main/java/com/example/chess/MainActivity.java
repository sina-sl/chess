package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

  private Button btnStartGame;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);




    btnStartGame = findViewById(R.id.btn_start_game);
    btnStartGame.setOnClickListener((btn) -> {
      if ( connectToServer() ){
        startActivity(new Intent(this,ChessActivity.class));
      }
    });


  }

  private boolean connectToServer(){
    /// connect to server
    return true;
  }

}