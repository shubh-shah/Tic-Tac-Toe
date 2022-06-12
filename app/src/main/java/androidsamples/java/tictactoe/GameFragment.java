package androidsamples.java.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.List;

import androidsamples.java.tictactoe.entities.GameResult;
import androidsamples.java.tictactoe.entities.GameStatus;
import androidsamples.java.tictactoe.entities.GameType;
import androidsamples.java.tictactoe.model.GameViewModel;
import androidsamples.java.tictactoe.model.GameViewModelFactory;
import androidsamples.java.tictactoe.model.UserViewModel;

public class GameFragment extends Fragment {
  private static final String TAG = "GameFragment";
  private UserViewModel userVM;
  private GameViewModel gameVM;
  private static final int GRID_SIZE = 9;

  private final Button[] mButtons = new Button[GRID_SIZE];
  private NavController mNavController;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    userVM = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

    setHasOptionsMenu(true); // Needed to display the action menu for this fragment

    // Extract the argument passed with the action in a type-safe way
    GameFragmentArgs args = GameFragmentArgs.fromBundle(getArguments());
    Log.d(TAG, "New game type = " + args.getGameType() + "Mail" + userVM.getCurrentUserInfo().getValue().getMailId());
    GameViewModelFactory factory;
    if (getString(R.string.two_player).equals(args.getGameType())) {
      factory = new GameViewModelFactory(GameType.TwoPlayer, null, userVM.getCurrentUserInfo().getValue().getMailId());
    }
    else if(getString(R.string.one_player).equals(args.getGameType())){
      factory = new GameViewModelFactory(GameType.SinglePlayer, null, userVM.getCurrentUserInfo().getValue().getMailId());
    }
    else {
      factory = new GameViewModelFactory(GameType.TwoPlayer, args.getGameType(), userVM.getCurrentUserInfo().getValue().getMailId());
    }
    gameVM = new ViewModelProvider(this, factory).get(GameViewModel.class);

    // Handle the back press by adding a confirmation dialog
    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
      @Override
      public void handleOnBackPressed() {
        Log.d(TAG, "Back pressed");

        // TODO show dialog only when the game is still in progress
        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
            .setTitle(R.string.confirm)
            .setMessage(R.string.forfeit_game_dialog_message)
            .setPositiveButton(R.string.yes, (d, which) -> {
              gameVM.forfeitGame();
              handleGameFinished(GameResult.Loss);
            })
            .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
            .create();
        dialog.show();
      }
    };
    requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_game, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mNavController = Navigation.findNavController(view);

    mButtons[0] = view.findViewById(R.id.button0);
    mButtons[1] = view.findViewById(R.id.button1);
    mButtons[2] = view.findViewById(R.id.button2);

    mButtons[3] = view.findViewById(R.id.button3);
    mButtons[4] = view.findViewById(R.id.button4);
    mButtons[5] = view.findViewById(R.id.button5);

    mButtons[6] = view.findViewById(R.id.button6);
    mButtons[7] = view.findViewById(R.id.button7);
    mButtons[8] = view.findViewById(R.id.button8);

    disableAllButtons();

    for (int i = 0; i < mButtons.length; i++) {
      int finalI = i;
      mButtons[i].setOnClickListener(v -> {
        disableAllButtons();
        Log.d(TAG, "Button " + finalI/3 + " " + finalI%3 + " clicked");
        if (!gameVM.makeMove(finalI/3, finalI%3)) {
          Activity activity = getActivity();
          if (activity != null) {
            Toast.makeText(getContext(), "Invalid Move", Toast.LENGTH_SHORT).show();
          }
          enableAllButtons();
        }
      });
    }


    gameVM.getGameLiveData().observe(getViewLifecycleOwner(),
            game -> {
              Log.i(TAG, game.getStatus()+String.valueOf(gameVM.getPlayerToken().equals(game.getCurrentPlayerToken())));
              if(game.getStatus().equals(GameStatus.GameStarted) && gameVM.getPlayerToken().equals(game.getCurrentPlayerToken())) {
                enableAllButtons();
              }
              updateBoard(game.getGameBoard());

              if (game.getStatus().equals(GameStatus.Finished)) {
                GameResult result = gameVM.getGameResult(game);
                if (gameVM.getPlayerToken().equals(game.getCurrentPlayerToken())){
                  gameVM.removeGame();
                }
                handleGameFinished(result);
              }
              else if (game.getStatus().equals(GameStatus.Forfeit) && gameVM.getPlayerToken().equals(game.getCurrentPlayerToken()) ){
                gameVM.removeGame();
                handleGameFinished(GameResult.Win);
              }
            });
  }

  private void handleGameFinished(GameResult result) {
    Log.i(TAG, "Register Outcome");
    Activity activity = getActivity();
    if (activity != null) {
      Toast.makeText(activity, result.getMessage(), Toast.LENGTH_SHORT).show();
    }
    userVM.updateScore(result);
    mNavController.popBackStack();
  }

  private void updateBoard(List<List<String>> gameBoard) {
    for(int i=0;i<3;i++){
      for(int j=0;j<3;j++){
        mButtons[i*3+j].setText(gameBoard.get(i).get(j));
      }
    }
  }

  private void disableAllButtons() {
    Log.i(TAG, "Disable Buttons");
    for(int j=0;j<9;j++) {
      mButtons[j].setEnabled(false);
    }
  }

  private void enableAllButtons() {
    Log.i(TAG, "Enable Buttons");
    for(int j=0;j<9;j++) {
      mButtons[j].setEnabled(true);
    }
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_logout, menu);
    // this action menu is handled in MainActivity
  }
}