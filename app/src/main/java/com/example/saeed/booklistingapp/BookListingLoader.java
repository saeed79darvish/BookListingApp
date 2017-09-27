package com.example.saeed.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class BookListingLoader extends AsyncTaskLoader<List<BookListing>> {

    private static final String LOG_TAG = BookListingLoader.class.getName();
    private String mUrl;

    public BookListingLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<BookListing> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<BookListing> booklistings = QueryUtils.fetchBooklistingData(mUrl);
        return booklistings;
    }
}


