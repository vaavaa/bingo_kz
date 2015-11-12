package kz.regto.bingo;

/**
 * Created by spt on 06.11.2015.
 */
public class ChipLogLimit {
    int Entry=0;
    int id=0;

    ChipLogLimit(int entry, int lid){
        Entry=entry;
        id=lid;
    }

    public int getEntry() {
        return Entry;
    }
    public int getId() {
        return id;
    }
}
