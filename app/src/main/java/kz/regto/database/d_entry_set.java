package kz.regto.database;

/**
 * Created by Старцев on 23.11.2015.
 */
public class d_entry_set {
    int log_id;
    int chip_number;
    int entry_value;
    int entry_id;
    int divided_by;
    int x;
    int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getChip_number() {
        return chip_number;
    }

    public int getDivided_by() {
        return divided_by;
    }

    public int getEntry_id() {
        return entry_id;
    }

    public int getEntry_value() {
        return entry_value;
    }

    public int getLog_id() {
        return log_id;
    }

    public void setChip_number(int chip_number) {
        this.chip_number = chip_number;
    }

    public void setDivided_by(int divided_by) {
        this.divided_by = divided_by;
    }

    public void setEntry_id(int entry_id) {
        this.entry_id = entry_id;
    }

    public void setEntry_value(int entry_value) {
        this.entry_value = entry_value;
    }

    public void setLog_id(int log_id) {
        this.log_id = log_id;
    }

}