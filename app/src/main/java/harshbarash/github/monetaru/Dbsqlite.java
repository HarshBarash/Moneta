package harshbarash.github.monetaru;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import harshbarash.github.monetaru.Moneta.CoinEntry;


//класс - работа с БД
public class Dbsqlite extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "localmoneta.db";
    public static final int DATABASE_VERSION = 1;

    public Dbsqlite(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_TABLE = " CREATE TABLE IF NOT EXISTS " + CoinEntry.TABLE_NAME + " ("
                + CoinEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CoinEntry.COLUMN_NOMINAL + " TEXT NOT NULL, "
                + CoinEntry.COLUMN_CONDITION + " TEXT NOT NULL, "
                + CoinEntry.COLUMN_MAGNET + " TEXT NOT NULL, "
                + CoinEntry.COLUMN_STAMP + " TEXT NOT NULL, "
                + CoinEntry.COLUMN_TYPE + " TEXT NOT NULL, "
                + CoinEntry.COLUMN_MATERIAL + " TEXT NOT NULL, "
                + CoinEntry.COLUMN_YEAR + " TEXT NOT NULL, "
                + CoinEntry.COLUMN_PRICE + " TEXT NOT NULL, "
                + CoinEntry.COLUMN_DESCRIPTION + " TEXT );";

        db.execSQL(SQL_TABLE);


    }

    //обновление базы данных (Был фикс проблемы)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //открыть бд
    public void openDb() {
        SQLiteDatabase DB = this.getWritableDatabase();
    }

    //закрыть бд
    public void closeDb() {
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.close();
    }

    //вставить в бд
    public Boolean insert_data(String nominal, String condition, String description, String type, String is_magnet, String material, String price, String stamp, String year) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CoinEntry.COLUMN_NOMINAL, nominal);
        contentValues.put(CoinEntry.COLUMN_CONDITION, condition);
        contentValues.put(CoinEntry.COLUMN_DESCRIPTION, description);
        contentValues.put(CoinEntry.COLUMN_TYPE, type);
        contentValues.put(CoinEntry.COLUMN_MAGNET, is_magnet);
        contentValues.put(CoinEntry.COLUMN_MATERIAL, material);
        contentValues.put(CoinEntry.COLUMN_PRICE, price);
        contentValues.put(CoinEntry.COLUMN_STAMP, stamp);
        contentValues.put(CoinEntry.COLUMN_YEAR, year);
        long result = DB.insert(CoinEntry.TABLE_NAME, null, contentValues);
        if (result < 0) {
            return false;
        } else {
            return true;
        }
    }

    //обновить бд
    public Boolean update_data(String id, String nominal, String condition, String description, String type, String is_magnet, String material, String price, String stamp, String year) { //думаю можно отредактировать, но хер его знает
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CoinEntry.COLUMN_NOMINAL, nominal);
        contentValues.put(CoinEntry.COLUMN_CONDITION, condition);
        contentValues.put(CoinEntry.COLUMN_DESCRIPTION, description);
        contentValues.put(CoinEntry.COLUMN_TYPE, type);
        contentValues.put(CoinEntry.COLUMN_MAGNET, is_magnet);
        contentValues.put(CoinEntry.COLUMN_MATERIAL, material);
        contentValues.put(CoinEntry.COLUMN_PRICE, price);
        contentValues.put(CoinEntry.COLUMN_STAMP, stamp);
        contentValues.put(CoinEntry.COLUMN_YEAR, year);

        Cursor cursor = DB.rawQuery("SELECT * FROM " + CoinEntry.TABLE_NAME + " WHERE " + CoinEntry._ID + " =? ", new String[]{id}); //вместо description указать что-нибудь другое, скорее всего последний элемент

        if (cursor.getCount() > 0) {

            long result = DB.update(CoinEntry.TABLE_NAME, contentValues, CoinEntry._ID + " =?", new String[]{id}); //здесь нужно указать до какого параметра должно происходить обновление

            if (result < 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    //удалить из бд
    public Boolean delete_data(String description) { //указать откуда начинать удалять
        SQLiteDatabase DB = this.getWritableDatabase();


        Cursor cursor = DB.rawQuery("SELECT * FROM " + CoinEntry.TABLE_NAME + " WHERE " + CoinEntry.COLUMN_DESCRIPTION + " =? ", new String[]{description}); //вместо description указать что-нибудь другое, скорее всего последний элемент либо ключевой идентификатор для удаления (как тот же номинал или что-то другое).

        if (cursor.getCount() > 0) {

            long result = DB.delete(CoinEntry.TABLE_NAME, "description=?", new String[]{description}); //здесь

            if (result < 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    //получить данные
    public Cursor readData() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM " + Moneta.CoinEntry.TABLE_NAME, null);
        return cursor;

    }


}
