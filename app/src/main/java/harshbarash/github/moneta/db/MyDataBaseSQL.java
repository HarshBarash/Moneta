package harshbarash.github.moneta.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class MyDataBaseSQL extends SQLiteOpenHelper {
    //db data
    public static final String TABLE_NAME = "coins";
    public static final String CONTENT_AUTHORITY = "harshbarash.github.moneta";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_COINS = "mycoins";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_COINS);
    public static final String DATABASE_NAME = "mymoneta.db";
    public static final int DATABASE_VERSION = 1;
    public static final String _ID = BaseColumns._ID;
    public static final String COLUMN_NOMINAL = "nominal";
    public static final String COLUMN_CONDITION = "condition";
    public static final String COLUMN_IS_MAGNET = "is_magnet";
    public static final String COLUMN_STAMP = "stamp";
    public static final String COLUMN_KANT = "kant";
    public static final String COLUMN_GURT = "gurt";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DESCRIPTION = "description";


    public MyDataBaseSQL(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +"("+
                BaseColumns._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COLUMN_NOMINAL +" TEXT,"+
                COLUMN_CONDITION + " TEXT,"+
                COLUMN_DESCRIPTION +" TEXT,"+
                COLUMN_GURT +" TEXT,"+
                COLUMN_IS_MAGNET +" TEXT,"+
                COLUMN_KANT +" TEXT,"+
                COLUMN_PRICE +" TEXT,"+
                COLUMN_STAMP +" TEXT,"+
                COLUMN_YEAR +" TEXT"+")";
        DB.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int oldVersion, int newVersion) {
        DB.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(DB);
    }

    //открыть бд
    public void openDb(){
        SQLiteDatabase DB = this.getWritableDatabase();
    }

    //вставить в бд
    public Boolean insert_data(String nominal, String condition, String description, String gurt, String is_magnet, String kant, String price, String stamp, String year){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOMINAL, nominal);
        contentValues.put(COLUMN_CONDITION, condition);
        contentValues.put(COLUMN_DESCRIPTION, description);
        contentValues.put(COLUMN_GURT, gurt);
        contentValues.put(COLUMN_IS_MAGNET, is_magnet);
        contentValues.put(COLUMN_KANT, kant);
        contentValues.put(COLUMN_PRICE, price);
        contentValues.put(COLUMN_STAMP, stamp);
        contentValues.put(COLUMN_YEAR, year);
        long result = DB.insert(TABLE_NAME, null, contentValues);
        if (result < 0){
            return false;
        }
        else {
            return true;
        }
    }

    //обновить бд
    public Boolean update_data(String nominal, String condition, String description, String gurt, String is_magnet, String kant, String price, String stamp, String year){ //думаю можно отредактировать, но хер его знает
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOMINAL, nominal);
        contentValues.put(COLUMN_CONDITION, condition);
        contentValues.put(COLUMN_DESCRIPTION, description);
        contentValues.put(COLUMN_GURT, gurt);
        contentValues.put(COLUMN_IS_MAGNET, is_magnet);
        contentValues.put(COLUMN_KANT, kant);
        contentValues.put(COLUMN_PRICE, price);
        contentValues.put(COLUMN_STAMP, stamp);
        contentValues.put(COLUMN_YEAR, year);

        Cursor cursor = DB.rawQuery("SELECT * FROM "+TABLE_NAME + " WHERE "+ COLUMN_DESCRIPTION + " =? ", new String[]{description}); //вместо description указать что-нибудь другое, скорее всего последний элемент

        if (cursor.getCount()>0) {

        long result = DB.update(TABLE_NAME, contentValues, null, null); //здесь нужно указать до какого параметра должно происходить обновление

        if (result < 0){
            return false;
        }
        else {
            return true;
        }
        }else {
            return false;
        }
    }

    //удалить из бд
    public Boolean delete_data(String nominal, String condition, String description, String gurt, String is_magnet, String kant, String price, String stamp, String year){ //указать что удалить
        SQLiteDatabase DB = this.getWritableDatabase();


        Cursor cursor = DB.rawQuery("SELECT * FROM "+TABLE_NAME + " WHERE "+ COLUMN_DESCRIPTION + " =? ", new String[]{description}); //вместо description указать что-нибудь другое, скорее всего последний элемент либо ключевой идентификатор для удаления (как тот же номинал или что-то другое).

        if (cursor.getCount()>0) {

            long result = DB.delete(TABLE_NAME, null, null); //здесь

            if (result < 0){
                return false;
            }
            else {
                return true;
            }
        }else {
            return false;
        }
    }

    //получить данные
    public Cursor readData(){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        return  cursor;

    }
}
