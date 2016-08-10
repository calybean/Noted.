package com.youravgjoe.apps.noted;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class NoteActivity extends AppCompatActivity {

    EditText titleEditText;
    EditText contentEditText;
    ActionBar actionBar;

    int mId;
    Note mNote;
    DBHandler mDbHandler;
    boolean mNoteIsAlreadyInDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        contentEditText = (EditText) findViewById(R.id.contentEditText);

        mDbHandler = new DBHandler(this);

        // get the note info from when they click a list item
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // if the note already exists, get it from the db
            mId = extras.getInt("id");
            mNote = mDbHandler.getNote(mId);
            mNoteIsAlreadyInDb = true;

            // populate the edit texts with title and content
            titleEditText.setText(mNote.getTitle());
            contentEditText.setText(mNote.getContent());

            // set cursor to end of title
            titleEditText.setSelection(titleEditText.getText().length());
        } else {
            // if it doesn't exist, create a blank note
            mId = mDbHandler.getNotesCount();
            mNote = new Note(mId);
            mNoteIsAlreadyInDb = false;
        }

        // show soft keyboard when activity is loaded
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // hide soft keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;
            case R.id.action_delete:
                // check to make sure the title and content aren't both empty
                if(!(TextUtils.equals(mNote.getTitle(), null) && TextUtils.equals(mNote.getContent(), null))) {
                    Log.d("joey", mNote.getTitle() + " " + mNote.getContent());
                    // for later: make an alert "ARE YOU SURE?" dialog
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setMessage("Are you sure you want to delete this note?");
                    alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDbHandler.deleteNote(mNote);
                                    finish();
                                }
                            });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing?
                                }
                            });
                    alert.show();

                    // hide soft keyboard for aesthetic purposes.
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                } else {
                    finish();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        mNote.setTitle(titleEditText.getText().toString());
        mNote.setContent(contentEditText.getText().toString());

        if(mNoteIsAlreadyInDb) {
            // check to make sure the title and content aren't both empty
            if(!(TextUtils.equals(mNote.getTitle(), "") && TextUtils.equals(mNote.getContent(), ""))) {
                // if the note is already in the db, update it
                mDbHandler.updateNote(mNote);
            } else { // if it is empty, delete the note.
                mDbHandler.deleteNote(mNote);
            }
        } else {
            // check to make sure the title and content aren't both empty
            if(!(TextUtils.equals(mNote.getTitle(), "") && TextUtils.equals(mNote.getContent(), ""))) {
                // if the note is new, add it to the db
                mDbHandler.addNote(mNote);
            }
        }

        super.onBackPressed();
    }

    public class DBHandler extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "notesInfo";
        private static final String TABLE_NOTES = "notes";
        private static final String KEY_ID = "id";
        private static final String KEY_TITLE = "title";
        private static final String KEY_CONTENT = "content";


        public DBHandler(Context context) {
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
            int count = cursor.getCount();
            cursor.close();
            return count;
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
}
