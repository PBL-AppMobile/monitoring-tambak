package com.iot.iot;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class InfluxDBResponse {
    @SerializedName("results")
    private List<InfluxDBResult> results;

    public List<InfluxDBResult> getResults() {
        return results;
    }

    public void setResults(List<InfluxDBResult> results) {
        this.results = results;
    }
}

class InfluxDBResult {
    @SerializedName("series")
    private List<InfluxDBSeries> series;

    public List<InfluxDBSeries> getSeries() {
        return series;
    }

    public void setSeries(List<InfluxDBSeries> series) {
        this.series = series;
    }
}

class InfluxDBSeries {
    @SerializedName("columns")
    private List<String> columns;

    @SerializedName("values")
    private List<List<Object>> values;

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<List<Object>> getValues() {
        return values;
    }

    public void setValues(List<List<Object>> values) {
        this.values = values;
    }
}

