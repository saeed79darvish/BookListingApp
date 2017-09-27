package com.example.saeed.booklistingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BookListingAdapter extends ArrayAdapter<BookListing> {

    public BookListingAdapter(Context context, List<BookListing> booklistings) {
        super(context, 0, booklistings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_listing_item, parent, false);
        }
        BookListing currentBooklisting = getItem(position);
        TextView authorView = (TextView) listItemView.findViewById(R.id.author_item);
        authorView.setText(currentBooklisting.getmAuthor());
        TextView titleView = (TextView) listItemView.findViewById(R.id.title_item);
        titleView.setText(currentBooklisting.getmTitle());
        return listItemView;
    }
}


