package androidsamples.java.tictactoe.entities;

public enum GameResult {
    Loss("Sorry!"),
    Win("Congratulations!"),
    Tie("Draw"),
    Unfinished("Unfinished");

    private String result;

    GameResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return result;
    }
}