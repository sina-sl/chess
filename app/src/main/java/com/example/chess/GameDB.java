package com.example.chess;

import com.example.chess.logic.BaseMove;

import java.util.ArrayList;
import java.util.List;

public class GameDB {

  public static class Game{

    private String title;
    private List<BaseMove> moves;

    public Game(String title, List<BaseMove> moves){
      this.title = title;
      this.moves = moves;
    }

    public String getTitle() {
      return title;
    }

    public List<BaseMove> getMoves() {
      return moves;
    }
  }

  public interface OnInsertNewItem{
    void onInsert( int position);
  }

  private List<Game> games;
  private OnInsertNewItem onInsertNewItem;

  public GameDB(){
    games = new ArrayList<>();
  }

  public void setOnInsertNewItem(OnInsertNewItem onInsertNewItem) {
    this.onInsertNewItem = onInsertNewItem;
  }

  public void insertGame(Game game){
    if (games.add(game))
      onInsertNewItem.onInsert(games.size() - 1);
  }

  public int size(){
    return games.size();
  }

  public Game getGame(int index){
    return games.get(index);
  }

}
