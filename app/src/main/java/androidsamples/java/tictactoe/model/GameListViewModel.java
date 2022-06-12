package androidsamples.java.tictactoe.model;

import androidx.lifecycle.ViewModel;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameListViewModel extends ViewModel {

    private final DatabaseReference openGameDatabase;

    private ChildEventListener childEventListener;

    public GameListViewModel() {
        openGameDatabase = FirebaseDatabase.getInstance("https://tic-tac-toe-shubh-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("open_games");
    }

    public void setPersistentGameListListener(ChildEventListener childEventListener) {
        this.childEventListener = childEventListener;
        openGameDatabase.addChildEventListener(childEventListener);
    }

    public void removePersistentGameListListener() {
        openGameDatabase.removeEventListener(childEventListener);
    }
}
