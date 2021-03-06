package kz.regto.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int STATE_OPENED = 0;
    public static final int STATE_CLOSED = 1;
    public static final int STATE_NOTACTIVATED = -1;
    // Logcat tag
    private static final String LOG = DatabaseHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "bingo_db";
    
    private SQLiteDatabase db;
    private SQLiteDatabase dbr;
    private int state;

    // Table Create Statements
    private static final String _Game = "Game";
    private static final String CREATE_TABLE_Game = "CREATE TABLE Game (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "win_ball INTEGER NOT NULL DEFAULT '-1' , " +
            "state INTEGER NOT NULL DEFAULT '0' , " +
            "device_id INTEGER NOT NULL DEFAULT '0', " +
            "dtime DATETIME NOT NULL,"+
            "game_code VARCHAR(10),"+
            "server_game_id INTEGER NOT NULL)";

    private static final String _balance = "balance";
    private static final String CREATE_TABLE_balance = "CREATE TABLE balance " +
            "(id_balance INTEGER NOT NULL DEFAULT '0'," +
            " sum INTEGER NOT NULL, " +
            " dtime DATETIME NOT NULL DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')), " +
            " status INTEGER NOT NULL DEFAULT '0')";

    private static final String _returnGame = "returnGame";
    private static final String CREATE_TABLE_returnGame = "CREATE TABLE returnGame " +
            "(id_game INTEGER NOT NULL," +
            " dtime DATETIME NOT NULL DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')), " +
            " status INTEGER NOT NULL DEFAULT '0')";

    private static final String _device = "device";
    private static final String CREATE_TABLE_device = "CREATE TABLE device (device_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            " device_code VARCHAR(200) NOT NULL, status INTEGER NOT NULL DEFAULT '0', server_device_id INTEGER NOT NULL DEFAULT '-1', " +
            " type_id INTEGER NOT NULL DEFAULT '0', Comment VARCHAR(200) NOT NULL DEFAULT '', type_name VARCHAR(200) NOT NULL DEFAULT ''," +
            " game_limit INTEGER NOT NULL DEFAULT '0', game_mask VARCHAR(200) NOT NULL DEFAULT '')";

    private static final String _entry_set = "entry_set";
    private static final String CREATE_TABLE_entry_set = "    CREATE TABLE entry_set (\n" +
            "                    sys_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
            "                    log_id INTEGER NOT NULL, "+
            "                    chip_number INTEGER NOT NULL,\n" +
            "                    entry_value INTEGER NOT NULL,\n" +
            "                    entry_id INTEGER NOT NULL,\n" +
            "                    divided_by INTEGER NOT NULL, x INTEGER NOT NULL, y INTEGER NOT NULL," +
            "                    game_id INTEGER NOT NULL,"+
            "                    sum INTEGER NOT NULL," +
            "                    entry_pack_id INTEGER NOT NULL)";

    private static final String _entry = "entry";
    private static final String CREATE_TABLE_entry = "CREATE TABLE entry ( " +
            "                    id_sys INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "                    game_id INTEGER NOT NULL, "+
            "                    entry_sum INTEGER NOT NULL, " +
            "                    entry_win INTEGER NOT NULL, " +
            "                    lay INTEGER NOT NULL, " +
            "                    lay_sum INTEGER NOT NULL, " +
            "                    id_pack INTEGER NOT NULL, " +
            "                    id_place INTEGER NOT NULL, " +
            "                    x INTEGER NOT NULL," +
            "                    y INTEGER NOT NULL)";

    private static final String _entry_line = "entry_line";
    private static final String CREATE_TABLE_entry_line = "CREATE TABLE entry_line ( " +
            "                    id_line INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "                    game_id INTEGER NOT NULL, "+
            "                    entry_sum INTEGER NOT NULL, " +
            "                    id_pack INTEGER NOT NULL, " +
            "                    x INTEGER NOT NULL," +
            "                    y INTEGER NOT NULL)";

    private static final String _entry_details = "entry_details";
    private static final String CREATE_TABLE_entry_details = "CREATE TABLE entry_details ( " +
            "                    id_details INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "                    id_sys INTEGER NOT NULL, "+
            "                    divided_by INTEGER NOT NULL)";

    private static final String _settings = "settings";
    private static final String CREATE_TABLE_settings = "CREATE TABLE settings " +
            "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, settings_name VARCHAR(255) NOT NULL," +
            " settings_value VARCHAR(255) NOT NULL)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        state = STATE_NOTACTIVATED;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_Game);
        db.execSQL(CREATE_TABLE_balance);
        db.execSQL(CREATE_TABLE_device);
        db.execSQL(CREATE_TABLE_entry_set);
        db.execSQL(CREATE_TABLE_settings);
        db.execSQL(CREATE_TABLE_entry);
        db.execSQL(CREATE_TABLE_entry_details);
        db.execSQL(CREATE_TABLE_entry_line);
        db.execSQL(CREATE_TABLE_returnGame);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + _Game);
        db.execSQL("DROP TABLE IF EXISTS " + _balance);
        db.execSQL("DROP TABLE IF EXISTS " + _device);
        db.execSQL("DROP TABLE IF EXISTS " + _entry_set);
        db.execSQL("DROP TABLE IF EXISTS " + _settings);
        db.execSQL("DROP TABLE IF EXISTS " + _entry);
        db.execSQL("DROP TABLE IF EXISTS " + _entry_details);
        db.execSQL("DROP TABLE IF EXISTS " + _entry_line);
        db.execSQL("DROP TABLE IF EXISTS " + _returnGame);
        // create new tables
        onCreate(db);
    }

    public boolean openDB() {
        boolean bRet;
        try{
            db = this.getWritableDatabase();
            dbr = this.getReadableDatabase();
            bRet=true;
            state = STATE_OPENED;
        }
        catch (SQLException  ex){
            bRet=false;
        }
        return bRet;
    }

    public void close() {
        db.close();
        state = STATE_CLOSED;
    }
    public int getDBState() {
        return state;
    }


    // ------------------------ "settings" table methods ----------------//

    public boolean createNewSettings(d_settings dSettings) {
        int id_settings = 0;
        String insert_sql = "insert into settings ( settings_name, settings_value) " +
                "VALUES ('"+dSettings.getSettingsName()+"','"+dSettings.getSettingsValue()+"')";
        try {
            //Еcли там уже есть настрока, то обновим
            String Sql_Chk = "SELECT id FROM settings WHERE settings_name='"+dSettings.getSettingsName()+"'";
            Cursor c_ck = this.runResultedSQL(Sql_Chk);
            if (c_ck!=null && c_ck.moveToFirst()){
                id_settings = c_ck.getInt(c_ck.getColumnIndex("id"));
                dSettings.setId(id_settings);
                c_ck.close();
            }
            if (id_settings==0) {
                db.execSQL(insert_sql);
                String sql = "SELECT last_insert_rowid() as lastid";
                Cursor c = this.runResultedSQL(sql);
                if (c!=null && c.moveToFirst()){
                    dSettings.setId(c.getInt(c.getColumnIndex("lastid")));
                    c.close();
                }
                return true;
            }
            else {
                return this.updateSettings(dSettings);
            }

        }
        catch (Exception ex){
            return false;
        }
    }

    /**
     * get single settings by name
     * */
    public d_settings getSettings(String settings_name) {
        String selectQuery = "SELECT * FROM settings WHERE settings_name = '"+settings_name+"' limit 1";
        Cursor c = dbr.rawQuery(selectQuery, null);
        d_settings td = null;

        if( c != null && c.moveToFirst() ){
            td = new d_settings();
            td.setSettingsName(c.getString(c.getColumnIndex("settings_name")));
            td.setSettingsValue(c.getString(c.getColumnIndex("settings_value")));
            td.setId(c.getInt(c.getColumnIndex("id")));
            c.close();
        }
        return td;
    }

    /**
     * get single settings by id
     * */
    public d_settings getSettings(int id) {
        String selectQuery = "SELECT * FROM settings WHERE int = "+id+" limit 1";
        Cursor c = dbr.rawQuery(selectQuery, null);
        d_settings td = null;

        if( c != null && c.moveToFirst() ){
            td = new d_settings();
            td.setSettingsName(c.getString(c.getColumnIndex("settings_name")));
            td.setSettingsValue(c.getString(c.getColumnIndex("settings_value")));
            td.setId(c.getInt(c.getColumnIndex("id")));
            c.close();
        }
        return td;
    }

    /**
     * Updating a settings
     */
    public boolean updateSettings(d_settings dSettings) {
        boolean bReturn;
        String update_sql = "Update settings " +
                "SET settings_name ='"+dSettings.getSettingsName()+"', " +
                "settings_value = '"+dSettings.getSettingsValue() +"' "+
                "WHERE id = " +dSettings.getId();
        try {
            db.execSQL(update_sql);
            bReturn = true;
        }
        catch (Exception ex){
            bReturn = false;
        }
        return bReturn;
    }

    /**
     * Deleting a settings
     */
    public boolean deleteSettings(int id) {
        boolean bReturn;
        String delete_sql = "DELETE FROM settings \n"+
                "WHERE id=" + id;
        try {
            //SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(delete_sql);
            bReturn = true;
        }
        catch (Exception ex) {
            bReturn = false;
        }
        return bReturn;
    }
    /**
     * Deleting a settings by name
     */
    public boolean deleteSettings(String settings_name) {
        boolean bReturn;
        String delete_sql = "DELETE FROM settings \n"+
                "WHERE settings_name='" + settings_name+"'";
        try {
            db.execSQL(delete_sql);
            bReturn = true;
        }
        catch (Exception ex) {
            bReturn = false;
        }
        return bReturn;
    }

    // ------------------------ close "settings" table methods _---------//




    public d_balance createNewBalance(d_balance dBalance) {
        //Складываем текущий баланс с новым балансом
        String insert_sql = " insert into balance (id_balance,sum, status) " +
                "VALUES ("+dBalance.getOperation()+","+dBalance.getSum()+",0)";
        try {
            db.execSQL(insert_sql);
            return dBalance;}
        catch (Exception ex){
            return null;
        }

    }

    public d_balance UpdateBalanceFROMServer(d_balance dBalance) {
        //Складываем текущий баланс с новым балансом
        String selectQuery = "SELECT id_balance, sum FROM balance " +
                " WHERE id_balance="+dBalance.getOperation();
        Cursor cz = db.rawQuery(selectQuery, null);
        if (cz !=null && !cz.moveToNext()) {
            dBalance =  createNewBalance(dBalance);
            cz.close();
        }
        return dBalance;
    }

    public List<d_balance> getBalance(int status) {

        String selectQuery = "SELECT * FROM balance " +
                " WHERE status = "+status;
        List<d_balance> dBalance = new ArrayList<>();

        Cursor c = dbr.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                d_balance td = new d_balance();
                td.setOperation((c.getInt(c.getColumnIndex("id_balance"))));
                td.setSum(c.getInt(c.getColumnIndex("sum")));
                td.setDatetime(c.getInt(c.getColumnIndex("dtime")));
                td.setStatus(c.getInt(c.getColumnIndex("status")));
                dBalance.add(td);
            } while (c.moveToNext());
            c.close();
        }
        return dBalance;
    }

    public int getCurrentServerBalance() {
        String selectQuery = "SELECT TOTAL(balance.sum) as sum FROM balance " +
                " WHERE status = 0";
        int rValue=0;
        Cursor c = dbr.rawQuery(selectQuery, null);
        if( c != null && c.moveToFirst() ){
            rValue = c.getInt(c.getColumnIndex("sum"));
            c.close();
        }

        return rValue;
    }
    public boolean setCurrentServerBalanceFlag() {
        String UpdateQuery = "Update balance SET status = 1 " +
                " WHERE status = 0";
        try {
            db.execSQL(UpdateQuery);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }

    /**
     * Deleting a balance
     */
    public boolean DeleteServerBalance() {
        boolean bReturn;
        String delete_sql = "DELETE FROM balance";
        try {
            db.execSQL(delete_sql);
            bReturn = true;
        }
        catch (Exception ex){
            bReturn=false;
        }
        return bReturn;
    }

    public boolean updateBalance(d_balance dBalance) {
        boolean bReturn;
        String update_sql = "Update balance " +
                "SET sum ="+dBalance.getSum()+", " +
                "id_balance ="+dBalance.getOperation() +
                " WHERE id_balance=" +dBalance.getOperation();
        try {
            db.execSQL(update_sql);
            bReturn = true;
        }
        catch (Exception ex){
            bReturn = false;
        }
        return bReturn;
    }


    // ------------------------ end "balance" table methods ----------------//


    // ------------------------ "device" table methods ----------------//
    /**
     * Creating a device
     * Device Status
     * 0 - inactive
     * 1- active
     */
    public boolean createNewDevice(d_device dDevice) {
        boolean bReturn;
//        device_code VARCHAR(200)
//        status INTEGER
//        server_device_id INTEGER
//        type_id INTEGER ,
//        Comment VARCHAR(200)
//        type_name VARCHAR(200)
//        game_limit int
//        game_mask VARCHAR(200)

        String insert_sql = "insert into device (device_code, status, server_device_id, type_id, Comment, type_name, game_limit, game_mask) " +
                "VALUES ('"+dDevice.getDeviceCode()+"',"+dDevice.getStatus()+", "
                +dDevice.getServerDeviceId()+", "+dDevice.getTypeId()+", '"+dDevice.getComment()+"', '"+dDevice.getType_name()+"'," +
                dDevice.getGame_limit()+", '"+dDevice.getGame_mask()+"')";
        try {
            //Еcли там уже есть устройство, нам добавлять не нужно
            if (this.getSQLQueryCount("SELECT * FROM device")==0) {
                db.execSQL(insert_sql);
                String sql = "SELECT last_insert_rowid() as lastid";
                Cursor c = this.runResultedSQL(sql);
                if (c!=null && c.moveToFirst()){
                    dDevice.setDevice_id(c.getInt(c.getColumnIndex("lastid")));
                    c.close();
                }
            }
            bReturn = true;}
        catch (Exception ex){
            bReturn = false;
        }
        return bReturn;
    }

    /**
     * get single device
     */
    public d_device getDevice() {
        String selectQuery = "SELECT * FROM device limit 1";
        Cursor c = dbr.rawQuery(selectQuery, null);
        d_device td = null;
//        device_code VARCHAR(200)
//        status INTEGER
//        server_device_id INTEGER
//        type_id INTEGER ,
//        Comment VARCHAR(200)
//        type_name VARCHAR(200)
//        game_limit int
//        game_mask VARCHAR(200)
        if( c != null && c.moveToFirst() ){
            td = new d_device();
            td.setDeviceCode(c.getString(c.getColumnIndex("device_code")));
            td.setStatus(c.getInt(c.getColumnIndex("status")));
            td.setDevice_id(c.getInt(c.getColumnIndex("device_id")));
            td.setServerDeviceId(c.getInt(c.getColumnIndex("server_device_id")));
            td.setTypeId(c.getInt(c.getColumnIndex("type_id")));
            td.setComment(c.getString(c.getColumnIndex("Comment")));
            td.setType_name(c.getString(c.getColumnIndex("type_name")));
            td.setGame_limit(c.getInt(c.getColumnIndex("game_limit")));
            td.setGame_mask(c.getString(c.getColumnIndex("game_mask")));
            c.close();
        }
        return td;
    }

    /**
     * Updating a device
     */
    public boolean updateDevice(d_device dDevice) {
        boolean bReturn;
//        device_code VARCHAR(200)
//        status INTEGER
//        server_device_id INTEGER
//        type_id INTEGER ,
//        Comment VARCHAR(200)
//        type_name VARCHAR(200)
//        game_limit int
//        game_mask VARCHAR(200)
        String update_sql = "Update device " +
                "SET device_code ='"+dDevice.getDeviceCode()+"', " +
                "status = "+dDevice.getStatus() +", "+
                "server_device_id = "+ dDevice.getServerDeviceId() +", "+
                "type_id = "+ dDevice.getTypeId() +", "+
                "Comment = '"+ dDevice.getComment() +"', "+
                "type_name = '"+ dDevice.getType_name() +"', "+
                "game_limit = "+ dDevice.getGame_limit() +", "+
                "game_mask = '"+ dDevice.getGame_mask() +"' "+
                "WHERE device_id = " +dDevice.getDevice_id();
        try {
            db.execSQL(update_sql);
            bReturn = true;
        }
        catch (Exception ex){
            bReturn = false;
        }
        return bReturn;
    }

    /**
     * Deleting a device
     */
    public boolean deleteDevice(int device_id) {
        boolean bReturn;
        String delete_sql = "DELETE FROM device \n"+
                "WHERE device_id=" + device_id;
        try {
            db.execSQL(delete_sql);
            bReturn = true;
        }
        catch (Exception ex) {
            bReturn = false;
        }
        return bReturn;
    }


    // ------------------------ close "device" table methods _---------//


    // ------------------------ "Games" table methods ----------------//

    /**
     * Creating a game
     */
    public boolean createNewGame(d_game dGame) {
        boolean bReturn;
        String insert_sql = "insert into game (win_ball,state,device_id,dtime,game_code, server_game_id) " +
                "VALUES ("+Integer.toString(dGame.getWin_ball())+","+dGame.getState()+","+dGame.getDevice_id()+",'"+getDateTime()+"','"+dGame.getGameCode()+"',"+dGame.getServer_game_id()+")";

        //Если устройство активно мы начинаем новую игру
        if (this.getDevice().getStatus()==0) {
            try {
               if (this.getSQLQueryCount("SELECT game_code FROM game WHERE game_code='"+dGame.getGameCode()+"'")==0)  db.execSQL(insert_sql);
                String sql = "SELECT last_insert_rowid() as lastid";
                Cursor c = this.runResultedSQL(sql);
                if (c!=null && c.moveToFirst()){
                    dGame.setId(c.getInt(c.getColumnIndex("lastid")));
                    c.close();
                }
                bReturn = true;}
            catch (Exception ex){
                bReturn = false;
            }
        }
        else {
            bReturn = false;
        }
        return bReturn;
    }

    /**
     * get single game
     */
    public d_game getLastGame() {
        String selectQuery = "SELECT * FROM game ORDER BY id DESC LIMIT 1";
        Cursor c = dbr.rawQuery(selectQuery, null);
        d_game td = null;

        if (c != null&&c.moveToFirst()){
            td = new d_game();
            td.setId(c.getInt(c.getColumnIndex("id")));
            td.setWin_ball((c.getInt(c.getColumnIndex("win_ball"))));
            td.setState(c.getInt(c.getColumnIndex("state")));
            td.setDevice_id(c.getInt(c.getColumnIndex("device_id")));
            td.setServer_game_id(c.getInt(c.getColumnIndex("server_game_id")));
            td.setGameCode(c.getString(c.getColumnIndex("game_code")));
            td.setDtime(c.getString(c.getColumnIndex("dtime")));
            c.close();
        }
        return td;
    }

    /**
     * getting active game
     * */
    public d_game getActiveGame() {
        d_game dGames = null;
        String selectQuery = "SELECT * FROM game WHERE state = 0";

        Cursor c = dbr.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c!=null && c.moveToFirst()) {
            dGames = new d_game();
            dGames.setId(c.getInt(c.getColumnIndex("id")));
            dGames.setWin_ball((c.getInt(c.getColumnIndex("win_ball"))));
            dGames.setState(c.getInt(c.getColumnIndex("state")));
            dGames.setDevice_id(c.getInt(c.getColumnIndex("device_id")));
            dGames.setServer_game_id(c.getInt(c.getColumnIndex("server_game_id")));
            dGames.setGameCode(c.getString(c.getColumnIndex("game_code")));
            dGames.setDtime(c.getString(c.getColumnIndex("dtime")));
            c.close();
        }
        return dGames;
    }

    /**
     * getting active game
     * */
    public d_game getGame(int serverId) {
        d_game dGames = null;
        if (serverId>0) {
            String selectQuery = "SELECT * FROM game WHERE server_game_id = " + serverId;
            Cursor c = dbr.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                dGames = new d_game();
                dGames.setId(c.getInt(c.getColumnIndex("id")));
                dGames.setWin_ball((c.getInt(c.getColumnIndex("win_ball"))));
                dGames.setState(c.getInt(c.getColumnIndex("state")));
                dGames.setDevice_id(c.getInt(c.getColumnIndex("device_id")));
                dGames.setServer_game_id(c.getInt(c.getColumnIndex("server_game_id")));
                dGames.setGameCode(c.getString(c.getColumnIndex("game_code")));
                dGames.setDtime(c.getString(c.getColumnIndex("dtime")));
                c.close();
            }
        }
        return dGames;
    }



    /**
     * getting all games
     * */
    public List<d_game> getAllGames() {
        List<d_game> d_games = new ArrayList<>();
        String selectQuery = "SELECT  * FROM game";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c!=null && c.moveToFirst()) {
            do {
                d_game td = new d_game();
                td.setId(c.getInt(c.getColumnIndex("id")));
                td.setWin_ball((c.getInt(c.getColumnIndex("win_ball"))));
                td.setState(c.getInt(c.getColumnIndex("state")));
                td.setDevice_id(c.getInt(c.getColumnIndex("device_id")));
                td.setServer_game_id(c.getInt(c.getColumnIndex("server_game_id")));
                td.setGameCode(c.getString(c.getColumnIndex("game_code")));
                td.setDtime(c.getString(c.getColumnIndex("dtime")));
                d_games.add(td);
            } while (c.moveToNext());
            c.close();
        }
        return d_games;
    }



    /**
     * Updating a game
     */
    public boolean updateGame(d_game dGame) {
        boolean bReturn;
        String update_sql = "Update Game\n" +
                "SET win_ball ="+dGame.getWin_ball()+",\n" +
                "state ="+dGame.getState()+",\n" +
                "device_id = "+dGame.getDevice_id()+",\n" +
                "dtime = '" + getDateTime() +"', "+
                "game_code = '"+dGame.getGameCode()+"', "+
                "server_game_id = '"+dGame.getServer_game_id()+"' "+
                "WHERE id=" +dGame.getId();
        try {
            db.execSQL(update_sql);
            bReturn = true;
        }
        catch (Exception ex){
            bReturn = false;
        }
        return bReturn;
    }

    /**
     * Deleting a game
     */
    public boolean deleteGame(int game_id) {
        boolean bReturn;
        String delete_sql = "DELETE FROM Game \n"+
                "WHERE id=" + game_id;
        try {
            db.execSQL(delete_sql);
            bReturn = true;
        }
        catch (Exception ex) {
            bReturn = false;
        }

        return bReturn;
    }
    // ------------------------ end "Games" table methods ----------------//
    // ------------------------ d_entry_set table methods ----------------//

    /**
     * Creating a d_entry_set
     */
    public boolean createNewEntrySet(d_entry_set dEntrySet) {
        boolean bReturn;
        String insert_sql = "INSERT INTO entry_set (log_id, chip_number,entry_value,entry_id,divided_by,x,y,game_id, sum, entry_pack_id)\n" +
                "VALUES ("+
                + dEntrySet.getLog_id()+","
                + dEntrySet.getChip_number() + ","
                + dEntrySet.getEntry_value() + ","
                + dEntrySet.getEntry_id() + ","
                + dEntrySet.getDivided_by() + ","+
                + dEntrySet.getX() + ","+
                + dEntrySet.getY() + ","+
                + dEntrySet.getGame_id()+","+
                + dEntrySet.getSum()+ ","
                + dEntrySet.getEntryPackId()+")";
        try {

            db.execSQL(insert_sql);
            String sql = "SELECT last_insert_rowid() as lastid";
            Cursor c = this.runResultedSQL(sql);
            if (c!=null && c.moveToFirst()){
                dEntrySet.setSys_id(c.getInt(c.getColumnIndex("lastid")));
                c.close();
            }
            bReturn = true;
        }
        catch (Exception ex){
            bReturn = false;
        }
        return bReturn;
    }

    public int getLastSetPack() {
        String selectQuery = "SELECT entry_pack_id FROM entry_set ORDER BY entry_pack_id DESC LIMIT 1";
        Cursor c = dbr.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst()) {
            int dfff =  c.getInt(c.getColumnIndex("entry_pack_id"));
            c.close();
            return dfff;
        }
        else return -1;
    }


    public List<d_entry_set> getLastEntrySetPack() {
        String selectQuery = "SELECT entry_pack_id FROM entry_set ORDER BY entry_pack_id DESC LIMIT 1";
        Cursor c = dbr.rawQuery(selectQuery, null);
        List<d_entry_set> list_td = new ArrayList<>();
        int last_entry_pack_id;

        if (c != null && c.moveToFirst()){
            last_entry_pack_id = c.getInt(c.getColumnIndex("entry_pack_id"));
            selectQuery = "SELECT * FROM entry_set WHERE entry_pack_id = "+last_entry_pack_id;
            c.close();
            c = dbr.rawQuery(selectQuery, null);
            do {
                d_entry_set td = new d_entry_set();
                td.setSys_id(c.getInt(c.getColumnIndex("sys_id")));
                td.setLog_id(c.getInt(c.getColumnIndex("log_id")));
                td.setChip_number((c.getInt(c.getColumnIndex("chip_number"))));
                td.setEntry_value(c.getInt(c.getColumnIndex("entry_value")));
                td.setEntry_id(c.getInt(c.getColumnIndex("entry_id")));
                td.setDivided_by(c.getInt(c.getColumnIndex("divided_by")));
                td.setX(c.getInt(c.getColumnIndex("x")));
                td.setY(c.getInt(c.getColumnIndex("y")));
                td.setGame_id(c.getInt(c.getColumnIndex("game_id")));
                td.setSum(c.getInt(c.getColumnIndex("sum")));
                td.setEntryPackId(c.getInt(c.getColumnIndex("entry_pack_id")));
                list_td.add(td);
            } while (c.moveToNext());
            c.close();
        }
        return list_td;
    }

    /**
     * get single d_entry_set
     */
    public d_entry_set getLastEntrySet() {
        String selectQuery = "SELECT * FROM entry_set ORDER BY sys_id DESC LIMIT 1";
        Cursor c = dbr.rawQuery(selectQuery, null);
        d_entry_set td =null;

        if (c != null && c.moveToFirst()){
            td = new d_entry_set();
            td.setSys_id(c.getInt(c.getColumnIndex("sys_id")));
            td.setLog_id(c.getInt(c.getColumnIndex("log_id")));
            td.setChip_number((c.getInt(c.getColumnIndex("chip_number"))));
            td.setEntry_value(c.getInt(c.getColumnIndex("entry_value")));
            td.setEntry_id(c.getInt(c.getColumnIndex("entry_id")));
            td.setDivided_by(c.getInt(c.getColumnIndex("divided_by")));
            td.setX(c.getInt(c.getColumnIndex("x")));
            td.setY(c.getInt(c.getColumnIndex("y")));
            td.setGame_id(c.getInt(c.getColumnIndex("game_id")));
            td.setSum(c.getInt(c.getColumnIndex("sum")));
            td.setEntryPackId(c.getInt(c.getColumnIndex("entry_pack_id")));
            c.close();
        }
        return td;
    }

    public d_entry_set getEntrySet(int sys_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM entry_set WHERE sys_id="+sys_id;
        Cursor c = db.rawQuery(selectQuery, null);
        d_entry_set td =null;

        if (c != null&&c.moveToFirst()){

            td = new d_entry_set();
            td.setSys_id(sys_id);
            td.setLog_id(c.getInt(c.getColumnIndex("log_id")));
            td.setChip_number((c.getInt(c.getColumnIndex("chip_number"))));
            td.setEntry_value(c.getInt(c.getColumnIndex("entry_value")));
            td.setEntry_id(c.getInt(c.getColumnIndex("entry_id")));
            td.setDivided_by(c.getInt(c.getColumnIndex("divided_by")));
            td.setX(c.getInt(c.getColumnIndex("x")));
            td.setY(c.getInt(c.getColumnIndex("y")));
            td.setGame_id(c.getInt(c.getColumnIndex("game_id")));
            td.setSum(c.getInt(c.getColumnIndex("sum")));
            td.setEntryPackId(c.getInt(c.getColumnIndex("entry_pack_id")));
            c.close();
        }
        return td;
    }

    /**
     * getting all d_entry_set
     * */
    public List<d_entry_set> getAllGameEntrySet(int GameId) {
        List<d_entry_set> d_entry_set = new ArrayList<>();
        String selectQuery = "SELECT * FROM entry_set WHERE game_id="+GameId;
        Cursor c = dbr.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c!=null && c.moveToFirst()) {
            do {
                d_entry_set td = new d_entry_set();
                td.setSys_id(c.getInt(c.getColumnIndex("sys_id")));
                td.setLog_id(c.getInt(c.getColumnIndex("log_id")));
                td.setChip_number((c.getInt(c.getColumnIndex("chip_number"))));
                td.setEntry_value(c.getInt(c.getColumnIndex("entry_value")));
                td.setEntry_id(c.getInt(c.getColumnIndex("entry_id")));
                td.setDivided_by(c.getInt(c.getColumnIndex("divided_by")));
                td.setX(c.getInt(c.getColumnIndex("x")));
                td.setY(c.getInt(c.getColumnIndex("y")));
                td.setGame_id(c.getInt(c.getColumnIndex("game_id")));
                td.setSum(c.getInt(c.getColumnIndex("sum")));
                td.setEntryPackId(c.getInt(c.getColumnIndex("entry_pack_id")));
                d_entry_set.add(td);
            } while (c.moveToNext());
            c.close();
        }
        return d_entry_set;
    }


    /**
     * Updating a EntrySet
     */
    public boolean updateEntrySet(d_entry_set dEntrySet) {
        boolean bReturn;
        String update_sql = "Update entry_set\n" +
                "SET log_id = " + dEntrySet.getLog_id() + ","+
                "chip_number ="+dEntrySet.getChip_number()+",\n" +
                "entry_value ="+dEntrySet.getEntry_value()+",\n" +
                "entry_id ="+dEntrySet.getEntry_id()+"\n"+
                "divided_by ="+dEntrySet.getDivided_by()+"\n"+
                "x ="+dEntrySet.getX()+"\n"+
                "y="+dEntrySet.getY()+"\n"+
                "game_id="+dEntrySet.getGame_id()+ "\n"+
                "sum="+dEntrySet.getSum()+", "+
                "entry_pack_id="+dEntrySet.getEntryPackId()+" "+
                "WHERE sys_id=" +dEntrySet.getSys_id();
        try{
            db.execSQL(update_sql);
            bReturn = true;
        }
        catch (Exception ex){
            bReturn = false;
        }
        return bReturn;
    }

    public int getGameSum(int game_id, int win_nbr ) {
        int iReturn=0;
        String group_sum_sql = "SELECT TOTAL(sum) as cur_sum  FROM entry_set WHERE game_id=" + game_id+" AND chip_number="+win_nbr+" GROUP BY chip_number";
        try{
            Cursor c = dbr.rawQuery(group_sum_sql, null);
            if (c!=null && c.moveToFirst()){
                iReturn = c.getInt(c.getColumnIndex("cur_sum"));
                c.close();
            }

        }
        catch (Exception ex){
            iReturn = -1;
        }
        return iReturn;
    }

    public int getGameCurrentSum(int game_id) {
        int iReturn=-1;
        String group_sum_sql = "SELECT SUM(cur_sum) as cur_sum1 FROM (SELECT MIN(entry_value) as cur_sum, MIN(entry_id) as EI  FROM entry_set WHERE game_id=" + game_id+ " GROUP BY log_id)";
        try{
            Cursor c = dbr.rawQuery(group_sum_sql, null);
            if (c!=null && c.moveToFirst()){
                iReturn = c.getInt(c.getColumnIndex("cur_sum1"));
                c.close();
            }

        }
        catch (Exception ex){
            iReturn = -1;
        }
        return iReturn;
    }

    public int getGameIdSum(int game_id, int idV) {
        int iReturn=0;
        String group_sum_sql = "SELECT TOTAL(F1.cur_sum) as cur_sum1 FROM (SELECT MIN(entry_value) as cur_sum, MIN(entry_id) as EI FROM entry_set WHERE game_id=" + game_id+" AND entry_id="+idV+" GROUP BY log_id) as F1 GROUP BY EI";
        try{
            Cursor c = dbr.rawQuery(group_sum_sql, null);
            if (c!=null && c.moveToFirst()){
                iReturn = c.getInt(c.getColumnIndex("cur_sum1"));
                c.close();
            }

        }
        catch (Exception ex){
            iReturn = -1;
        }
        return iReturn;
    }

    public int getGameIdSum(int game_id) {
        int iReturn=0;
        String group_sum_sql = "select sum (vle) as cur_sum from " +
                "(SELECT min(entry_value) as vle FROM entry_set WHERE game_id = "+game_id+" group by log_id) as slct";
        try{
            Cursor c = dbr.rawQuery(group_sum_sql, null);
            if (c!=null && c.moveToFirst()){
                iReturn = c.getInt(c.getColumnIndex("cur_sum"));
                c.close();
            }

        }
        catch (Exception ex){
            iReturn = -1;
        }
        return iReturn;
    }

    /**
     * Deleting a EntrySet
     */
    public boolean deleteEntrySet(long log_id) {
        boolean bReturn;
        String delete_sql = "DELETE FROM entry_set \n"+
                "WHERE log_id=" + log_id;
        try {
            db.execSQL(delete_sql);
            bReturn = true;
        }
        catch (Exception ex){
            bReturn=false;
        }
        return bReturn;
    }

    /**
     * Deleting a EntrySet by game_id
     */
    public boolean deleteEntrySetByGameID(int game_id) {
        boolean bReturn;
        String delete_sql = "DELETE FROM entry_set \n"+
                "WHERE game_id=" + game_id;
        try {
            db.execSQL(delete_sql);
            bReturn = true;
        }
        catch (Exception ex){
            bReturn=false;
        }
        return bReturn;
    }

    public boolean setReturnGame(int id){
        int idgame = 0;
        boolean breturn = true;
        String check_sql = "SELECT id_game FROM returnGame WHERE id_game ="+id;
        try{
            Cursor c = dbr.rawQuery(check_sql, null);
            if (c!=null && c.moveToFirst()){
                idgame = c.getInt(c.getColumnIndex("id_game"));
                breturn = true;
                c.close();
            }
            if (idgame==0) {
                String SQLstring = "INSERT INTO returnGame(id_game) VALUES(" + id + ")";
                db.execSQL(SQLstring);
                breturn=false;
            }
            return breturn;
        }
        catch (Exception ex){
            return breturn;
        }
    }


    /**
     * Deleting a EntrySet by game_id
     */
    public boolean deleteGameCache() {
        boolean bReturn;
        String delete_sql = "DELETE FROM game WHERE id NOT IN (SELECT id FROM game ORDER BY dtime DESC LIMIT 11)";
        String delete_sql2 = "DELETE FROM entry_set WHERE game_id NOT IN (SELECT id FROM game)";
        try {
            db.execSQL(delete_sql);
            db.execSQL(delete_sql2);
            bReturn = true;
        }
        catch (Exception ex){
            bReturn=false;
        }
        return bReturn;
    }


    // ------------------------ end d_entry_set table methods ----------------//

    public int getSQLQueryCount(String countQuery) {
        int count=0;
        if (countQuery.length()>0){
            try {
                Cursor cursor = db.rawQuery(countQuery, null);
                count = cursor.getCount();
                cursor.close();
            }
            catch (Exception ex){
                count = -1;
            }
        }
        // return count
        return count;
    }


    //Run SQL
    public boolean runEmpryResultSQL(String sql_query) {
        boolean bReturn=false;
        if (sql_query.length()>0 ){
           try{
                db.execSQL(sql_query);
                bReturn = true;}
           catch (Exception ex){
               bReturn = false;
           }
        }
        return bReturn;
    }


    //Run ResultedSQL, cursor should be closed after use
    public Cursor runResultedSQL(String sql_query) {
        Cursor c = null;
        if (sql_query.length()>0 ){
            try{
                //SQLiteDatabase db = this.getWritableDatabase();
                c = db.rawQuery(sql_query, null);
            }
            catch (Exception ex){
                c = null;
            }
        }
        return c;
    }

    // closing database
    public void closeDB() {
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }



}