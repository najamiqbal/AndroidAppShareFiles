package com.example.sharefiles;

public class HistoryModel {
    public static final String TABLE_NAME = "history";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_URL = "url_path";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String urlpath;
    private String timestamp;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_URL + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public HistoryModel() {

    }

    public HistoryModel(int id, String urlpath, String timestamp) {
        this.id = id;
        this.urlpath = urlpath;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrlpath() {
        return urlpath;
    }

    public void setUrlpath(String urlpath) {
        this.urlpath = urlpath;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}


