package com.youravgjoe.apps.noted;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DBHandler mDbHandler;

    List<Note> mNoteList = new ArrayList<>();

    ListView mNoteListView;
    ArrayAdapter<String> mNoteListAdapter;
    FloatingActionButton mAddFab;
    boolean mLongClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbHandler = new DBHandler(this);
        mNoteList = mDbHandler.getAllNotes();

        mNoteListView = (ListView) findViewById(R.id.noteListView);
        mAddFab = (FloatingActionButton) findViewById(R.id.addFab);
        mLongClicked = false;

        populateNoteList();

        mAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent noteIntent = new Intent(MainActivity.this, NoteActivity.class);
                MainActivity.this.startActivity(noteIntent);
            }
        });

        mNoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Note noteToOpen = mNoteList.get(position);

                Intent noteIntent = new Intent(MainActivity.this, NoteActivity.class);
                noteIntent.putExtra("id", noteToOpen.getId());
                MainActivity.this.startActivity(noteIntent);
            }
        });

//        mNoteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                if (!mLongClicked) {
//                    mLongClicked = true;
//                    mAddFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_white_24dp));
//                } else {
//                    mLongClicked = false;
//                    mAddFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
//                }
//                return true;
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // clear the list and add them all again, in case we deleted one from the note activity
        mNoteList.clear();
        mNoteList = mDbHandler.getAllNotes();

        populateNoteList();
    }

    public void populateNoteList() {
        List<String> noteTitleList = new ArrayList<>();
        List<String> noteContentList = new ArrayList<>(); // this won't be used until I implement a two line list item
        for (Note note : mNoteList) {
            if(note.getTitle().length() < 36) { // this would be better if it weren't hard coded
                noteTitleList.add(note.getTitle());
            } else {
                noteTitleList.add(note.getTitle().substring(0, 35) + "...");
            }
            noteContentList.add(note.getContent());
        }

        mNoteListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noteTitleList);
        mNoteListView.setAdapter(mNoteListAdapter);
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
            // Drop older table if it existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
            // Create new table
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

        // Get a single note
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
}
