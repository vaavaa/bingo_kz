package kz.regto.json;

/**
 * Created by Старцев on 25.11.2015.
 */
public class CurrentTime {
    private long currenttime;
    private int winnumber;
    private long finalCounter;
    private String game_code;
    private int game_id;

    public long getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(long currenttime) {
        this.currenttime = currenttime;
    }

    public int getWinnumber() {
        return winnumber;
    }

    public void setWinnumber(int winnumber) {
        this.winnumber = winnumber;
    }

    public long getFinalCounter() {
        return finalCounter;
    }
    public void setFinalCounter(long finalCounter) {
        this.finalCounter = finalCounter;
    }

    public int getGame_id() {
        return game_id;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }

    public String getGame_code() {
        return game_code;
    }

    public void setGame_code(String game_code) {
        this.game_code = game_code;
    }
}
