package com.example.saeed.booklistingapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String UNKNOWN_AUTHOR = "Unknown Author";
    private static final String AUTHOR_SEPARATOR = ", ";
    private static final String JSON_KEY_BOOK_AUTHORS = "authors";

    private QueryUtils() {
    }

    public static List<BookListing> fetchBooklistingData(String requestUrl) {

        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<BookListing> booklistings = extractFeatureFromJson(jsonResponse);
        return booklistings;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the booklist JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<BookListing> extractFeatureFromJson(String booklistingJSON) {

        if (TextUtils.isEmpty(booklistingJSON)) {
            return null;
        }
        List<BookListing> booklistings = new ArrayList<>();
        try {

            JSONObject baseJsonResponse = new JSONObject(booklistingJSON);
            if (!baseJsonResponse.has("items")) {
                return null;
            }
            JSONArray booklistingArray = baseJsonResponse.getJSONArray("items");
            for (int i = 0; i < booklistingArray.length(); i++) {
                JSONObject currentBooklisting = booklistingArray.getJSONObject(i);
                JSONObject items = currentBooklisting.getJSONObject("volumeInfo");
                JSONArray authorArray = items.optJSONArray(JSON_KEY_BOOK_AUTHORS);
                ArrayList<String> authors = new ArrayList<>();
                if (null != authorArray && authorArray.length() != 0) {
                    for (int j = 0; j < authorArray.length(); j++) {
                        try {
                            authors.add(authorArray.getString(j));
                        } catch (JSONException e) {
                            authors.add(UNKNOWN_AUTHOR);
                        }
                    }
                } else {
                    authors.add(UNKNOWN_AUTHOR);
                }

                String finalAuthorsString = TextUtils.join(AUTHOR_SEPARATOR, authors);
                String title = items.getString("title");
                BookListing booklisting = new BookListing(finalAuthorsString, title);
                booklistings.add(booklisting);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the booklist JSON results", e);
        }
        return booklistings;
    }
}


