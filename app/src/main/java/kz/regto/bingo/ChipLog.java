package kz.regto.bingo;

import android.util.Log;

/**
 * Created by spt on 06.11.2015.
 */
public class ChipLog {
    int NumberChip=-1;
    int Entry=0;
    int id=0;

    ChipLog(int chip, int entry, int lid){
        NumberChip=chip;
        Entry=entry;
        id=lid;
        Log.v("1",Integer.toString(NumberChip).concat("/").concat(Integer.toString(entry)));
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
}
