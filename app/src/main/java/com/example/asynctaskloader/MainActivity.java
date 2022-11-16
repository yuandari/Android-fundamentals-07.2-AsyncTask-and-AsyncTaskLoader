package com.example.asynctaskloader;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private EditText mBookInput;
    private TextView mTitleText;
    private TextView mAuthorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookInput = findViewById(R.id.bookInput);
        mTitleText = findViewById(R.id.titleText);
        mAuthorName = findViewById(R.id.authorText);

        if(getSupportLoaderManager().getLoader(0)!=null){
            getSupportLoaderManager().initLoader(0,null,this);
        }
    }

    public void searchBooks(View view) {
        String queryText = mBookInput.getText().toString();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected() && queryText.length() != 0) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryText);
            getSupportLoaderManager().restartLoader(0, queryBundle, this);
            mAuthorName.setText("");
            mTitleText.setText(R.string.loading);
        }
        else {
            if (queryText.length() == 0) {
                mAuthorName.setText("");
                mTitleText.setText(R.string.no_search_term);
            } else {
                mAuthorName.setText("");
                mTitleText.setText(R.string.no_network);
            }
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String queryString = null;
        if (args != null) {
            queryString = args.getString("queryString");
        }
        return new BookLoader(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            int i = 0;
            String title = null;
            String author = null;

            while (i < itemsArray.length() && (title == null && author == null)) {
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = jsonObject.getJSONObject("volumeInfo");

                try {
                    title = volumeInfo.getString("title");
                    author = volumeInfo.getString("authors");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                i++;
            }

            if (title != null && author != null) {
                mTitleText.setText(title);
                mAuthorName.setText(author);
            } else {
                mTitleText.setText(R.string.no_results);
                mAuthorName.setText("");
            }
        } catch (JSONException e) {
            mTitleText.setText(R.string.no_results);
            mAuthorName.setText("");
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}