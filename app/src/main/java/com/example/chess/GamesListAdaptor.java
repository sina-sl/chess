package com.example.chess;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GamesListAdaptor extends RecyclerView.Adapter<GamesListAdaptor.ViewHolder> {

  public static class ViewHolder extends RecyclerView.ViewHolder{

    TextView txtTitle;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      txtTitle = itemView.findViewById(R.id.txt_title);
    }
  }

  public interface OnItemClick{
    void onItemClick(int position);
  }

  private GameDB gameDB;
  private OnItemClick  onItemClick;

  public GamesListAdaptor(GameDB gameDB, OnItemClick onItemClick) {
    this.gameDB = gameDB;
    this.onItemClick = onItemClick;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.game_item,parent,false));
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.txtTitle.setText(gameDB.getGame(position).getTitle());
    holder.itemView.setOnClickListener((view)-> onItemClick.onItemClick(position));
  }

  @Override
  public int getItemCount() {
    return gameDB.size();
  }
}
