package com.example.projekt.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.projekt.entity.Event;
import com.example.projekt.entity.Repertoire;
import com.example.projekt.entity.Spektakle;

import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "id11454578_ldz_musictheatre";

    // Login table name
    private static final String TABLE_LOGIN = "login";

    private static final String TABLE_EVENTS = "events";

    private static final String TABLE_SPECTACLE = "spectacle";

    private static final String TABLE_REPERTOIRE = "repertoire";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_UID = "uid";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CREATED_AT = "created_at";

    private static final String KEY_ID_EVENT = "event_id";
    private static final String KEY_NAME_EVENT = "name_event";
    private static final String KEY_SDESC_EVENT = "sdesc_event";
    private static final String KEY_DESC_EVENT = "desc_event";

    private static final String KEY_ID_SPECTACLE = "spectacle_id";
    private static final String KEY_NAME_SPECTACLE = "name_event";
    private static final String KEY_DESC_SPECTACLE = "desc_spectacle";
    private static final String KEY_PREMIERE_DATE = "premiere_date";
    private static final String KEY_IMAGE_SPECTACLE = "image_spectacle";

    private static final String KEY_ID_REPERTOIRE = "repertoire_id";
    private static final String KEY_NAME_REPERTOIRE = "name_repertoire";
    private static final String KEY_DATE_REPERTOIRE = "repertoire_date";
    private static final String KEY_TAG_REPERTOIRE = "tag_spectacle";



    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Table Create Statements
    private static final String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_UID + " TEXT,"
            + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE,"
            + KEY_CREATED_AT + " TEXT" + ")";

    private static final String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
            + KEY_ID_EVENT + " TEXT,"
            + KEY_NAME_EVENT + " TEXT,"
            + KEY_SDESC_EVENT + " TEXT,"
            + KEY_DESC_EVENT + " TEXT" + ")";

    private static final String CREATE_SPECTACLE_TABLE = "CREATE TABLE " + TABLE_SPECTACLE + "("
            + KEY_ID_SPECTACLE + " TEXT,"
            + KEY_NAME_SPECTACLE + " TEXT,"
            + KEY_DESC_SPECTACLE + " TEXT,"
            + KEY_PREMIERE_DATE + " TEXT,"
            + KEY_IMAGE_SPECTACLE + " TEXT" + ")";

    private static final String CREATE_REPERTOIRE_TABLE = "CREATE TABLE " + TABLE_REPERTOIRE + "("
            + KEY_ID_REPERTOIRE + " TEXT,"
            + KEY_NAME_REPERTOIRE + " TEXT,"
            + KEY_DATE_REPERTOIRE + " TEXT,"
            + KEY_TAG_REPERTOIRE + " TEXT"+ ")";

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_EVENT_TABLE);
        db.execSQL(CREATE_SPECTACLE_TABLE);
        db.execSQL(CREATE_REPERTOIRE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPECTACLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPERTOIRE);
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String uid, String name, String email, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UID, uid); // uid
        values.put(KEY_NAME, name); // FirstName
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }
    /**
     * Storing events details in database
     * */
    public void addEvent(String id, String name, String sdesc, String desc) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_EVENT, id); // id
        values.put(KEY_NAME_EVENT, name); // name
        values.put(KEY_SDESC_EVENT, sdesc); //short description
        values.put(KEY_DESC_EVENT, desc); // description
        // Inserting Row
        db.insert(TABLE_EVENTS, null, values);
        db.close(); // Closing database connection
    }

    /**
     * Storing spectacles details in database
     * */
    public void addSpectacle(String id, String name, String desc, String date, String imgUrl) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_SPECTACLE, id); // id
        values.put(KEY_NAME_SPECTACLE, name); // name
        values.put(KEY_DESC_SPECTACLE, desc); // description
        values.put(KEY_PREMIERE_DATE, date); // date
        values.put(KEY_IMAGE_SPECTACLE, imgUrl); // imageURL

        // Inserting Row
        db.insert(TABLE_SPECTACLE, null, values);
        db.close(); // Closing database connection
    }

    /**
     * Storing spectacles details in database
     * */
    public void addRepertoire(String id, String name, String date, String tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_REPERTOIRE, id); // id
        values.put(KEY_NAME_REPERTOIRE, name); // name
        values.put(KEY_DATE_REPERTOIRE, date); // date
        values.put(KEY_TAG_REPERTOIRE, tag); // tag

        // Inserting Row
        db.insert(TABLE_REPERTOIRE, null, values);
        db.close(); // Closing database connection
    }

    /**
     * Getting user data from database
     * */

    public HashMap<String, String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put("uid", cursor.getString(1));
            user.put("name", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        return user;
    }

    /**
     * Getting event data from database
     * */

    public HashMap<Integer, Event> getEventsDetail(){
        HashMap<Integer, Event> events = new HashMap<>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        int tempID = 0;
        do
        {
        //if(cursor.getCount() > 0){
            String id =  cursor.getString(0);
            String name = cursor.getString(1);
            String sdesc = cursor.getString(2);
            String desc = cursor.getString(3);
            events.put(tempID, new Event((Long.parseLong(id)), name, sdesc,desc));
            tempID++;
        } while (cursor.moveToNext());
        cursor.close();
        db.close();
        // return events
        return events;
    }

    /**
     * Getting event data from database
     * */

    public HashMap<Integer, Spektakle> getSpectaclesDetail(){
        HashMap<Integer, Spektakle> spectacles = new HashMap<>();
        String selectQuery = "SELECT * FROM " + TABLE_SPECTACLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        int tempID = 0;
        do
        {
            //if(cursor.getCount() > 0){
            String id =  cursor.getString(0);
            String name = cursor.getString(1);
            String desc = cursor.getString(2);
            String date = cursor.getString(3);
            String imgURL = cursor.getString(4);

            spectacles.put(tempID, new Spektakle((Long.parseLong(id)), name,desc, date, imgURL));
            tempID++;
        } while (cursor.moveToNext());
        cursor.close();
        db.close();
        // return events
        return spectacles;
    }


    /**
     * Getting repertoire data from database
     * */

    public HashMap<Integer, Repertoire> getRepertoireDetail(){
        HashMap<Integer, Repertoire> spectacles = new HashMap<>();
        String selectQuery = "SELECT * FROM " + TABLE_REPERTOIRE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int tempID = 0;
        do
        {
            String id =  cursor.getString(0);
            String name = cursor.getString(1);
            String date = cursor.getString(2);
            String tag = cursor.getString(3);

            spectacles.put(tempID, new Repertoire((Long.parseLong(id)), name, date, tag));
            tempID++;
        } while (cursor.moveToNext());
        cursor.close();
        db.close();

        return spectacles;
    }
    /**
     * Getting repertoire data from database where TAG is ...
     * */
    public HashMap<Integer, Repertoire> getRepertoireDetail(String Tag){
        HashMap<Integer, Repertoire> spectacles = new HashMap<>();
        String selectQuery = "SELECT * FROM " + TABLE_REPERTOIRE + " WHERE " + KEY_TAG_REPERTOIRE + " = " + Tag;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int tempID = 0;
        if(cursor.getCount() > 0) {
            do {

                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String date = cursor.getString(2);
                String tag = cursor.getString(3);

                spectacles.put(tempID, new Repertoire((Long.parseLong(id)), name, date, tag));
                tempID++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return spectacles;
    }

    /**
     * Getting user login status
     * return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Recreate database
     * Delete all tables and create them again
     * */
    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();
    }

}
