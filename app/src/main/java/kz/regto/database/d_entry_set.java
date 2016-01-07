package kz.regto.database;

public class d_entry_set {
    //Инкриментный идентификатор
    private int sys_id;
    //Ид ставки
    private int log_id;
    //Цифра ставки
    private int chip_number;
    //Значение ставки
    private int entry_value;
    //Уникальный Ид ставки на поле
    private int entry_id;
    //На что делим
    private int divided_by;
    //х поля
    private int x;
    //у поля
    private int y;
    //Ид текущей игры
    private int game_id;
    //Сумма ставки
    private int sum;
    //Пакет ставки, т.е. как ставилась ставка, одиночно, или в ставку входил сет, умножение на 2, возвращение с предыдущей ставки
    private int EntryPackId;

    public d_entry_set(){}
    public d_entry_set(int i_chip_number,int i_log_id, int idV, int i_divided_by, int iX,int iY, int i_game_id, int i_sum, int ientry_value, int iEntryPackId){
        chip_number = i_chip_number;
        log_id = i_log_id;
        entry_id = idV;
        divided_by = i_divided_by;
        x= iX;
        y=iY;
        game_id = i_game_id;
        sum = i_sum;
        entry_value = ientry_value;
        EntryPackId = iEntryPackId;
    }

    public int getEntryPackId() {
        return EntryPackId;
    }

    public void setEntryPackId(int entryPackId) {
        EntryPackId = entryPackId;
    }

    public int getSys_id() {
        return sys_id;
    }

    public void setSys_id(int sys_id) {
        this.sys_id = sys_id;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getSum() {
        return sum;
    }

    public int getX() {
        return x;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }

    public int getGame_id() {
        return game_id;
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
