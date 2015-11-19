package kz.regto.bingo;

import android.util.Log;

/**
 * Created by spt on 06.11.2015.
 */
public class ChipLog {
    int NumberChip=-1;
    int Entry=0;
    int id=0;
    int divideBy=1;
    int x;
    int y;

    ChipLog(int chip, int entry, int lid,int dby, int x_in, int y_in){
        NumberChip=chip;
        Entry=entry;
        id=lid;
        divideBy=dby;
        x=x_in;
        y=y_in;

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getEntry() {
        return Entry;
    }

    public int getNumberChip() {
        return NumberChip;
    }

    public int getId() {
        return id;
    }

    public int getDivideBy() {
        return divideBy;
    }
}
