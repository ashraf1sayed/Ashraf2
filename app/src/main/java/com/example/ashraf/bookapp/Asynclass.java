package com.example.ashraf.bookapp;
import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;
public class Asynclass extends AsyncTaskLoader<List<BookItems>> {
    String Url;
    public Asynclass(Context context, String url) {
        super(context);
        Url = url;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }
    @Override
    public List<BookItems> loadInBackground() {
        if (Url == null) {
            return null;
        }
        List<BookItems> books = Quest.fetch(Url);
        return books;
    }
}
