package com.youravgjoe.apps.noted;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcannon on 8/9/16.
 */

public class DBHandler2 extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notesInfo";
    private static final String TABLE_NOTES = "notes";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";


    public DBHandler2(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT," + KEY_CONTENT + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        // Creating tables again
        onCreate(db);
    }

    // Add a note to the db
    public void addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_CONTENT, note.getContent());

        // Insert Row
        db.insert(TABLE_NOTES, null, values);
        db.close(); // Closing database connection
    }

    // Get a note from the db
    public Note getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, new String[] { KEY_ID, KEY_TITLE,
                KEY_CONTENT }, KEY_ID + "=?", new String[] { String.valueOf(id) },
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Note note = new Note(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));

        cursor.close();

        return note;
    }

    // Get all notes
    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NOTES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // loop through all rows and add them to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setContent(cursor.getString(2));

                // Add note to list
                noteList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return noteList;
    }

    // Get notes count
    public int getNotesCount() {
        String countQuery = "SELECT * FROM " + TABLE_NOTES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    // Update a note
    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_CONTENT, note.getContent());

        // updating row
        return db.update(TABLE_NOTES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    // Delete a note
    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });
        db.close();
    }
}