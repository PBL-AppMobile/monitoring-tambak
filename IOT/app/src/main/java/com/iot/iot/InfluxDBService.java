package com.iot.iot;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface InfluxDBService {
    @GET("query")
    Call<InfluxDBResponse> getData(
            @Query("q") String query, // Query yang dikirim ke InfluxDB
            @Query("db") String dbName // Nama database
    );
}

