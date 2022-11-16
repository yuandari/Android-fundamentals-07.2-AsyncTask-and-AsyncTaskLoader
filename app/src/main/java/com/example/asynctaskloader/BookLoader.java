package com.example.asynctaskloader;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

public class BookLoader extends Loader<String> {
    private String mQueryString;

    public BookLoader(@NonNull Context context, String mQueryString) {
        super(context);
        this.mQueryString = mQueryString;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    public String loadInBackground() {
        return NetworkUtils.getBookInfo(mQueryString);
    }
}