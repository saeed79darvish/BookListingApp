package com.example.saeed.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookListingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<BookListing>> {

    private static final String LOG_TAG = BookListingActivity.class.getName();
    private static final String BOOKLISTING_JSON = "https://www.googleapis.com/books/v1/volumes?maxResults=30&orderBy=newest&q=";
    private static final int BOOKLISTING_LOADER_ID = 1;
    private BookListingAdapter adapter;
    private TextView emptyTextView;
    private ProgressBar loadingIndicator;
    private EditText queryEditText;
    LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_listing);
        loaderManager = getLoaderManager();
        ListView booklistingListView = (ListView) findViewById(R.id.list);
        queryEditText = (EditText) findViewById(R.id.search_edit_text);
        emptyTextView = (TextView) findViewById(R.id.empty_textview);
        booklistingListView.setEmptyView(emptyTextView);
        loadingIndicator = (ProgressBar) findViewById(R.id.progressbar_indicator);
        adapter = new BookListingAdapter(this, new ArrayList<BookListing>());
        booklistingListView.setAdapter(adapter);

        queryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isOnline()) {
                    LoaderManager loaderManager = getLoaderManager();
                    Bundle args = new Bundle();
                    args.putString("QUERY", s.toString());
                    loaderManager.initLoader(BOOKLISTING_LOADER_ID, args, BookListingActivity.this);
                    loaderManager.restartLoader(BOOKLISTING_LOADER_ID, args, BookListingActivity.this);
                } else {
                    View loadingIndicator = findViewById(R.id.progressbar_indicator);
                    loadingIndicator.setVisibility(View.GONE);
                    emptyTextView.setText(R.string.error_connection);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public Loader<List<BookListing>> onCreateLoader(int i, Bundle bundle) {
        String requestUrl = "";
        String mQuery = bundle.getString("QUERY");
        if (mQuery != null && mQuery != "") {
            requestUrl = BOOKLISTING_JSON + mQuery;
        } else {
            String defaultQuery = "android";
            requestUrl = BOOKLISTING_JSON + defaultQuery;
        }
        return new BookListingLoader(this, requestUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<BookListing>> loader, List<BookListing> books) {
        View loadingIndicator = findViewById(R.id.progressbar_indicator);
        loadingIndicator.setVisibility(View.GONE);
        adapter.clear();
        if (books != null && !books.isEmpty()) {
            adapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<BookListing>> loader) {
        adapter.clear();
    }
}
