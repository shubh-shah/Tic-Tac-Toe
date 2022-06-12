package androidsamples.java.tictactoe.entities;

import com.google.firebase.database.IgnoreExtraProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@IgnoreExtraProperties
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private String mailId;

    private int wins;

    private int losses;

    private int ties;

    public void registerLoss(){
        losses++;
    }

    public void registerWin(){
        wins++;
    }

    public void registerTie(){
        ties++;
    }
}
