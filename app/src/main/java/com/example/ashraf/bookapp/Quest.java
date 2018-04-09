package com.example.ashraf.bookapp;
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
public class Quest {
    private static HttpURLConnection urlConnection = null;
    private static final String log_cat = Quest.class.getSimpleName();
    public static List<BookItems> fetch(String request) {
        URL url = converter(request);
        InputStream inputs = null;
        String jsonResponse = null;
        try {
            inputs = makeHttpRequest(url);
            jsonResponse = readFromStream(inputs);
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputs != null) {
                inputs.close();
            }
            if (inputs != null) {
                inputs.close();
            }
        } catch (IOException e) {
            Log.e(log_cat, "Problem making the HTTP request.", e);
        }
        List<BookItems> Books = extractFeatureFromJson(jsonResponse);
        return Books;
    }
    public static URL converter(String ul) {
        URL url = null;
        try {
            url = new URL(ul);
        } catch (MalformedURLException e) {
            Log.e(log_cat, "there is a problem in converting url", e);
        }
        return url;
    }
    public static InputStream makeHttpRequest(URL url) throws IOException {
        InputStream inputStream = null;
        if (url == null) {
            return inputStream;
        }
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
            } else {
                Log.e(log_cat, "Error in connection: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(log_cat, "there is a problem in getting json.", e);
        }
        return inputStream;
    }
    private static  String readFromStream(InputStream inputStream) throws IOException {
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
    public static List<BookItems> extractFeatureFromJson(String BookJSON) {
        if (TextUtils.isEmpty(BookJSON)) {
            return null;
        }
        List<BookItems> Books = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(BookJSON);
            JSONArray BookArray = baseJsonResponse.getJSONArray("items");
            for (int i = 0; i < BookArray.length(); i++) {
                JSONObject currentBook = BookArray.getJSONObject(i);
                JSONObject bookVolumeInfo = currentBook.getJSONObject("volumeInfo");
                String title;
                String exact_author;
                title = bookVolumeInfo.getString("title");
                JSONArray authorsArr;
                StringBuilder author = new StringBuilder();
                if (bookVolumeInfo.has("authors")) {
                    authorsArr = bookVolumeInfo.getJSONArray("authors");
                    for (int f = 0; f < authorsArr.length(); f++) {
                        author.append(authorsArr.getString(f));
                        if (f != authorsArr.length() - 1) {
                            author.append(",");
                        } else {
                            author.append(".");
                        }
                    }
                } else {
                    author.append(" there is No Authors");
                }
                exact_author = String.valueOf(author);
               BookItems book = new BookItems( exact_author,title);
                Books.add(book);
            }
        } catch (JSONException e) {
            Log.e("Quest", "Problem parsing the BookApp JSON results", e);
        }
        return Books;
    }
}
