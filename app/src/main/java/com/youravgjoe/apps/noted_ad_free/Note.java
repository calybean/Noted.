package com.youravgjoe.apps.noted_ad_free;

/**
 * Created by jcannon on 8/9/16.
 */

public class Note {
    private int id;
    private String title;
    private String content;

    public Note () {
        // empty constructor
    }

    public Note (int _id, String _title, String _content) {
        id= _id;
        title = _title;
        content = _content;
    }

    public Note (int _id) {
        id= _id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setId(int _id) {
        id = _id;
    }

    public void setTitle(String _title) {
        title = _title;
    }

    public void setContent(String _content) {
        content = _content;
    }
}
