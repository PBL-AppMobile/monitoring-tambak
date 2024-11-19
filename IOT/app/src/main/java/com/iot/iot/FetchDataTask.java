package com.iot.iot;

import android.os.AsyncTask;

public class FetchDataTask extends AsyncTask<Void, Void, String> {

    private final FetchDataCallback callback;

    public interface FetchDataCallback {
        void onDataFetched(String result);
    }

    public FetchDataTask(FetchDataCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... voids) {
        // Implementasi kode untuk mengambil data dari server
        // Misalnya, HTTP request untuk mendapatkan JSON dari server
        // Return hasilnya sebagai String
        return null;  // Ganti dengan data yang diambil dari server
    }

    @Override
    protected void onPostExecute(String result) {
        if (callback != null) {
            callback.onDataFetched(result);
        }
    }
}
