package androidsamples.java.tictactoe.entities;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@IgnoreExtraProperties
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Exclude private String gameID;

    private List<List<String>> gameBoard = new ArrayList<>();

    private String currentPlayerToken;

    private GameStatus status;

    @Builder
    public Game(String gameID, String currentPlayerToken, GameStatus status) {
        this.gameID = gameID;
        this.currentPlayerToken = currentPlayerToken;
        this.status = status;
        for(int i=0;i<3;i++){
            ArrayList<String> temp = new ArrayList<>();
            for (int j=0;j<3;j++){
                temp.add("");
            }
            gameBoard.add(temp);
        }
    }

    public int numEmptySlots() {
        int emptySlots = 0;
        for (List<String> row : gameBoard) {
            for (String element : row) {
                if (element.equals("")) {
                    emptySlots++;
                }
            }
        }
        return emptySlots;
    }

    public boolean makeMove(int i, int j, String token) {
        if (gameBoard.get(i).get(j).equals("")){
            gameBoard.get(i).set(j, token);
            return true;
        }
        else {
            return false;
        }
    }

    public void makeRandomMove(String token) {
        int num = new Random().nextInt(numEmptySlots());
        for (List<String> row : gameBoard) {
            for (int i=0;i<row.size();i++) {
                if(row.get(i).equals("")) {
                    if(num==0) {
                        row.set(i, token);
                        return;
                    }
                    num--;
                }
            }
        }
    }
}
