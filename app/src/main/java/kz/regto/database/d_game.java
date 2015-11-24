package kz.regto.database;

import java.util.Date;

/**
 * Created by Старцев on 23.11.2015.
 */
public class d_game {
    int id;
    int win_ball;
    int state;
    int device_id;
    String dtime;


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
}
