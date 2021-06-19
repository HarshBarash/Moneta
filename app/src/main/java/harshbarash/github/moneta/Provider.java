package harshbarash.github.moneta;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class Provider extends ContentProvider {

    public static final int COINS = 100;
    public static final int COINS_ID = 101;
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(Moneta.CONTENT_AUTHORITY, Moneta.PATH_COINS, COINS);
        sUriMatcher.addURI(Moneta.CONTENT_AUTHORITY, Moneta.PATH_COINS + "/#", COINS_ID);

    }

    public Dbsqlite mDbsqlite;


    @Override
    public boolean onCreate() {
        mDbsqlite = new Dbsqlite(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbsqlite.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case COINS:
                cursor = database.query(Moneta.CoinEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case COINS_ID:

                selection = Moneta.CoinEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(Moneta.CoinEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Выберите" + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int math = sUriMatcher.match(uri);
        switch (math) {
            case COINS:;
                return insertCoin(uri, values);

            default:
                throw new IllegalArgumentException("Невозможно вставить" + uri);
        }
    }

    private Uri insertCoin(Uri uri, ContentValues values) {
        String type = values.getAsString(Moneta.CoinEntry.COLUMN_NOMINAL);
        if (type == null || !Moneta.CoinEntry.isValidType(type)) {
            throw new IllegalArgumentException("Выберите значение");
        }

        String type2 = values.getAsString(Moneta.CoinEntry.COLUMN_CONDITION);
        if (type2 == null || !Moneta.CoinEntry.isValidType2(type2)) {
            throw new IllegalArgumentException("Выберите значение");
        }

        String type3 = values.getAsString(Moneta.CoinEntry.COLUMN_MAGNET);
        if (type3 == null || !Moneta.CoinEntry.isValidType3(type3)) {
            throw new IllegalArgumentException("Выберите значение");
        }

        String type4 = values.getAsString(Moneta.CoinEntry.COLUMN_STAMP);
        if (type4 == null || !Moneta.CoinEntry.isValidType4(type4)) {
            throw new IllegalArgumentException("Выберите значение");
        }

        String type5 = values.getAsString(Moneta.CoinEntry.COLUMN_TYPE);
        if (type5 == null || !Moneta.CoinEntry.isValidType5(type5)) {
            throw new IllegalArgumentException("Выберите значение");
        }

        String type6 = values.getAsString(Moneta.CoinEntry.COLUMN_MATERIAL);
        if (type6 == null || !Moneta.CoinEntry.isValidType6(type6)) {
            throw new IllegalArgumentException("Выберите значение");
        }

        String year = values.getAsString(Moneta.CoinEntry.COLUMN_YEAR);
        if (year == null) {
            throw new IllegalArgumentException("Введите год");
        }

        String price = values.getAsString(Moneta.CoinEntry.COLUMN_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Введите цену или поставье '-'");
        }

        String description = values.getAsString(Moneta.CoinEntry.COLUMN_DESCRIPTION);
        if (description == null) {
            throw new IllegalArgumentException("Введите описание");
        }

        SQLiteDatabase database = mDbsqlite.getWritableDatabase();
        long id = database.insert(Moneta.CoinEntry.TABLE_NAME, null, values);

        if (id == -1) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
         int rowsDeleted;
         SQLiteDatabase database = mDbsqlite.getWritableDatabase();
         int match = sUriMatcher.match(uri);
         switch (match) {
             case COINS:
                 rowsDeleted = database.delete(Moneta.CoinEntry.TABLE_NAME, selection, selectionArgs);
                 break;

             case COINS_ID:
                 selection = Moneta.CoinEntry._ID + "=?";
                 selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                 rowsDeleted = database.delete(Moneta.CoinEntry.TABLE_NAME, selection, selectionArgs);
                 break;

             default:
                 throw new IllegalArgumentException("Невозможно удалить" + uri);


         }

         if (rowsDeleted!=0) {
             getContext().getContentResolver().notifyChange(uri, null);
         }
         return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,  String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case COINS:
                return updateCoin(uri, values, selection, selectionArgs);

            case COINS_ID:

                selection = Moneta.CoinEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateCoin(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Невозможно обновить");

        }

    }

    private int updateCoin(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(Moneta.CoinEntry.COLUMN_NOMINAL)) {
            String type = values.getAsString(Moneta.CoinEntry.COLUMN_NOMINAL);
            if (type == null || !Moneta.CoinEntry.isValidType(type)) {
                throw new IllegalArgumentException("Выберите значение");
            }
        }

        if (values.containsKey(Moneta.CoinEntry.COLUMN_CONDITION)) {
            String type2 = values.getAsString(Moneta.CoinEntry.COLUMN_CONDITION);
            if (type2 == null || !Moneta.CoinEntry.isValidType2(type2)) {
                throw new IllegalArgumentException("Выберите значение");
            }
        }

        if (values.containsKey(Moneta.CoinEntry.COLUMN_MAGNET)) {
            String type3 = values.getAsString(Moneta.CoinEntry.COLUMN_MAGNET);
            if (type3 == null || !Moneta.CoinEntry.isValidType3(type3)) {
                throw new IllegalArgumentException("Выберите значение");
            }
        }

        if (values.containsKey(Moneta.CoinEntry.COLUMN_STAMP)) {
            String type4 = values.getAsString(Moneta.CoinEntry.COLUMN_STAMP);
            if (type4 == null || !Moneta.CoinEntry.isValidType4(type4)) {
                throw new IllegalArgumentException("Выберите значение");
            }
        }


        if (values.containsKey(Moneta.CoinEntry.COLUMN_YEAR)) {
            String year = values.getAsString(Moneta.CoinEntry.COLUMN_YEAR);
            if (year == null) {
                throw new IllegalArgumentException("Введите год");
            }
        }

        if (values.containsKey(Moneta.CoinEntry.COLUMN_PRICE)) {
            String price = values.getAsString(Moneta.CoinEntry.COLUMN_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Введите цену или поставье '-'");
            }
        }

        if (values.containsKey(Moneta.CoinEntry.COLUMN_DESCRIPTION)) {
            String description = values.getAsString(Moneta.CoinEntry.COLUMN_DESCRIPTION);
            if (description == null) {
                throw new IllegalArgumentException("Введите описание");
            }
        }

        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbsqlite.getWritableDatabase();
        int roesUpdated = database.update(Moneta.CoinEntry.TABLE_NAME, values, selection, selectionArgs);

        if (roesUpdated !=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return roesUpdated;
     }

}
