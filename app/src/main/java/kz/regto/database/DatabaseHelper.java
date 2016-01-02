package kz.regto.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = DatabaseHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "bingo_db";
    
    private SQLiteDatabase db;
    private SQLiteDatabase dbr;

    // Table Create Statements
    private static final String CREATE_TABLE_Game = "CREATE TABLE Game (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "win_ball INTEGER NOT NULL DEFAULT '-1' , " +
            "state INTEGER NOT NULL DEFAULT '0' , " +
            "device_id INTEGER NOT NULL DEFAULT '0', " +
            "dtime DATETIME NOT NULL,"+
            "game_code VARCHAR(10),"+
            "server_game_id INTEGER NOT NULL)";
    private static final String CREATE_TABLE_balance = "CREATE TABLE balance " +
            "(game_id INTEGER NOT NULL, operation_type INTEGER NOT NULL DEFAULT '0'," +
            " sum INTEGER NOT NULL)";
    private static final String CREATE_TABLE_device = "CREATE TABLE device (device_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            " device_code VARCHAR(200) NOT NULL, status INTEGER NOT NULL DEFAULT '0', balance INTEGER NOT NULL DEFAULT '0', network_path TEXT DEFAULT '')";
    private static final String CREATE_TABLE_entry_set = "    CREATE TABLE entry_set (\n" +
            "                    sys_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
            "                    log_id INTEGER NOT NULL, "+
            "                    chip_number INTEGER NOT NULL,\n" +
            "                    entry_value INTEGER NOT NULL,\n" +
            "                    entry_id INTEGER NOT NULL,\n" +
            "                    divided_by INTEGER NOT NULL, x INTEGER NOT NULL, y INTEGER NOT NULL," +
            "                    game_id INTEGER NOT NULL,"+
            "                    sum INTEGER NOT NULL)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_Game);
        db.execSQL(CREATE_TABLE_balance);
        db.execSQL(CREATE_TABLE_device);
        db.execSQL(CREATE_TABLE_entry_set);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_Game);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_balance);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_device);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_entry_set);

        // create new tables
        onCreate(db);
    }
    // ------------------------ "balance" table methods ----------------//


    public boolean openDB() {
        boolean bRet;
        try{
            db = this.getWritableDatabase();
            dbr = this.getReadableDatabase();
            bRet=true;
        }
        catch (SQLException  ex){
            bRet=false;
        }
        return bRet;
    }

    public void close() {
        db.close();
    }



    public boolean createNewBalance(d_balance dBalance) {
        boolean bReturn;
        String insert_sql = "insert into balance (game_id,operation_type,sum)\n" +
                "VALUES ("+dBalance.getGame_id()+","+dBalance.getOperation()+","+dBalance.getSum()+")";
        try {
            db.execSQL(insert_sql);
            bReturn = true;}
        catch (Exception ex){
            bReturn = false;
        }
        return bReturn;
    }

    public List<d_balance> getBalance(int game_id) {

        String selectQuery = "SELECT * FROM balance " +
                " WHERE game_id="+Integer.toString(game_id);
        List<d_balance> dBalance = new ArrayList<>();

        Cursor c = dbr.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                d_balance td = new d_balance();
                td.setGame_id(c.getInt(c.getColumnIndex("game_id")));
                td.setOperation((c.getInt(c.getColumnIndex("operation_type"))));
                td.setSum(c.getInt(c.getColumnIndex("sum")));

                dBalance.add(td);
            } while (c.moveToNext());
            c.close();
        }
        return dBalance;
    }

    /**
     * Deleting a balance
     */
    public boolean deleteBalance(int gameId) {
        boolean bReturn;
        String delete_sql = "DELETE FROM balance \n"+
                "WHERE game_id=" + gameId;
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
        String update_sql = "Update balance\n" +
                "SET sum ="+dBalance.getSum()+",\n" +
                "operation_type ="+dBalance.getOperation() +",\n" +
                "WHERE id=" +dBalance.getGame_id();
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
        String insert_sql = "insert into device (device_code, status, balance, network_path) " +
                "VALUES ('"+dDevice.getDeviceCode()+"',"+dDevice.getStatus()+","+dDevice.getBalance()+",'"+dDevice.getNetwork_path()+"')";
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
        String selectQuery = "SELECT * FROM device";
        Cursor c = dbr.rawQuery(selectQuery, null);
        d_device td = null;

        if( c != null && c.moveToFirst() ){
            td = new d_device();
            td.setDeviceCode(c.getString(c.getColumnIndex("device_code")));
            td.setStatus(c.getInt(c.getColumnIndex("status")));
            td.setDevice_id(c.getInt(c.getColumnIndex("device_id")));
            td.setNetwork_path(c.getString(c.getColumnIndex("network_path")));
            td.setBalance(c.getInt(c.getColumnIndex("balance")));
            c.close();
        }
        return td;
    }

    /**
     * Updating a device
     */
    public boolean updateDevice(d_device dDevice) {
        boolean bReturn;
        String update_sql = "Update device " +
                "SET device_code ='"+dDevice.getDeviceCode()+"', " +
                "status = "+dDevice.getStatus() +", "+
                "balance = "+ dDevice.getBalance() +", "+
                "network_path = '"+dDevice.getNetwork_path() +"' "+
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
            //SQLiteDatabase db = this.getWritableDatabase();
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
        if (this.getDevice().getStatus()==1) {
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
        String insert_sql = "INSERT INTO entry_set (log_id, chip_number,entry_value,entry_id,divided_by,x,y,game_id, sum)\n" +
                "VALUES ("+
                + dEntrySet.getLog_id()+","
                + dEntrySet.getChip_number() + ","
                + dEntrySet.getEntry_value() + ","
                + dEntrySet.getEntry_id() + ","
                + dEntrySet.getDivided_by() + ","+
                + dEntrySet.getX() + ","+
                + dEntrySet.getY() + ","+
                + dEntrySet.getGame_id()+","+
                + dEntrySet.getSum()+ ")";
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
            c.close();
        }
        return td;
    }

    /**
     * getting all d_entry_set
     * */
    public List<d_entry_set> getAllGameEntrySet(int GameId) {
        List<d_entry_set> d_entry_set = new ArrayList<>();
        String selectQuery = "SELECT * FROM entry_set WHERE sys_id IN (SELECT MIN(sys_id) FROM entry_set WHERE game_id="+GameId + " group by log_id)";
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
                "game_id="+dEntrySet.getGame_id()+"\n"+
                "sum="+dEntrySet.getSum()+"\n"+
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
        String group_sum_sql = "SELECT TOTAL(cur_sum) as cur_sum1 FROM (SELECT MIN(entry_value) as cur_sum, MIN(entry_id) as EI  FROM entry_set WHERE game_id=" + game_id+ " GROUP BY log_id)";
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
        String group_sum_sql = "SELECT TOTAL(F1.cur_sum) as cur_sum1 FROM (SELECT MIN(entry_value) as cur_sum, MIN(entry_id) as EI  FROM entry_set WHERE game_id=" + game_id+" AND entry_id="+idV+" GROUP BY log_id) as F1 GROUP BY EI";
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
    /**
     * Deleting a EntrySet by game_id
     */
    public boolean deleteGameCache() {
        boolean bReturn;
        String delete_sql = "DELETE FROM game WHERE id NOT IN (SELECT id FROM game ORDER BY dtime DESC LIMIT 11)";
        String delete_sql1 = "DELETE FROM balance WHERE game_id NOT IN (SELECT id FROM game)";
        String delete_sql2 = "DELETE FROM entry_set WHERE game_id NOT IN (SELECT id FROM game)";
        try {
            db.execSQL(delete_sql);
            db.execSQL(delete_sql1);
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