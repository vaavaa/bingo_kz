package kz.regto.database;

/**
 * Created by Старцев on 23.11.2015.
 */
public class d_game {
    private int id;
    private int win_ball=-1;
    private int state;
    private int device_id;
    private String gameCode;
    private String dtime;
    private int server_game_id;

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public int getId() {
        return id;
    }

    public int getDevice_id() {
        return device_id;
    }

    public int getState() {
        return state;
    }

    public int getWin_ball() {
        return win_ball;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setWin_ball(int win_ball) {
        this.win_ball = win_ball;
    }

    public String getDtime() {
        return dtime;
    }

    public void setDtime(String dtime) {
        this.dtime = dtime;
    }

    public int getServer_game_id() {
        return server_game_id;
    }

    public void setServer_game_id(int server_game_id) {
        this.server_game_id = server_game_id;
    }
}
