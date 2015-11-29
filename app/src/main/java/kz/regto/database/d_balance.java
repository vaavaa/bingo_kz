package kz.regto.database;

/**
 * Created by Старцев on 24.11.2015.
 */
public class d_balance {
    private int game_id;
    private int operation;
    private int sum;

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }

    public int getGame_id() {
        return game_id;
    }

    public int getOperation() {
        return operation;
    }

    public int getSum() {
        return sum;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
