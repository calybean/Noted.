package com.youravgjoe.apps.noted;

import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

public class NoteActivity extends AppCompatActivity {

    EditText titleEditText;
    EditText contentEditText;

    String title;
    String content;
    int size;


    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        contentEditText = (EditText) findViewById(R.id.contentEditText);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title = extras.getString("title");
            size = extras.getInt("size");
        }

        actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {

        title = titleEditText.getText().toString();
        content = contentEditText.getText().toString();

        Intent noteIntent = new Intent(NoteActivity.this, MainActivity.class);
        noteIntent.putExtra("title", title);
        noteIntent.putExtra("content", content);
        noteIntent.putExtra("size", size);
        NoteActivity.this.startActivity(noteIntent);

        super.onBackPressed();
    }
}
