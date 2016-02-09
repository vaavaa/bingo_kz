package kz.regto.database;

/**
 * id_sys INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT
 game_id INTEGER NOT NULL
 entry_sum INTEGER NOT NULL
 entry_win INTEGER NOT NULL
 lay INTEGER NOT NULL
 id_place INTEGER NOT NULL
 x INTEGER NOT NULL
 y INTEGER NOT NULL
 lay_sum INTEGER NOT NULL
 id_pack INTEGER NOT NULL, "
 */
public class d_entry {
    private int id_sys;
    private int entry_sum;
    private int entry_win;
    private int lay;
    private int lay_sum;
    private int id_pack;
    private int id_place;
    private int x;
    private int y;



    public d_entry(){}
    public d_entry(int id_sys, int entry_sum, int entry_win, int lay, int lay_sum, int id_pack, int id_place, int x, int y){
        this.id_sys = id_sys;
        this.entry_sum = entry_sum;
        this.entry_win = entry_win;
        this.lay = lay;
        this.lay_sum = lay_sum;
        this.id_pack = id_pack;
        this.id_place = id_place;
        this.x = x;
        this.y = y;
    }

    public int getId_pack() {
        return id_pack;
    }

    public int getLay_sum() {
        return lay_sum;
    }

    public int getEntry_sum() {
        return entry_sum;
    }
    public int getEntry_win() {
        return entry_win;
    }

    public int getId_place() {
        return id_place;
    }

    public int getId_sys() {
        return id_sys;
    }

    public int getLay() {
        return lay;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setId_pack(int id_pack) {
        this.id_pack = id_pack;
    }

    public void setLay_sum(int lay_sum) {
        this.lay_sum = lay_sum;
    }

    public void setEntry_sum(int entry_sum) {
        this.entry_sum = entry_sum;
    }

    public void setEntry_win(int entry_win) {
        this.entry_win = entry_win;
    }

    public void setId_place(int id_place) {
        this.id_place = id_place;
    }

    public void setId_sys(int id_sys) {
        this.id_sys = id_sys;
    }

    public void setLay(int lay) {
        this.lay = lay;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

}
