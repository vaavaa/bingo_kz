package kz.regto.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

    // Table Create Statements
    private static final String CREATE_TABLE_Game = "CREATE TABLE Game (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "win_ball INTEGER NOT NULL DEFAULT '-1' , " +
            "state INTEGER NOT NULL DEFAULT '0' , " +
            "device_id INTEGER NOT NULL DEFAULT '0', " +
            "dtime DATETIME NOT NULL)";
    private static final String CREATE_TABLE_balance = "CREATE TABLE balance " +
            "(game_id INTEGER NOT NULL, operation_type INTEGER NOT NULL DEFAULT '0'," +
            " sum INTEGER NOT NULL)";
    private static final String CREATE_TABLE_device = "CREATE TABLE device (device_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            " device_code VARCHAR(200) NOT NULL, status INTEGER NOT NULL DEFAULT '0')";
    private static final String CREATE_TABLE_entry_set = "    CREATE TABLE entry_set (\n" +
            "                    log_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
            "                    chip_number INTEGER NOT NULL,\n" +
            "                    entry_value INTEGER NOT NULL,\n" +
            "                    entry_id INTEGER NOT NULL,\n" +
            "                    divided_by INTEGER NOT NULL, x INTEGER NOT NULL, y INTEGER NOT NULL)";
    private static final String CREATE_TABLE_log = "   CREATE TABLE log\n" +
            "            (\n" +
            "                    game_id INTEGER NOT NULL,\n" +
            "                    log_id INTEGER NOT NULL\n" +
            "            )";


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
        db.execSQL(CREATE_TABLE_log);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_Game);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_balance);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_device);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_entry_set);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_log);


        // create new tables
        onCreate(db);
    }
    // ------------------------ "balance" table methods ----------------//


    public boolean createNewBalance(d_balance dBalance) {
        boolean bReturn;
        String insert_sql = "insert into game (game_id,operation_type,sum)\n" +
                "VALUES ("+Integer.toString(dBalance.getGame_id())+","+dBalance.getOperation()+","+dBalance.getSum()+")";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
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

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

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
        SQLiteDatabase db = this.getWritableDatabase();
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
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(update_sql);
            bReturn = true;
        }
        catch (Exception ex){
            bReturn = false;
        }
        return bReturn;
    }


    // ------------------------ end "balance" table methods ----------------//


    // ------------------------ "log" table methods ----------------//
    public boolean createNewLog(d_log dLog) {
        boolean bReturn;
        String insert_sql = "insert into game (game_id, log_id)\n" +
                "VALUES ("+dLog.getGame_id()+","+dLog.getLog_id()+")";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(insert_sql);
            bReturn = true;}
        catch (Exception ex){
            bReturn = false;
        }
        return bReturn;
    }

    public List<d_entry_set> getLog(int game_id) {

        String selectQuery = "SELECT * FROM entry_set left join log on entry_set.log_id = log.log_id" +
                " WHERE log.game_id="+Integer.toString(game_id);
        List<d_entry_set> dLog = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                d_entry_set td = new d_entry_set();
                td.setLog_id(c.getInt(c.getColumnIndex("log_id")));
                td.setChip_number((c.getInt(c.getColumnIndex("chip_number"))));
                td.setEntry_value(c.getInt(c.getColumnIndex("entry_value")));
                td.setEntry_id(c.getInt(c.getColumnIndex("entry_id")));
                td.setDivided_by(c.getInt(c.getColumnIndex("divided_by")));
                td.setX(c.getInt(c.getColumnIndex("x")));
                td.setY(c.getInt(c.getColumnIndex("y")));

                dLog.add(td);
            } while (c.moveToNext());
            c.close();
        }
        return dLog;
    }

    /**
     * Deleting a log
     */
    public boolean deleteLog(int gameId) {
        boolean bReturn;
        String delete_sql = "DELETE FROM log \n"+
                "WHERE game_id=" + gameId;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(delete_sql);
            bReturn = true;
        }
        catch (Exception ex){
            bReturn=false;
        }
        return bReturn;
    }


    // ------------------------ end "log" table methods ----------------//
    // ------------------------ "device" table methods ----------------//
    /**
     * Creating a device
     */
    public boolean createNewDevice(d_device dDevice) {
        boolean bReturn;
        String insert_sql = "insert into game (device_code, status)\n" +
                "VALUES ("+dDevice.getDeviceCode()+","+dDevice.getStatus()+")";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(insert_sql);
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
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT top 1 * FROM device";
        Cursor c = db.rawQuery(selectQuery, null);
        d_device td = null;

        if (c != null){
            c.moveToFirst();

            td = new d_device();
            td.setDeviceCode(c.getString(c.getColumnIndex("device_code")));
            td.setStatus(c.getInt(c.getColumnIndex("status")));
            td.setDevice_id(c.getInt(c.getColumnIndex("device_id")));
            c.close();
        }
        return td;
    }

    /**
     * Updating a device
     */
    public boolean updateGame(d_device dDevice) {
        boolean bReturn;
        String update_sql = "Update device\n" +
                "SET device_code ="+dDevice.getDeviceCode()+",\n" +
                "state ="+dDevice.getStatus() +",\n" +
                "WHERE id=" +dDevice.getDevice_id();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
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
                "WHERE id=" + device_id;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
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
        String insert_sql = "insert into game (win_ball,state,device_id,dtime)\n" +
                "VALUES ("+Integer.toString(dGame.getWin_ball())+","+dGame.getState()+","+dGame.getDevice_id()+","+getDateTime()+")";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(insert_sql);
            bReturn = true;}
        catch (Exception ex){
            bReturn = false;
        }
        return bReturn;
    }

    /**
     * get single game
     */
    public d_game getGame() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT top 1 * FROM game ORDER BY dtime DESC";
        Cursor c = db.rawQuery(selectQuery, null);
        d_game td = null;

        if (c != null){
            c.moveToFirst();

            td = new d_game();
            td.setId(c.getInt(c.getColumnIndex("id")));
            td.setWin_ball((c.getInt(c.getColumnIndex("win_ball"))));
            td.setState(c.getInt(c.getColumnIndex("state")));
            td.setDevice_id(c.getInt(c.getColumnIndex("device_id")));
            c.close();
        }

        return td;
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
        if (c.moveToFirst()) {
            do {
                d_game td = new d_game();
                td.setId(c.getInt(c.getColumnIndex("id")));
                td.setWin_ball((c.getInt(c.getColumnIndex("win_ball"))));
                td.setState(c.getInt(c.getColumnIndex("state")));
                td.setDevice_id(c.getInt(c.getColumnIndex("device_id")));
                td.setDtime(c.getString(c.getColumnIndex("dtime")));


                d_games.add(td);
            } while (c.moveToNext());
        }
        c.close();
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
                "dtime = \""+dGame.getDtime()+"\"\n"+
                "WHERE id=" +dGame.getId();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
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
            SQLiteDatabase db = this.getWritableDatabase();
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
        String insert_sql = "INSERT INTO entry_set (chip_number,entry_value,entry_id,divided_by,x,y)\n" +
                "VALUES ("+ Integer.toString(dEntrySet.getChip_number()) + ","
                + dEntrySet.getEntry_value() + ","
                + dEntrySet.getEntry_id() + ","
                + dEntrySet.getDivided_by() + ","+
                + dEntrySet.getX() + ","+
                + dEntrySet.getY() + ")";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(insert_sql);
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
    public d_entry_set getEntrySet(int log_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM entry_set WHERE log_id="+log_id;
        Cursor c = db.rawQuery(selectQuery, null);
        d_entry_set td =null;

        if (c != null){
                c.moveToFirst();

            td = new d_entry_set();
            td.setLog_id(c.getInt(c.getColumnIndex("log_id")));
            td.setChip_number((c.getInt(c.getColumnIndex("chip_number"))));
            td.setEntry_value(c.getInt(c.getColumnIndex("entry_value")));
            td.setEntry_id(c.getInt(c.getColumnIndex("entry_id")));
            td.setDivided_by(c.getInt(c.getColumnIndex("divided_by")));
            td.setX(c.getInt(c.getColumnIndex("x")));
            td.setY(c.getInt(c.getColumnIndex("y")));
            c.close();
        }
        return td;
    }

    /**
     * getting all d_entry_set
     * */
    public List<d_entry_set> getAllEntrySet() {
        List<d_entry_set> d_entry_set = new ArrayList<>();
        String selectQuery = "SELECT  * FROM entry_set";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                d_entry_set td = new d_entry_set();
                td.setLog_id(c.getInt(c.getColumnIndex("log_id")));
                td.setChip_number((c.getInt(c.getColumnIndex("chip_number"))));
                td.setEntry_value(c.getInt(c.getColumnIndex("entry_value")));
                td.setEntry_id(c.getInt(c.getColumnIndex("entry_id")));
                td.setDivided_by(c.getInt(c.getColumnIndex("divided_by")));

                d_entry_set.add(td);
            } while (c.moveToNext());
            c.close();
        }

        return d_entry_set;
    }

    public List<d_entry_set> getSelectedEntrySet() {
        List<d_entry_set> d_entry_set = new ArrayList<>();
        String selectQuery = "SELECT  * FROM entry_set";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                d_entry_set td = new d_entry_set();
                td.setLog_id(c.getInt(c.getColumnIndex("log_id")));
                td.setChip_number((c.getInt(c.getColumnIndex("chip_number"))));
                td.setEntry_value(c.getInt(c.getColumnIndex("entry_value")));
                td.setEntry_id(c.getInt(c.getColumnIndex("entry_id")));
                td.setDivided_by(c.getInt(c.getColumnIndex("divided_by")));

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
                "SET chip_number ="+dEntrySet.getChip_number()+",\n" +
                "entry_value ="+dEntrySet.getEntry_value()+",\n" +
                "entry_id ="+dEntrySet.getEntry_id()+"\n"+
                "divided_by ="+dEntrySet.getDivided_by()+"\n"+
                "x ="+dEntrySet.getX()+"\n"+
                "y="+dEntrySet.getY()+"\n"+
                "WHERE log_id=" +dEntrySet.getLog_id();
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(update_sql);
            bReturn = true;
        }
        catch (Exception ex){
            bReturn = false;
        }
        return bReturn;
    }

    /**
     * Deleting a EntrySet
     */
    public boolean deleteEntrySet(int log_id) {
        boolean bReturn;
        String delete_sql = "DELETE FROM entry_set \n"+
                "WHERE log_id=" + log_id;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(delete_sql);
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
            SQLiteDatabase db = this.getReadableDatabase();
            try {
                Cursor cursor = db.rawQuery(countQuery, null);
                count = cursor.getCount();
                cursor.close();
            }
            catch (Exception ex){
                count = 0;
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
                SQLiteDatabase db = this.getWritableDatabase();
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
                SQLiteDatabase db = this.getWritableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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