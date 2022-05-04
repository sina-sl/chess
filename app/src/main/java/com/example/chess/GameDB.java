package com.example.chess;

import com.example.chess.logic.BaseMove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GameDB {

  public static class Game implements Comparable<Game>{

    private Long date;
    private String title;
    private List<BaseMove> moves;

    public Game(String title, long date, List<BaseMove> moves) {
      this.title = title;
      this.moves = moves;
      this.date = date;
    }

    public String getTitle() {
      return title;
    }

    public List<BaseMove> getMoves() {
      return moves;
    }

    public Long getDate() {
      return date;
    }

    @Override
    public int compareTo(Game o) {
      return this.title.compareTo(o.title);
    }
  }

  public interface OnItemChange {
    void onInsert(int position);
    void onSort();
  }

  private List<Game> games;
  private OnItemChange onItemChange;

  private boolean isSortedByName = false;

  public GameDB() {
    games = new ArrayList<>();
  }

  public void setOnItemChange(OnItemChange onItemChange) {
    this.onItemChange = onItemChange;
  }

  public void insertGame(Game game) {
    if (isSortedByName){
      int index = Collections.binarySearch(games,game);
      games.add(index<0?~index:index,game);
      onItemChange.onInsert(index);
    }else {
      if (games.add(game))
        onItemChange.onInsert(games.size() - 1);
    }
  }

  public void sortByName() {
    isSortedByName = true;
    games.sort(Comparator.comparingInt(o -> o.title.charAt(0)));
    onItemChange.onSort();
  }

  public void sortByDate() {
    isSortedByName = false;
    games.sort(Comparator.comparingLong(o -> o.date));
    onItemChange.onSort();
  }

  public int size() {
    return games.size();
  }

  public Game getGame(int index) {
    return games.get(index);
  }

}
