package com.example.ashraf.bookapp;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<BookItems>> {
    private String REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=1";
    private LoaderManager loaderManager;
    private static int loaded_book_id = 1;
    private myAdapter mAdapter;
    private ProgressBar progressbar;
    private EditText editText;
    ImageButton imageButton;
    ListView listView;
    TextView NoFound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        NoFound = (TextView) findViewById(R.id.no_found);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        editText = (EditText) findViewById(R.id.editText);
        progressbar .setVisibility(View.GONE);
        listView.setEmptyView(NoFound);
        mAdapter = new myAdapter(this, new ArrayList<BookItems>());
        listView.setAdapter(mAdapter);
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            REQUEST_URL = REQUEST_URL + "android";
            loaderManager = getLoaderManager();
            loaderManager.initLoader(loaded_book_id, null, MainActivity.this);
        } else {
            NoFound.setText("check your network connection");
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queried_book = editText.getText().toString();
                if (editText.length() == 0) {
                    editText.setHint(" you should enter data to search");
                }
                else {
                    loaded_book_id += 1;
                    REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        mAdapter.clear();
                        String edited_user_query = queried_book.replaceAll("\\s+", "");
                        REQUEST_URL = REQUEST_URL + edited_user_query;
                        loaderManager = getLoaderManager();
                        loaderManager.initLoader(loaded_book_id, null, MainActivity.this);
                    }
                    else {
                        mAdapter.clear();
                        progressbar .setVisibility(View.GONE);
                        NoFound.setText("no network connection");
                    }

                }

            }
        });
    }
    @Override
    public Loader<List<BookItems>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        return new Asynclass(this, uriBuilder.toString());
    }
    @Override
    public void onLoadFinished(Loader<List<BookItems>> loader, List<BookItems> Books) {
        progressbar .setVisibility(View.GONE);
        mAdapter.clear();
        if (Books != null && !Books.isEmpty()) {
            mAdapter.addAll(Books);
        } else {
            NoFound.setText("there is no results");
        }
    }
    @Override
    public void onLoaderReset(Loader<List<BookItems>> loader) {
     mAdapter.clear();
    }
    class myAdapter extends ArrayAdapter<BookItems> {
        public myAdapter(Activity context, ArrayList<BookItems> touristicalMonuments) {
            super(context ,0 ,touristicalMonuments);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ListView=convertView;
            if(ListView == null) {
                ListView = LayoutInflater.from(getContext()).inflate(R.layout.list_itmes, parent, false);
            }
            BookItems List1= (BookItems) getItem(position);
            TextView Title= (TextView) ListView.findViewById(R.id.title);
            Title.setText(List1.getTitle());
            TextView Author= (TextView) ListView.findViewById(R.id.author);
            Author.setText(List1.getAuthor());
            return ListView;
        }
    }
}
