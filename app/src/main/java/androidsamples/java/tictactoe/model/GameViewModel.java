package androidsamples.java.tictactoe.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidsamples.java.tictactoe.entities.Game;
import androidsamples.java.tictactoe.entities.GameResult;
import androidsamples.java.tictactoe.entities.GameStatus;
import androidsamples.java.tictactoe.entities.GameType;
import androidsamples.java.tictactoe.entities.OpenGame;
import lombok.Getter;

public class GameViewModel extends ViewModel {

    private final DatabaseReference gameDatabase;

    private final DatabaseReference openGameDatabase;

    private final GameType gameType;

    private final String mailId;

    @Getter
    private final MutableLiveData<Game> gameLiveData = new MutableLiveData<>();

    @Getter
    private String playerToken;

    public GameViewModel(GameType type, String gameId, String mailId) {
        gameDatabase = FirebaseDatabase.getInstance("https://tic-tac-toe-shubh-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("games");
        openGameDatabase = FirebaseDatabase.getInstance("https://tic-tac-toe-shubh-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("open_games");
        this.gameType = type;
        this.mailId = mailId;
        if (gameType.equals(GameType.TwoPlayer)) {
            this.createOrJoinGame(gameId);
        }
        else {
            createSinglePlayerGame();
        }
    }

    private void createOrJoinGame(String gameId) {
        if (gameId==null){
            gameId = createMultiPlayerGame();
            createOpenGame(gameId);
        }
        else{
            openGameDatabase.child(gameId).removeValue();
            joinGame(gameId);
        }

        gameDatabase.child(gameId).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Game game = dataSnapshot.getValue(Game.class);
                        if (game!=null) {
                            game.setGameID(dataSnapshot.getKey());
                            gameLiveData.setValue(game);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("GameViewModel", "loadPost:onCancelled", databaseError.toException());
                    }
                });
    }

    private String createMultiPlayerGame() {
        this.playerToken = "X";
        Game newGame = Game.builder()
                .currentPlayerToken("X")
                .status(GameStatus.WaitingForPlayer)
                .build();

        DatabaseReference newGameReference = this.gameDatabase.push();
        newGameReference.setValue(newGame);
        newGame.setGameID(newGameReference.getKey());

        this.gameLiveData.setValue(newGame);

        return newGameReference.getKey();
    }

    private void createSinglePlayerGame() {
        this.playerToken = "X";
        Game newGame = Game.builder()
                .currentPlayerToken("X")
                .status(GameStatus.GameStarted)
                .build();

        this.gameLiveData.setValue(newGame);
    }

    private void createOpenGame(String gameId) {
        OpenGame openGame = new OpenGame(gameId, this.mailId);
        openGameDatabase.child(gameId).setValue(openGame);
    }

    private void joinGame(String gameId) {
        this.playerToken = "O";

        Log.i("GameViewModel", "Joining Game");

        OnCompleteListener<DataSnapshot> gameListOneTimeListener = task -> {
            if (!task.isSuccessful() || task.getResult()==null) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                Game joinedGame = task.getResult().getValue(Game.class);
                if(joinedGame==null){
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    joinedGame.setStatus(GameStatus.GameStarted);
                    joinedGame.setGameID(gameId);
                    this.gameDatabase.child(gameId).setValue(joinedGame);

                    this.gameLiveData.setValue(joinedGame);
                }
            }
        };

        this.gameDatabase.child(gameId).get().addOnCompleteListener(gameListOneTimeListener);
    }

    private String getOpponentToken() {
        if (getPlayerToken().equals("X")) {
            return "O";
        }
        else {
            return "X";
        }
    }

    public boolean makeMove(int i, int j) {
        Game currentGame = gameLiveData.getValue();

        if (currentGame!=null) {

            if(!currentGame.makeMove(i, j, playerToken))
                return false;

            currentGame.setCurrentPlayerToken(getOpponentToken());

            if (isGameFinished(currentGame)) {
                currentGame.setStatus(GameStatus.Finished);
            }

            gameLiveData.setValue(currentGame);

            if (gameType.equals(GameType.TwoPlayer))
                gameDatabase.child(currentGame.getGameID()).setValue(currentGame);
            else if (!isGameFinished(currentGame)) {
                makeRandomMove();
            }
            return true;
        }
        return false;
    }

    public void makeRandomMove() {
        Game currentGame = gameLiveData.getValue();

        if (currentGame!=null) {

            currentGame.makeRandomMove(getOpponentToken());

            currentGame.setCurrentPlayerToken(playerToken);

            if (isGameFinished(currentGame)) {
                currentGame.setStatus(GameStatus.Finished);
            }

            gameLiveData.setValue(currentGame);
        }
    }

    public boolean isGameFinished(Game game) {
        return !getGameResult(game).equals(GameResult.Unfinished);
    }

    private GameResult checkDiagonal(Game game, int startRow, int startCol, int incRow, int incCol) {
        boolean victory = true;
        boolean loss = true;
        int col = startCol;
        int row = startRow;
        while (row<3 && col<3 && row>=0 && col>=0) {
            if (!game.getGameBoard().get(row).get(col).equals(this.playerToken)) {
                victory = false;
            }
            if (!game.getGameBoard().get(row).get(col).equals(getOpponentToken())) {
                loss = false;
            }
            row+=incRow;
            col+=incCol;
        }
        if(victory)
            return GameResult.Win;
        if(loss)
            return GameResult.Loss;
        return GameResult.Unfinished;
    }

    public GameResult getGameResult(Game game) {
        // Horizontal Check
        for (int row=0; row<3; row++) {
            GameResult result = checkDiagonal(game, row, 0, 0, 1);
            if (!result.equals(GameResult.Unfinished))
                return result;
        }
        // Vertical Check
        for (int col=0;col<3;col++) {
            GameResult result = checkDiagonal(game, 0, col, 1, 0);
            if (!result.equals(GameResult.Unfinished))
                return result;
        }
        // Diagonal Check
        GameResult result = checkDiagonal(game, 0, 0, 1, 1);
        if (!result.equals(GameResult.Unfinished))
            return result;
        // Diagonal Check
        result = checkDiagonal(game, 0, 2, 1, -1);
        if (!result.equals(GameResult.Unfinished))
            return result;

        if (game.numEmptySlots()==0)
            return GameResult.Tie;

        return GameResult.Unfinished;
    }

    public void removeGame() {
        if (gameType.equals(GameType.TwoPlayer))
            gameDatabase.child(gameLiveData.getValue().getGameID()).removeValue();
    }

    public void forfeitGame() {
        Game currentGame = gameLiveData.getValue();

        if (currentGame!=null) {

            currentGame.setCurrentPlayerToken(getOpponentToken());

            if (gameType.equals(GameType.TwoPlayer)) {
                Log.i("GameViewModel", "Open Game Removed11");
                openGameDatabase.child(currentGame.getGameID()).removeValue();
                if(currentGame.getStatus().equals(GameStatus.GameStarted)) {
                    currentGame.setStatus(GameStatus.Forfeit);
                    gameDatabase.child(currentGame.getGameID()).setValue(currentGame);
                }
                else {
                    gameDatabase.child(currentGame.getGameID()).removeValue();
                }
            }
        }
    }
}
