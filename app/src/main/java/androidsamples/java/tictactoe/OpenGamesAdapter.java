package androidsamples.java.tictactoe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import androidsamples.java.tictactoe.entities.OpenGame;

public class OpenGamesAdapter extends RecyclerView.Adapter<OpenGamesAdapter.ViewHolder> {
  private List<OpenGame> gameList;

  public OpenGamesAdapter() {
    gameList = new ArrayList<>();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
    if (gameList != null) {
      OpenGame game = gameList.get(position);
      holder.mIdView.setText("Opponent: "+game.getCreatorPlayerMail());
      holder.mContentView.setText("Game ID: "+game.getGameID());
    }
  }

  @Override
  public int getItemCount() {
    return (gameList == null) ? 0 : gameList.size();
  }

  public void removeGame(OpenGame game) {
    int index = gameList.indexOf(game);
    gameList.remove(index);
    notifyItemRemoved(index);
  }

  public void insertGame(OpenGame game) {
    gameList.add(game);
    notifyItemInserted(gameList.size()-1);
  }

  public void setGames(List<OpenGame> games) {
    gameList = games;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public final View mView;
    public final TextView mIdView;
    public final TextView mContentView;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      mIdView = view.findViewById(R.id.item_number);
      mContentView = view.findViewById(R.id.content);

      view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      DashboardFragmentDirections.ActionGame action = DashboardFragmentDirections.actionGame(gameList.get(getBindingAdapterPosition()).getGameID());
      Navigation.findNavController(v).navigate(action);
    }

    @NonNull
    @Override
    public String toString() {
      return super.toString() + " '" + mContentView.getText() + "'";
    }
  }
}