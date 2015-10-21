package com.youravgjoe.apps.noted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> noteTitleArray = new ArrayList<>();
    List<String> noteContentArray = new ArrayList<>();

    String[] testArray = {"test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test"};

    ListView noteList;
    ArrayAdapter<String> noteListAdapter;
    AddFloatingActionButton addFab;

    String titlePref;
    String contentPref;
    int newNotePosition;
    String newNoteTitle;
    String newNoteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noteList = (ListView) findViewById(R.id.noteListView);
        addFab = (AddFloatingActionButton) findViewById(R.id.addFab);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            newNotePosition = extras.getInt("size");
            newNoteTitle = extras.getString("title");
            newNoteContent = extras.getString("content");

            noteTitleArray.clear();
            noteTitleArray.addAll(readPref(titlePref));
            noteTitleArray.add(newNoteTitle); //add the new meal to the day's List of meals
            writePref(titlePref, noteTitleArray);

            noteContentArray.clear();
            noteContentArray.addAll(readPref(contentPref));
            noteContentArray.add(newNoteContent); //add the new meal to the day's List of meals
            writePref(contentPref, noteContentArray); //output the updated list to the day's shared pref
            if(noteTitleArray != null)
            {
                populateNoteList(); //repopulate the day's list of meals
            }
        }

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteTitleArray.add(noteTitleArray.size(), "Note " + noteTitleArray.size());
                Intent noteIntent = new Intent(MainActivity.this, NoteActivity.class);
                noteIntent.putExtra("title", noteTitleArray.get(noteTitleArray.size() - 1)); //pass the note name (note number, basically)
                noteIntent.putExtra("size", noteTitleArray.size()); //pass the note list size, to be used as the note position
                MainActivity.this.startActivity(noteIntent);
            }
        });
    }

    public void populateNoteList()
    {
        noteListAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.content_main, R.id.noteTextView, noteTitleArray);
        noteList.setAdapter(noteListAdapter);

//        noteListAdapter = new ArrayAdapter<>(this, R.layout.content_main, R.id.noteTextView, testArray);
//        noteList.setAdapter(noteListAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public List<String> readPref(String prefName)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(prefName, MODE_PRIVATE);
        HashSet<String> hashSet = (HashSet<String>) sharedPreferences.getStringSet(prefName, new HashSet<String>());
        return new ArrayList<>(hashSet);
    }

    public void writePref(String prefName, List<String> values)
    {
        HashSet<String> hashSet = new HashSet<>(values);
        SharedPreferences sharedPreferences = getSharedPreferences(prefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(prefName, hashSet);
        editor.apply();
    }
}
