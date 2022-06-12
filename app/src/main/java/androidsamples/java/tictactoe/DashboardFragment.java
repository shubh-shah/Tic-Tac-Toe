package androidsamples.java.tictactoe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import androidsamples.java.tictactoe.entities.OpenGame;
import androidsamples.java.tictactoe.model.GameListViewModel;
import androidsamples.java.tictactoe.model.UserViewModel;

public class DashboardFragment extends Fragment {

  private static final String TAG = "DashboardFragment";
  private UserViewModel userVM;
  private GameListViewModel gameListViewModel;
  private TextView scoreText;
//  private NavController mNavController;
  private OpenGamesAdapter adapter;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public DashboardFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    userVM = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    gameListViewModel = new ViewModelProvider(this).get(GameListViewModel.class);

    setHasOptionsMenu(true); // Needed to display the action menu for this fragment
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_dashboard, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    scoreText = view.findViewById(R.id.txt_score);

    if(!userVM.checkIfLoggedIn()){
      NavDirections action = DashboardFragmentDirections.actionNeedAuth();
      Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(action);
    }
    else{
      RecyclerView gamesList = view.findViewById(R.id.list);
      gamesList.setLayoutManager(new LinearLayoutManager(getActivity()));
      adapter = new OpenGamesAdapter();
      gamesList.setAdapter(adapter);
    }

    // Show a dialog when the user clicks the "new game" button
    view.findViewById(R.id.fab_new_game).setOnClickListener(v -> {

      // A listener for the positive and negative buttons of the dialog
      DialogInterface.OnClickListener listener = (dialog, which) -> {
        String gameType = "No type";
        if (which == DialogInterface.BUTTON_POSITIVE) {
          gameType = getString(R.string.two_player);
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
          gameType = getString(R.string.one_player);
        }
        Log.d(TAG, "New Game: " + gameType);

        // Passing the game type as a parameter to the action
        // extract it in GameFragment in a type safe way
        NavDirections action = DashboardFragmentDirections.actionGame(gameType);
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(action);
      };

      // create the dialog
      AlertDialog dialog = new AlertDialog.Builder(requireActivity())
          .setTitle(R.string.new_game)
          .setMessage(R.string.new_game_dialog_message)
          .setPositiveButton(R.string.two_player, listener)
          .setNegativeButton(R.string.one_player, listener)
          .setNeutralButton(R.string.cancel, (d, which) -> d.dismiss())
          .create();
      dialog.show();
    });


    ChildEventListener gameListChildEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

        OpenGame newGame = dataSnapshot.getValue(OpenGame.class);
        adapter.insertGame(newGame);
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

        OpenGame newGame = dataSnapshot.getValue(OpenGame.class);
        adapter.removeGame(newGame);
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
      }

      @Override
      public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
        Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "Game Sync Failed", databaseError.toException());
        Toast.makeText(requireActivity(), "Game Sync Failed", Toast.LENGTH_SHORT).show();
      }
    };

    gameListViewModel.setPersistentGameListListener(gameListChildEventListener);
  }

  @Override
  public void onResume() {
    super.onResume();

    userVM.getCurrentUserInfo().observe(getViewLifecycleOwner(), userInfo -> {
      if (userInfo != null) {
        scoreText.setText(getString(R.string.scoreText, userInfo.getWins(), userInfo.getLosses() ,userInfo.getTies()));
      }
      else {
        Toast.makeText(requireActivity(), userVM.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
    userVM.retrieveUserInfo();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    gameListViewModel.removePersistentGameListListener();
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_logout, menu);
    // this action menu is handled in MainActivity
  }
}