package jonas.projektfono;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseHelper extends SQLiteOpenHelper {

    private final String WALLPAPERS = "wallpapers";
    private final String NAME = "name";
    private final String CATEGORY = "category";
    private final String SIZE = "size";

    private SQLiteDatabase database;

    DataBaseHelper(Context context) {
        super(context, "wallpapers.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + WALLPAPERS + " (" + NAME + " TEXT PRIMARY KEY NOT NULL, " + CATEGORY + " TEXT NOT NULL, " + SIZE + " TEXT NOT NULL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WALLPAPERS);
        onCreate(sqLiteDatabase);

    }

    void open() {
        database = getWritableDatabase();
    }

    void insertImgData(String name) {

        String category = "Abstract";
        String size = "672x840";

        ContentValues contentValues = new ContentValues();

        contentValues.put(CATEGORY, category);
        contentValues.put(SIZE, size);
        contentValues.put(NAME, name);

        database.insert(WALLPAPERS, null, contentValues);

    }

    Cursor readFromDB() {

        String[] allColumns = new String[]{NAME, CATEGORY, SIZE};

        Cursor cursor = database.query(WALLPAPERS, allColumns, null, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;

    }

    String readImgCategory(String name) {

        String category = "";

        Cursor cursor = database.rawQuery("SELECT * FROM " + WALLPAPERS + " WHERE " + NAME + " = '" + name + "'", null);

        if (cursor.moveToFirst()) {
            category = cursor.getString(1);
        }

        cursor.close();

        return category;

    }

    String readImgSize(String name) {

        String size = "";

        Cursor cursor = database.rawQuery("SELECT * FROM " + WALLPAPERS + " WHERE " + NAME + " = '" + name + "'", null);

        if (cursor.moveToFirst()) {
            size = cursor.getString(2);
        }

        cursor.close();

        return size;


    }

    String readImgName(String name) {

        String nameToReturn = "";

        Cursor cursor = database.rawQuery("SELECT * FROM " + WALLPAPERS + " WHERE " + NAME + " = '" + name + "'", null);

        if (cursor.moveToFirst()) {
            nameToReturn = cursor.getString(0);
        }

        cursor.close();

        return nameToReturn;


    }

}