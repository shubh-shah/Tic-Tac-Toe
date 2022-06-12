package androidsamples.java.tictactoe.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import androidsamples.java.tictactoe.entities.GameType;

public class GameViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final GameType gameType;
    private final String gameId;
    private final String mailId;

    public GameViewModelFactory(GameType gameType, String gameId, String mailId) {
        this.gameType = gameType;
        this.gameId = gameId;
        this.mailId = mailId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GameViewModel(gameType, gameId, mailId);
    }
}
