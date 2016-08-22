package com.shamdroid.most3000words;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class sqlHelper extends SQLiteOpenHelper {

    private  String DB_PATH = "data/data/com.shamdroid.most3000words/databases/";

    private static String DB_NAME = "longman3000.db";

    public SQLiteDatabase myDataBase;

    private final Context myContext;

    public sqlHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }



    public boolean checkExist() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {


        } catch (Exception ep) {
            ep.printStackTrace();
        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    public void importIfNotExist() throws IOException {

        boolean dbExist = checkExist();

        if (dbExist) {
            // do nothing - database already exist
        } else {

            // By calling this method and empty database will be created into
            // the default system path
            // of your application so we are gonna be able to overwrite that
            // database with our database.
            this.getWritableDatabase();

            try {

                copyDatabase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    private void copyDatabase() throws IOException {
        InputStream is = myContext.getAssets().open(DB_NAME);

        OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);

        byte[] buffer = new byte[4096];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        os.flush();
        os.close();
        is.close();
        this.close();
    }
    public void openDataBase(){

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }




    public Cursor getAll(){
        return myDataBase.rawQuery("select * from words",null);
    }

    public Cursor getData(int id){
        return myDataBase.rawQuery("select * from words where _id = " + id,null);

    }

    public Cursor getAllFav(){
        return myDataBase.rawQuery("select * from words where done = 1 ",null);
    }

    public void addToFav(int id){
        myDataBase = this.getWritableDatabase();
        myDataBase.execSQL("update words set done = 1 where _id = "+id);
    }

    public void removeFromFav(int id){
        myDataBase = this.getWritableDatabase();
        myDataBase.execSQL("update words set done = 0 where _id = "+id);
    }

    public boolean getFav(int id){
        Cursor cursor = myDataBase.rawQuery("select done from words where _id = "+id,null);
        cursor.moveToFirst();
        return cursor.getInt(0) == 1 ? true : false ;
    }

    public int getNum(){
        return (int) DatabaseUtils.queryNumEntries(myDataBase,"words");
    }


}
