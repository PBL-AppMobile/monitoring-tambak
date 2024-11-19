package com.iot.iot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Konfigurasi {

    // Key untuk JSON yang akan diparsing
    public static final String TAG_JSON_ARRAY = "result";  // Key JSON array

    // Key JSON untuk mengambil data spesifik dari item
    public static final String TAG_TANGGAL = "tanggal";       // Key tanggal
    public static final String TAG_JAM = "jam";               // Key jam
    public static final String TAG_PH = "ph";                 // Key pH air
    public static final String TAG_SUHU_AIR = "suhu_air";     // Key suhu air
    public static final String TAG_KUALITAS_AIR = "kualitas_air"; // Key kualitas air

    // Nama produk sebagai contoh key lain jika diperlukan
    public static final String TAG_NAMA_PRODUK = "nama_produk"; // Key nama produk
    public static final String TAG_DESKRIPSI = "deskripsi";     // Key deskripsi produk
    public static final String TAG_STOK = "stok";               // Key stok produk

    // Data dummy
    public static List<HashMap<String, String>> getDummyData() {
        List<HashMap<String, String>> itemList = new ArrayList<>();

        // Data Dummy - 10 Entries
        for (int i = 1; i <= 10; i++) {
            HashMap<String, String> item = new HashMap<>();
            item.put(TAG_TANGGAL, "2024-10-" + (24 - i));           // Tanggal dummy
            item.put(TAG_JAM, "0" + i + ":00");                     // Jam dummy
            item.put(TAG_PH, String.valueOf(7.0 + i * 0.1));        // pH dummy
            item.put(TAG_SUHU_AIR, String.valueOf(24.0 + i * 0.2)); // Suhu air dummy
            item.put(TAG_KUALITAS_AIR, i % 2 == 0 ? "Baik" : "Sedang"); // Kualitas air dummy
            item.put(TAG_NAMA_PRODUK, "Lobster " + i);              // Nama produk dummy
            item.put(TAG_DESKRIPSI, "Deskripsi Lobster " + i);      // Deskripsi dummy
            item.put(TAG_STOK, String.valueOf(i * 5));              // Stok dummy

            itemList.add(item);
        }

        return itemList;
    }
}
